package lk.dropti.ninewood.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.dropti.ninewood.dto.OrderDTO;
import lk.dropti.ninewood.dto.ProductDTO;
import lk.dropti.ninewood.dto.UserDTO;
import lk.dropti.ninewood.entity.*;
import lk.dropti.ninewood.util.AppUtil;
import lk.dropti.ninewood.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutService {

    private final String MERCHANT_ID = "1228704";
    private final String MERCHANT_SECRET = "MTE0ODY5Nzk0NTM2NjM3MjE2NTYyMzExNzc3OTUyNjMwMjUxMjcx";
    @SuppressWarnings("unchecked")
    private List<Cart> getSessionCart(HttpSession session) {
        return (List<Cart>) session.getAttribute("sessionCart");
    }


//    public JsonObject processCODOrder(JsonObject orderData, HttpSession httpSession) {
//        JsonObject response = new JsonObject();
//        User user = (User) httpSession.getAttribute("user");
//
//        List<Cart> cartList = new ArrayList<>();
//        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//        Transaction transaction = null;
//
//        try {
//            if (user == null) {
//                List<Cart> sessionCart = getSessionCart(httpSession);
//                if (sessionCart != null) cartList.addAll(sessionCart);
//            } else {
//                cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user.id=:id", Cart.class)
//                        .setParameter("id", user.getId())
//                        .getResultList();
//            }
//
//            if (cartList.isEmpty()) {
//                response.addProperty("status", false);
//                response.addProperty("message", "Cart is empty!");
//                return response;
//            }
//
//            transaction = hibernateSession.beginTransaction();
//
//            double subtotal = 0;
//            boolean hasHeavyItem = false;
//
//            for (Cart cart : cartList) {
//                Stock stock = hibernateSession.find(Stock.class, cart.getStock().getId());
//                subtotal += (cart.getQty() * stock.getPrice());
//
//                if (stock.getProduct().isHeavy()) {
//                    hasHeavyItem = true;
//                }
//            }
//
//            double shippingFee = hasHeavyItem ? 5000.0 : 500.0;
//            double grandTotal = subtotal + shippingFee;
//
//
//            if (user == null) {
//                httpSession.removeAttribute("sessionCart"); // Session එකෙන් අයින් කරනවා
//            } else {
//                hibernateSession.createQuery("DELETE FROM Cart c WHERE c.user.id=:id")
//                        .setParameter("id", user.getId())
//                        .executeUpdate();
//            }
//
//            transaction.commit();
//
//            response.addProperty("status", true);
//            response.addProperty("orderId", "INV-" + System.currentTimeMillis());
//            response.addProperty("message", "Order placed successfully!");
//
//        } catch (Exception e) {
//            if (transaction != null) transaction.rollback();
//            e.printStackTrace();
//            response.addProperty("status", false);
//            response.addProperty("message", e.getMessage());
//        } finally {
//            hibernateSession.close();
//        }
//
//        return response;
//    }

    public JsonObject processCODOrder(JsonObject orderData, HttpSession httpSession) {
        JsonObject responseObject = new JsonObject();
        User user = (User) httpSession.getAttribute("user");

        if (user == null) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Please login to place an order!");
            return responseObject;
        }

        List<Cart> cartList = new ArrayList<>();
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user.id=:id", Cart.class)
                    .setParameter("id", user.getId())
                    .getResultList();

            if (cartList.isEmpty()) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Cart is empty!");
                return responseObject;
            }

            transaction = hibernateSession.beginTransaction();

            double subtotal = 0;
            boolean hasHeavyItem = false;

            // 🎯 1. Calculate pricing and validate heavy items dynamically
            for (Cart cart : cartList) {
                Stock stock = hibernateSession.find(Stock.class, cart.getStock().getId());
                subtotal += (cart.getQty() * stock.getPrice());

                // 💡 දැන් කිසිම බයක් නැතුව ඇත්තම DB Column එකෙන් චෙක් කරනවා මචං
                if (stock.getProduct() != null && stock.getProduct().isHeavy()) {
                    hasHeavyItem = true;
                }
            }

            // 🎯 2. Delivery & Payment Setup (උඹේ පරණ ඩේටාබේස් IDs වල හැටියට)
            // heavy (ID = 1), standard (ID = 2)
            int deliveryTypeId = hasHeavyItem ? 1 : 2;
            double shippingFee = hasHeavyItem ? 5000.0 : 500.0;
            double grandTotal = subtotal + shippingFee;

            Status orderStatus = hibernateSession.find(Status.class, 1); // 1 = Pending

            // ==========================================================
            // 🎯 3. ORDERS TABLE INSERT
            // ==========================================================
            Orders order = new Orders();
            order.setUser(user);
            order.setPrice(grandTotal);
            order.setStatus(orderStatus);
            order.setDeliveryTypesId(deliveryTypeId);
            order.setPaymentTypesId(1); // 1 = COD

            hibernateSession.persist(order);

            // ==========================================================
            // 🎯 4. ORDER_ITEMS INSERT & STOCK REDUCTION
            // ==========================================================
            for (Cart cart : cartList) {
                Stock stock = hibernateSession.find(Stock.class, cart.getStock().getId());

                if (stock.getQty() < cart.getQty()) {
                    throw new Exception("Sorry, product '" + stock.getProduct().getTitle() + "' is out of stock!");
                }

                // A. Stock එක අඩු කරලා Update කරනවා
                stock.setQty(stock.getQty() - cart.getQty());
                stock.setUpdatedAt(java.time.LocalDateTime.now()); // 189 Line LocalDateTime Fix!
                hibernateSession.merge(stock);

                // B. OrderItem එක හැදීම
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setStock(stock);
                orderItem.setQty(cart.getQty());
                orderItem.setRating(null);

                // 💡 100% Dynamic Seller Allotment:
                // ප්‍රොඩක්ට් එක ඇතුළේ ඉන්න ඇත්තම සෙලර්ව අරන් ඕඩර් අයිටම් එකට ලින්ක් කරනවා මචං
                if (stock.getProduct() != null && stock.getProduct().getSeller() != null) {
                    orderItem.setSeller(stock.getProduct().getSeller());
                } else {
                    Seller defaultSeller = hibernateSession.find(Seller.class, 1);
                    orderItem.setSeller(defaultSeller);
                }

                hibernateSession.persist(orderItem);
            }

            // ==========================================================
            // 🎯 5. CLEAR CART
            // ==========================================================
            hibernateSession.createQuery("DELETE FROM Cart c WHERE c.user.id=:id")
                    .setParameter("id", user.getId())
                    .executeUpdate();

            transaction.commit();

            responseObject.addProperty("status", true);
            responseObject.addProperty("orderId", "INV-" + order.getId());
            responseObject.addProperty("message", "Your Cash on Delivery order has been placed successfully!");

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Order Error: " + e.getMessage());
        } finally {
            hibernateSession.close();
        }

        return responseObject;
    }


    public JsonObject processCardPaymentInitiation(JsonObject orderData, HttpSession httpSession) {
        JsonObject response = new JsonObject();
        User user = (User) httpSession.getAttribute("user");

        List<Cart> cartList = new ArrayList<>();
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        try {
            // 🔄 [FIXED LOGIC]: කාර්ට් එක හිස් නොවෙන්න මෙතනටත් සෙස්සන් කාර්ට් ලොජික් එක බස්සනවා
            if (user == null) {
                List<Cart> sessionCart = getSessionCart(httpSession);
                if (sessionCart != null) cartList.addAll(sessionCart);
            } else {
                cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user.id=:id", Cart.class)
                        .setParameter("id", user.getId())
                        .getResultList();
            }

            if (cartList.isEmpty()) {
                response.addProperty("status", false);
                response.addProperty("message", "Cart is empty!");
                return response;
            }

            double subtotal = 0;
            boolean hasHeavyItem = false;
            StringBuilder itemNames = new StringBuilder();

            for (Cart cart : cartList) {
                Stock stock = hibernateSession.find(Stock.class, cart.getStock().getId());
                subtotal += (cart.getQty() * stock.getPrice());
                itemNames.append(stock.getProduct().getTitle()).append(", ");
                if (stock.getProduct().isHeavy()) {
                    hasHeavyItem = true;
                }
            }

            double shippingFee = hasHeavyItem ? 5000.0 : 500.0;
            double grandTotal = subtotal + shippingFee;

            String orderId = orderData.get("orderId").getAsString();

            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00", java.text.DecimalFormatSymbols.getInstance(java.util.Locale.US));
            String amountFormatted = df.format(grandTotal);


            String merchantSecretHash = getMd5(MERCHANT_SECRET).toUpperCase();
            String mainHashSource = MERCHANT_ID + orderId + amountFormatted + "LKR" + merchantSecretHash;
            String finalHash = getMd5(mainHashSource).toUpperCase();


            System.out.println("====== PAYHERE HASH DEBUG ======");
            System.out.println("Using Order ID: " + orderId);
            System.out.println("Using Amount: " + amountFormatted);
            System.out.println("Generated Hash: " + finalHash);
            System.out.println("================================");

            response.addProperty("status", true);
            response.addProperty("merchantId", MERCHANT_ID);
            response.addProperty("orderId", orderId);
            response.addProperty("amount", amountFormatted);
            response.addProperty("itemsName", itemNames.toString());
            response.addProperty("hash", finalHash);

            response.addProperty("returnUrl", "http://localhost:8081/ninewood/order-success.html");
            response.addProperty("cancelUrl", "http://localhost:8081/ninewood/checkout.html");
            response.addProperty("notifyUrl", "http://localhost:8081/ninewood/api/callback/payhere");

            response.addProperty("firstName", orderData.get("firstName").getAsString());
            response.addProperty("lastName", orderData.get("lastName").getAsString());
            response.addProperty("email", orderData.get("email").getAsString());
            response.addProperty("phone", orderData.get("phone").getAsString());
            response.addProperty("address", orderData.get("address").getAsString());
            response.addProperty("city", orderData.get("city").getAsString());

        } catch (Exception e) {
            e.printStackTrace();
            response.addProperty("status", false);
            response.addProperty("message", e.getMessage());
        } finally {
            hibernateSession.close();
        }

        return response;
    }

    private String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getUserOrderHistory(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        JsonArray ordersArray = new JsonArray();
        boolean status = false;

        HttpSession httpSession = request.getSession();
        User user = (User) httpSession.getAttribute("user");

        if (user == null) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "User not logged in!");
            return AppUtil.GSON.toJson(responseObject);
        }

        // Try-with-resources එකෙන් Session එක auto-close වෙනවා
        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {

            // HQL එකෙන් ලොග් වෙලා ඉන්න යූසර්ගේ ID එකට අදාළ ඕඩර්ස් විතරක් අරන් අලුත්ම ඒවා උඩට එන්න (DESC) ගන්නවා
            List<Orders> userOrders = hibernateSession.createQuery(
                            "FROM Orders WHERE user.id = :userId ORDER BY createdAt DESC", Orders.class)
                    .setParameter("userId", user.getId())
                    .list();

//            for (Orders order : userOrders) {
//                JsonObject orderJson = new JsonObject();
//                orderJson.addProperty("id", order.getId());
//                orderJson.addProperty("createdAt", order.getCreatedAt().toString());
//                orderJson.addProperty("status", order.getStatus().getValue()); // Status Table එකක් තියෙන නිසා getValue() කරා
//
//                // උඹේ Orders එන්ටිටි එකේ Total Price එකක් ඇතිනේ, ඒකත් මෙහෙම දාන්න
//                // orderJson.addProperty("totalPrice", order.getTotalPrice());
//
//                ordersArray.add(orderJson);
//            }

            // CheckoutService.java එකේ loop එක ඇතුළත කොටස මේකට මාරු කරන්න:
            for (Orders order : userOrders) {
                JsonObject orderJson = new JsonObject();
                orderJson.addProperty("id", order.getId());
                orderJson.addProperty("createdAt", order.getCreatedAt().toString());
                orderJson.addProperty("total", order.getPrice());

                if (order.getStatus() != null) {
                    orderJson.addProperty("status", order.getStatus().getValue());
                } else {
                    orderJson.addProperty("status", "Unknown");
                }

                StringBuilder productNames = new StringBuilder();

                // 1. Orders ඇතුළේ තියෙන OrderItems ලිස්ට් එක ගන්නවා
                if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {

                    for (OrderItem item : order.getOrderItems()) {
                        // item -> getStock() [මෙන්න මේකෙන් stock table එකට පනිනවා]
                        if (item.getStock() != null) {

                            // stock -> getProduct() (හෝ getProducts() - උඹේ Entity එකේ නම අනුව)
                            if (item.getStock().getProduct() != null) {

                                if (productNames.length() > 0) {
                                    productNames.append(", "); // අයිටම්ස් කිහිපයක් තිබ්බොත් කොමාවකින් වෙන් කරනවා
                                }

                                // product -> getTitle() (කෙලින්ම බඩුවේ නම ගන්නවා)
                                productNames.append(item.getStock().getProduct().getTitle());
                            }
                        }
                    }
                }

                // ආරක්ෂිත පියවරක් විදිහට (Fallback නමක්)
                if (productNames.length() == 0) {
                    productNames.append("Furniture Item");
                }

                // දැන් dynamic හැදුන Product Name string එක front-end යවනවා
                orderJson.addProperty("name", productNames.toString());
                // ==========================================

                ordersArray.add(orderJson);
            }

            status = true;
            responseObject.add("orders", ordersArray);

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("message", "Error loading orders: " + e.getMessage());
        }

        responseObject.addProperty("status", status);
        return AppUtil.GSON.toJson(responseObject);
    }
}

