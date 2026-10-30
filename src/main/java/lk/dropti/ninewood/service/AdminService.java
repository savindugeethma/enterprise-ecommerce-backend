package lk.dropti.ninewood.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;
import lk.dropti.ninewood.dto.OrderDTO;
import lk.dropti.ninewood.dto.ProductDTO;
import lk.dropti.ninewood.dto.UserDTO;
import lk.dropti.ninewood.entity.*;
import lk.dropti.ninewood.util.AppUtil;
import lk.dropti.ninewood.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    public String loadOverview(@Context HttpServletRequest request){
        System.out.println("done");
        return null;
    }

//    public String getAdminDashboardOverview() {
//        JsonObject responseObject = new JsonObject();
//        boolean status = false;
//
//        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
//
//            // 1. Total Revenue (price column එකේ එකතුව)
//            Double totalRevenue = (Double) hibernateSession.createQuery("SELECT SUM(o.price) FROM Orders o").uniqueResult();
//            if (totalRevenue == null) totalRevenue = 0.0;
//
//            // 2. Total Orders Count
//            Long totalOrders = (Long) hibernateSession.createQuery("SELECT COUNT(o.id) FROM Orders o").uniqueResult();
//
//            // 3. Total Customers Count (Status එක 3 නොවන ඔක්කොම Customers ලා විදිහට ගන්නවා)
//            // ⚠️ උඹේ User entity එකේ status මැප් කරපු variable name එක 'status' නම් u.status.id != 3 කරන්න
//            Long totalCustomers = (Long) hibernateSession.createQuery("SELECT COUNT(u.id) FROM User u WHERE u.status.id != 3").uniqueResult();
//
//            // 4. Recent Orders Table (අලුත්ම ඕඩර්ස් 5)
//            // o.user_id හෝ o.users_id කේස් එක Hibernate මඟින් o.user හරහා auto-handle වෙනවා
//            List<Orders> recentOrdersList = hibernateSession.createQuery(
//                            "SELECT DISTINCT o FROM Orders o LEFT JOIN FETCH o.orderItems ORDER BY o.createdAt DESC", Orders.class)
//                    .setMaxResults(5)
//                    .list();
//
//            JsonArray recentOrdersArray = new JsonArray();
//            for (Orders order : recentOrdersList) {
//                JsonObject orderJson = new JsonObject();
//                orderJson.addProperty("id", order.getId());
//
//                // 💡 Customer ගේ සම්පූර්ණ නම ගන්නවා
//                if (order.getUser() != null) {
//                    orderJson.addProperty("customer", order.getUser().getFirstName() + " " + order.getUser().getLastName());
//                } else {
//                    orderJson.addProperty("customer", "Guest User");
//                }
//
//                orderJson.addProperty("createdAt", order.getCreatedAt().toString());
//                orderJson.addProperty("total", order.getPrice());
//                orderJson.addProperty("status", order.getStatus() != null ? order.getStatus().getValue() : "Unknown");
//
//                recentOrdersArray.add(orderJson);
//            }
//
//            // JSON එකට දත්ත ටික සෙට් කරනවා
//            responseObject.addProperty("totalRevenue", totalRevenue);
//            responseObject.addProperty("totalOrders", totalOrders);
//            responseObject.addProperty("totalCustomers", totalCustomers);
//            responseObject.add("recentOrders", recentOrdersArray);
//
//            status = true;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            responseObject.addProperty("message", "Error: " + e.getMessage());
//        }
//
//        responseObject.addProperty("status", status);
//        return AppUtil.GSON.toJson(responseObject);
//    }

    public String getSellerDashboardOverview(int userId) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {

            // 1. ලොග් වෙලා ඉන්න User ID එකට අදාළ Seller Object එක ගන්නවා
            Seller seller = (Seller) hibernateSession.createQuery("FROM Seller WHERE user.id = :userId", Seller.class)
                    .setParameter("userId", userId)
                    .uniqueResult();

            if (seller == null) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Seller profile not found!");
                return AppUtil.GSON.toJson(responseObject);
            }

            int sellerId = seller.getId();

            // 2. TOTAL REVENUE: මේ සෙලර්ගේ බඩු වල (Qty * Stock Price) එකතුව
            Double totalRevenue = (Double) hibernateSession.createQuery(
                            "SELECT SUM(oi.qty * oi.stock.price) FROM OrderItem oi WHERE oi.seller.id = :sellerId")
                    .setParameter("sellerId", sellerId)
                    .uniqueResult();
            if (totalRevenue == null) totalRevenue = 0.0;

            // 3. TOTAL ORDERS: මේ සෙලර්ට ආපු Orders ගණන
            Long totalOrders = (Long) hibernateSession.createQuery(
                            "SELECT COUNT(DISTINCT oi.order.id) FROM OrderItem oi WHERE oi.seller.id = :sellerId")
                    .setParameter("sellerId", sellerId)
                    .uniqueResult();

            // 4. TOTAL CUSTOMERS: මේ සෙලර්ගේ බඩු ගත්තු සුවිශේෂී යූසර්ස්ලා ගණන
            Long totalCustomers = (Long) hibernateSession.createQuery(
                            "SELECT COUNT(DISTINCT oi.order.user.id) FROM OrderItem oi WHERE oi.seller.id = :sellerId")
                    .setParameter("sellerId", sellerId)
                    .uniqueResult();

            // 5. RECENT ORDERS TABLE: මෙන්න මෙතනට මචං මම parameter එක bind කරලා දිව්වේ
            Query<Orders> recentOrdersQuery = hibernateSession.createQuery(
                    "SELECT DISTINCT o FROM Orders o " +
                            "JOIN o.orderItems oi " +
                            "WHERE oi.seller.id = :sellerId ORDER BY o.createdAt DESC", Orders.class);

            // 🎯 අන්න ඒ අඩුව මෙතනින් පිරෙව්වා!
            recentOrdersQuery.setParameter("sellerId", sellerId);
            recentOrdersQuery.setMaxResults(5);

            List<Orders> recentOrdersList = recentOrdersQuery.list();

            JsonArray recentOrdersArray = new JsonArray();
            for (Orders order : recentOrdersList) {
                JsonObject orderJson = new JsonObject();
                orderJson.addProperty("id", order.getId());

                if (order.getUser() != null) {
                    orderJson.addProperty("customer", order.getUser().getFirstName() + " " + order.getUser().getLastName());
                } else {
                    orderJson.addProperty("customer", "Guest User");
                }

                orderJson.addProperty("createdAt", order.getCreatedAt().toString());
                orderJson.addProperty("total", order.getPrice());
                orderJson.addProperty("status", order.getStatus() != null ? order.getStatus().getValue() : "Unknown");

                recentOrdersArray.add(orderJson);
            }

            // JSON එකට සේරම දත්ත ටික පිරවීම
            responseObject.addProperty("totalRevenue", totalRevenue);
            responseObject.addProperty("totalOrders", totalOrders);
            responseObject.addProperty("totalCustomers", totalCustomers);
            responseObject.add("recentOrders", recentOrdersArray);

            status = true;

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("message", "Error: " + e.getMessage());
        }

        responseObject.addProperty("status", status);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String getSellerProducts(int userId) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {

            Seller seller = (Seller) hibernateSession.createQuery("FROM Seller WHERE user.id = :userId", Seller.class)
                    .setParameter("userId", userId)
                    .uniqueResult();

            if (seller == null) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Seller profile not found!");
                return AppUtil.GSON.toJson(responseObject);
            }

            // HQL Logic: මේ සෙලර් විකුණන බඩු (Stock) ටික ඇදලා ගන්නවා
            List<Stock> sellerStocks = hibernateSession.createQuery(
                            "SELECT DISTINCT s FROM Stock s " +
                                    "JOIN OrderItem oi ON oi.stock.id = s.id " +
                                    "WHERE oi.seller.id = :sellerId", Stock.class)
                    .setParameter("sellerId", seller.getId())
                    .list();

            JsonArray productsArray = new JsonArray();
            for (Stock stock : sellerStocks) {
                if (stock.getProduct() != null) {
                    JsonObject productJson = new JsonObject();
                    productJson.addProperty("id", stock.getProduct().getId());
                    productJson.addProperty("title", stock.getProduct().getTitle());
                    productJson.addProperty("price", stock.getPrice());
                    productJson.addProperty("qty", stock.getQty());
                    productJson.addProperty("status", stock.getStatus() != null ? stock.getStatus().getValue() : "Active");

                    productsArray.add(productJson);
                }
            }

            responseObject.add("products", productsArray);
            status = true;

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("message", "Error loading products: " + e.getMessage());
        }

        responseObject.addProperty("status", status);
        return AppUtil.GSON.toJson(responseObject);
    }


    public String getSellerCustomers(int userId) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {

            Seller seller = (Seller) hibernateSession.createQuery("FROM Seller WHERE user.id = :userId", Seller.class)
                    .setParameter("userId", userId)
                    .uniqueResult();

            if (seller == null) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Seller profile not found!");
                return AppUtil.GSON.toJson(responseObject);
            }

            // HQL Logic: මේ සෙලර්ගේ බඩු මිලදී ගත්තු සුවිශේෂී (DISTINCT) පාරිභෝගිකයන් (Users) ලැයිස්තුව
            List<User> customers = hibernateSession.createQuery(
                            "SELECT DISTINCT oi.order.user FROM OrderItem oi WHERE oi.seller.id = :sellerId", User.class)
                    .setParameter("sellerId", seller.getId())
                    .list();

            JsonArray customersArray = new JsonArray();
            for (User customer : customers) {
                JsonObject customerJson = new JsonObject();
                customerJson.addProperty("id", customer.getId());
                customerJson.addProperty("name", customer.getFirstName() + " " + customer.getLastName());
                customerJson.addProperty("email", customer.getEmail());

                customersArray.add(customerJson);
            }

            responseObject.add("customers", customersArray);
            status = true;

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("message", "Error loading customers: " + e.getMessage());
        }

        responseObject.addProperty("status", status);
        return AppUtil.GSON.toJson(responseObject);
    }


    public String loadProducts(@Context HttpServletRequest request){
        System.out.println("loadProducts service");
        JsonObject responseObject = new JsonObject();
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<ProductDTO> productDTOList = new ArrayList<>();
        List<Stock> stockList = hibernateSession.createQuery("FROM Stock s ORDER BY s.createdAt DESC", Stock.class)
                .setMaxResults(8)
                .getResultList();

        System.out.println("Admin load products Query done");
        for(Stock stock :stockList){
            Product product = stock.getProduct();
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(product.getId());
            productDTO.setTitle(product.getTitle());
            productDTO.setColorId(product.getColor().getId());
            productDTO.setBrandId(product.getModel().getBrand().getId());
            productDTO.setModelId(product.getModel().getId());
            productDTO.setQualityId(product.getQuality().getId());
            productDTO.setColorValue(product.getColor().getValue());
            productDTO.setImages(product.getImages());
            productDTO.setStockId(stock.getId());
            productDTO.setQty(stock.getQty());
            productDTO.setPrice(stock.getPrice());
            productDTOList.add(productDTO);
        }
        hibernateSession.close();
        responseObject.add("newArrivals", AppUtil.GSON.toJsonTree(productDTOList));
        System.out.println("returning products");
        return AppUtil.GSON.toJson(responseObject);
    }

//    public String loadOrders(@Context HttpServletRequest request){
//        System.out.println("loadOrders service");
//        JsonObject responseObject = new JsonObject();
//        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//        List<OrderDTO> orderDTOList = new ArrayList<>();
//        List<Orders> orderList = hibernateSession.createQuery("SELECT o FROM Orders o", Orders.class)
//                .getResultList();
//
//        System.out.println("Admin load orders Query done");
//        for(Orders order :orderList){
//            OrderDTO orderDTO = new OrderDTO();
//            orderDTO.setId(1);
//            orderDTO.setStatusId(1);
//            orderDTO.setUserId(1);
//
//        }
//        hibernateSession.close();
//        responseObject.add("newArrivals", AppUtil.GSON.toJsonTree(orderDTOList));
//        System.out.println("returning products");
//        return AppUtil.GSON.toJson(responseObject);
//    }
    public String loadOrders(@Context HttpServletRequest request) {
        System.out.println("loadOrders service");

        JsonObject responseObject = new JsonObject();
        List<OrderDTO> orderDTOList = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            // Fetch orders with user info in one query
            List<Orders> orders = session.createQuery(
                    "FROM Orders o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.status ORDER BY o.createdAt DESC",
                    Orders.class
            ).getResultList();

            System.out.println("Admin load orders Query done, total orders: " + orders.size());

            for (Orders order : orders) {
                OrderDTO dto = new OrderDTO();
                dto.setId(order.getId());
                dto.setPrice(order.getPrice());
//                dto.setUpdatedAt(order.getUpdatedAt());

                // Customer info
                User user = order.getUser();
                dto.setUserId(user != null ? user.getId() : 0);
                dto.setUserName(user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown");

                // Status info
                Status status = order.getStatus();
                dto.setStatusId(status != null ? status.getId() : 0);
                dto.setStatusName(status != null ? status.getValue() : "Unknown");

                // Order items info
//                List<OrderItem> items = order.getOrderItems();
//                dto.setOrderItemsCount(items != null ? items.size() : 0);

                // Calculate total amount from items
//                double totalAmount = 0;
//                if (items != null) {
//                    totalAmount = items.stream()
//                            .mapToDouble(item -> item.getPrice() * item.getQuantity())
//                            .sum();
//                }
//                dto.setAmount(totalAmount);

                orderDTOList.add(dto);
            }

            // Convert to JSON
            JsonArray ordersJson = AppUtil.GSON.toJsonTree(orderDTOList).getAsJsonArray();
            responseObject.add("orders", ordersJson);
            responseObject.addProperty("total_orders", orderDTOList.size());

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("error", "Failed to load orders");
        } finally {
            session.close();
        }

        return AppUtil.GSON.toJson(responseObject);
    }
//    public String loadOrders(@Context HttpServletRequest request) {
//        System.out.println("loadOrders service");
//
//        JsonObject responseObject = new JsonObject();
//        List<OrderDTO> orderDTOList = new ArrayList<>();
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        try {
//            List<Orders> orders = session
//                    .createQuery("FROM Orders o ORDER BY o.createdAt DESC", Orders.class)
//                    .getResultList();
//
//            for (Orders order : orders) {
//                OrderDTO dto = new OrderDTO();
//                dto.setId(order.getId());
//                dto.setStatusId(order.getStatus() != null ? order.getStatus().getId() : 0);
//                dto.setUserId(order.getUser() != null ? order.getUser().getId() : 0);
//                dto.setCreatedAt(order.getCreatedAt());
//                dto.setUpdatedAt(order.getUpdatedAt());
//                dto.setOrderItemsCount(order.getOrderItems() != null ? order.getOrderItems().size() : 0);
//
//                orderDTOList.add(dto);
//            }
//
//            // Convert to JSON
//            JsonArray ordersJson = AppUtil.GSON.toJsonTree(orderDTOList).getAsJsonArray();
//            responseObject.add("orders", ordersJson);
//            responseObject.addProperty("total_orders", orderDTOList.size());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
//
//        return AppUtil.GSON.toJson(responseObject);
//    }

//    public String loadOrders(@Context HttpServletRequest request) {
//
//        System.out.println("loadOrders service");
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        JsonObject responseObject = new JsonObject();
//
//        try {
//            List<Orders> orderList = session
//                    .createQuery("SELECT o FROM Orders o", Orders.class)
//                    .getResultList();
//
//            System.out.println("Orders count: " + orderList.size());
//
//            responseObject.add("orders", AppUtil.GSON.toJsonTree(orderList));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
//
//        return AppUtil.GSON.toJson(responseObject);
//    }


//    public String loadCustomers(@Context HttpServletRequest request){
//
//    }

    public String loadCustomers(@Context HttpServletRequest request) {
        System.out.println("load Customers service");

        JsonObject responseObject = new JsonObject();
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        try {
            // Fetch users ordered by creation date descending
            List<User> customersList = hibernateSession
                    .createQuery("FROM User u ORDER BY u.createdAt DESC", User.class)
                    .getResultList();

            System.out.println("Admin load Customers Query done");

            // Convert Users to DTOs
            List<UserDTO> customerDTOList = new ArrayList<>();
            for (User customer : customersList) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(customer.getId());
                userDTO.setFirstName(customer.getFirstName());
                userDTO.setLastName(customer.getLastName());
                userDTO.setEmail(customer.getEmail());
                userDTO.setStatus(customer.getStatus().getValue());

                // Add the DTO to the list
                customerDTOList.add(userDTO);
            }

            // Convert DTO list to JSON
            responseObject.add("customers", AppUtil.GSON.toJsonTree(customerDTOList));

            System.out.println("returning customers");
            return AppUtil.GSON.toJson(responseObject);

        } finally {
            // Ensure session is always closed
            hibernateSession.close();
        }
    }


    public String updateOrderDetails(HttpServletRequest request, String orderId, String status, String trackingNumber) {
        System.out.println("==========================================");
        System.out.println("updateOrderDetails service called");
        System.out.println("Values in Service: " + orderId + " | " + status + " | " + trackingNumber);
        System.out.println("==========================================");

        // TODO: මෙතනට Database Update Logic එක දාන්න

        // Orders ටික ආපහු load කරලා JSON එක return කිරීම
        return AppUtil.GSON.toJson(loadOrders(request));
    }
}
