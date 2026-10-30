package lk.dropti.ninewood.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lk.dropti.ninewood.dto.CartDTO;
import lk.dropti.ninewood.entity.Cart;
import lk.dropti.ninewood.entity.Stock;
import lk.dropti.ninewood.entity.User;
import lk.dropti.ninewood.util.AppUtil;
import lk.dropti.ninewood.util.HibernateUtil;
import lk.dropti.ninewood.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class CartService {

    public String deCarts(String UpcartQty, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        System.out.println(UpcartQty);
        boolean status = false;
        String message = "done";

//        if (cartId == null || cartId.isBlank()) {
//            message = "Invalid ID format!";
//        } else if (!cartId.matches(Validator.IS_INTEGER)) {
//            message = "Invalid ID format!";
//        } else {
//            int cId = Integer.parseInt(cartId);
//            HttpSession httpSession = request.getSession();
//            User sessionUser = (User) httpSession.getAttribute("user");
//            if (sessionUser == null) {
//                // remove from session cart
//                List<Cart> sessionCart = getSessionAttribute(httpSession);
//                if (sessionCart != null && !sessionCart.isEmpty()) {
//                    sessionCart.removeIf(cart -> cart.getId() == cId);
//                    httpSession.setAttribute("sessionCart", sessionCart);
//                    status = true;
//                    message = "Cart item deleted";
//                }
//
//            } else {
//                // remove from db cart
//                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//                Cart existingCart = hibernateSession.createQuery("FROM Cart c WHERE c.id=:cartId AND c.user.id=:userId", Cart.class)
//                        .setParameter("cartId", cId)
//                        .setParameter("userId", sessionUser.getId())
//                        .getSingleResultOrNull();
//                if (existingCart == null) {
//                    message = "Cart item not found!";
//                } else {
//                    Transaction transaction = hibernateSession.beginTransaction();
//                    try {
//                        hibernateSession.remove(existingCart);
//                        transaction.commit();
//                        status = true;
//                        message = "Cart item deleted";
//                    } catch (HibernateException e) {
//                        transaction.rollback();
//                    }
//                }
//                hibernateSession.close();
//            }
//        }
        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String updateCartQuantity(HttpServletRequest request, Long cartId, String action) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        int newQty = 1;
        double newTotalItemPrice = 0.0;

        HttpSession httpSession = request.getSession();
        User sessionUser = (User) httpSession.getAttribute("user");

        if (sessionUser == null) {
            System.out.println("updateCartQuantity: User is null, updating Session Cart...");

            List<Cart> sessionCart = getSessionAttribute(httpSession);

            if (sessionCart != null && !sessionCart.isEmpty()) {
                for (Cart cart : sessionCart) {
                    if (cart.getId() == cartId.intValue()) {
                        int currentQty = cart.getQty();

                        if ("increase".equals(action)) {
                            cart.setQty(currentQty + 1);
                        } else if ("decrease".equals(action)) {
                            if (currentQty > 1) cart.setQty(currentQty - 1);
                        }

                        newQty = cart.getQty();
                        if (cart.getStock() != null) {
                            newTotalItemPrice = cart.getQty() * cart.getStock().getPrice();
                        }
                        status = true;
                        message = "Session cart quantity updated";
                        break;
                    }
                }
                httpSession.setAttribute("sessionCart", sessionCart);
            } else {
                message = "Session cart is empty!";
            }

        } else {
            System.out.println("updateCartQuantity: User logged in, updating DB Cart...");

            try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                hibernateSession.beginTransaction();

                Cart dbCart = hibernateSession.get(Cart.class, cartId);

                if (dbCart.getUser().getId() == sessionUser.getId()) {
                    int currentQty = dbCart.getQty();

                    if ("increase".equals(action)) {
                        dbCart.setQty(currentQty + 1);
                    } else if ("decrease".equals(action)) {
                        if (currentQty > 1) dbCart.setQty(currentQty - 1);
                    }

                    hibernateSession.update(dbCart);

                    newQty = dbCart.getQty();

                    if (dbCart.getStock() != null) {
                        newTotalItemPrice = dbCart.getQty() * dbCart.getStock().getPrice();
                    }

                    hibernateSession.getTransaction().commit();
                    status = true;
                    message = "Database cart quantity updated";
                } else {
                    message = "Cart item not found or unauthorized!";
                }
            } catch (Exception e) {
                System.err.println("Hibernate Error in updateCartQuantity: " + e.getMessage());
                message = "Error: " + e.getMessage();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.addProperty("newQty", newQty);
        responseObject.addProperty("newTotalItemPrice", newTotalItemPrice);

        return AppUtil.GSON.toJson(responseObject);
    }

    public String getAllUserCarts(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        HttpSession httpSession = request.getSession();
        User sessionUser = (User) httpSession.getAttribute("user");
        if (sessionUser == null) {
            System.out.println("sessionUser is null");
            // use session cart
            List<Cart> sessionCart = getSessionAttribute(httpSession);
            if (sessionCart == null) {
                message = "Your cart is empty!";
            } else if (sessionCart.isEmpty()) {
                message = "Your cart is empty!";
            } else {
                // generate cart DTOs
                List<CartDTO> cartDTOList = generateCartDTOs(sessionCart);
                responseObject.add("cartItems", AppUtil.GSON.toJsonTree(cartDTOList));
                status = true;
                message = "Cart items loading success";
            }
        } else {
            System.out.println("User is already logged in");
            // use db cart
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            List<Cart> cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user.id=:id", Cart.class)
                    .setParameter("id", sessionUser.getId())
                    .getResultList();
            if (cartList.isEmpty()) {
                message = "Your cart is empty";
            } else {
                // generate cart DTOs
                List<CartDTO> cartDTOList = generateCartDTOs(cartList);
                responseObject.add("cartItems", AppUtil.GSON.toJsonTree(cartDTOList));
                System.out.println(responseObject);
                status = true;
                message = "Cart items loading success";
            }
            hibernateSession.close();
        }
        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    private List<CartDTO> generateCartDTOs(List<Cart> cartList) {
        List<CartDTO> cartDTOList = new ArrayList<>();
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        try {
            for (Cart cart : cartList) {
                // 💡 ආරක්ෂිතව Stock එක ඩේටාබේස් එකෙන් හොයාගන්නවා
                Stock stock = hibernateSession.find(Stock.class, cart.getStock().getId());

                if (stock != null && stock.getProduct() != null) {
                    CartDTO cartDTO = new CartDTO();
                    cartDTO.setCartId(cart.getId());
                    cartDTO.setStockId(stock.getId());
                    cartDTO.setProductTitle(stock.getProduct().getTitle());
                    cartDTO.setQty(cart.getQty());
                    cartDTO.setPrice(stock.getPrice());
                    cartDTO.setHeavy(stock.getProduct().isHeavy());

                    // 🎯 [IMAGES LAZY LOADING FIX]:
                    // සෙෂන් එක වහන්න කලින් පින්තූර ලිස්ට් එක බලෙන්ම අලුත් ලිස්ට් එකකට කොපි කරලා සෙට් කරනවා!
                    if (stock.getProduct().getImages() != null) {
                        cartDTO.setImages(new ArrayList<>(stock.getProduct().getImages()));
                    } else {
                        cartDTO.setImages(new ArrayList<>()); // පින්තූර නැත්නම් හිස් ලිස්ට් එකක්
                    }

                    cartDTOList.add(cartDTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 🛑 හැමදේම ඉවර වුණාම ක්ලීන් එකට සෙෂන් එක වහනවා
            hibernateSession.close();
        }

        return cartDTOList;
    }

    // merge session cart and db cart
//    public void mergeUserCarts(HttpServletRequest request) {
//        HttpSession httpSession = request.getSession();
//        User sessionUser = (User) httpSession.getAttribute("user");
//        if (sessionUser != null) {
//            List<Cart> sessionCart = getSessionAttribute(httpSession);
//            if (sessionCart != null && !sessionCart.isEmpty()) {
//                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//                User dbUser = hibernateSession.find(User.class, sessionUser.getId());
//                Transaction transaction = hibernateSession.beginTransaction();
//                for (Cart cart : sessionCart) {
//                    Stock stock = hibernateSession.find(Stock.class, cart.getStock().getId());
//                    Cart existingCart = hibernateSession.createQuery("FROM Cart c WHERE c.user=:user AND c.stock=:stock", Cart.class)
//                            .setParameter("user", dbUser)
//                            .setParameter("stock", stock)
//                            .getSingleResultOrNull();
//                    if (existingCart == null) {
//                        existingCart = new Cart(); // assign new Item
//                        existingCart.setQty(cart.getQty());
//                        existingCart.setUser(dbUser);
//                        existingCart.setStock(stock);
//                        hibernateSession.persist(existingCart);
//                    } else {
//                        int newQty = existingCart.getQty() + cart.getQty();
//                        if (newQty <= stock.getQty()) {
//                            existingCart.setQty(newQty);
//                            hibernateSession.merge(existingCart);
//                        }
//                    }
//                }
//                transaction.commit();
//                hibernateSession.close();
//            }
//            httpSession.setAttribute("sessionCart", null);
//        }
//    }

    public String addToCart(String sId, String qty, HttpServletRequest request) {
        System.out.println("addtocart service called");
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (sId == null || sId.isBlank()) {
            message = "Product ID not found!";
        } else if (!sId.matches(Validator.IS_INTEGER)) {
            message = "Invalid product Id!";
        } else if (qty == null || qty.isBlank()) {
            message = "Product quantity not found!";
        } else if (!qty.matches(Validator.IS_INTEGER)) {
            message = "Invalid quantity value!";
        } else {
            int stockId = Integer.parseInt(sId);
            int requestQty = Integer.parseInt(qty);
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Stock stock = hibernateSession.find(Stock.class, stockId);
            if (stock == null) {
                message = "Product not found!";
            } else {
                // stock available
                HttpSession httpSession = request.getSession();
                User user = (User) httpSession.getAttribute("user");
                List<Cart> sessionCart = getSessionAttribute(httpSession);
                if (user == null) {
                    // User not logged in
                    if (sessionCart == null) {
                        // first time
                        // no session cart -> create new session cart for user
                        return guestUserFirstTime(stock, requestQty, httpSession);
                    } else {
                        // second time
                        // session cart exists -> add new cart item to list
                        return guestUserSecondTime(stock, requestQty, httpSession);
                    }
                } else {
                    // User already logged
                    return loggedUserCart(stock, requestQty, httpSession, hibernateSession);
                }
            }
            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String deleteCartItem(String sId, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (sId == null || sId.isBlank()) {
            message = "Product ID not found!";
        } else if (!sId.matches(Validator.IS_INTEGER)) {
            message = "Invalid product Id!";
        } else {
            int stockId = Integer.parseInt(sId);
            HttpSession httpSession = request.getSession();
            User user = (User) httpSession.getAttribute("user");

            if (user == null) {
                // 1. Guest User නම් - Session Cart එකෙන් අයින් කරනවා
                List<Cart> sessionCart = (List<Cart>) httpSession.getAttribute("sessionCart");
                if (sessionCart != null) {
                    boolean removed = sessionCart.removeIf(item -> item.getStock().getId() == stockId);
                    if (removed) {
                        status = true;
                        message = "Item removed from session cart!";
                    } else {
                        message = "Item not found in cart!";
                    }
                } else {
                    message = "Cart is empty!";
                }
            } else {
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Transaction transaction = hibernateSession.beginTransaction();

                    Cart cartItem = (Cart) hibernateSession.createQuery(
                                    "FROM Cart WHERE user.id = :userId AND stock.id = :stockId")
                            .setParameter("userId", user.getId())
                            .setParameter("stockId", stockId)
                            .uniqueResult();

                    if (cartItem != null) {
                        hibernateSession.delete(cartItem);
                        transaction.commit();
                        status = true;
                        message = "Item removed from your database cart!";
                    } else {
                        message = "Item not found in database cart!";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "Server error: " + e.getMessage();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    private String loggedUserCart(Stock stock, int requestQty, HttpSession httpSession, Session hibernateSession) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        User sessionUser = (User) httpSession.getAttribute("user");
        if (sessionUser != null) {
            User dbUser = hibernateSession.find(User.class, sessionUser.getId());
            Cart existingCart = hibernateSession.createQuery("FROM Cart c WHERE c.user=:user AND c.stock=:stock", Cart.class)
                    .setParameter("user", dbUser)
                    .setParameter("stock", stock)
                    .getSingleResultOrNull();
            Transaction transaction = hibernateSession.beginTransaction();
            if (existingCart == null) {
                // new cart item
                existingCart = new Cart();
                existingCart.setUser(dbUser);
                existingCart.setStock(stock);
                existingCart.setQty(requestQty);
                hibernateSession.persist(existingCart);
                status = true;
                message = "Product add to cart";
            } else {
                // update quantity. already cart exist
                int newQty = existingCart.getQty() + requestQty;
                if (newQty > stock.getQty()) {
                    message = "Product quantity exceeded!";
                } else {
                    existingCart.setQty(newQty);
                    hibernateSession.merge(existingCart);
                    status = true;
                    message = "User cart updated";
                }
            }
            transaction.commit();
            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    private String guestUserSecondTime(Stock stock, int requestQty, HttpSession httpSession) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        List<Cart> sessionCart = getSessionAttribute(httpSession);
        boolean found = false;
        Cart cart = null;
        for (Cart c : sessionCart) {
            if (c.getStock().getId() == stock.getId()) {
                found = true;
                cart = c;
                break;
            }
        }
        if (found) {
            int newQty = cart.getQty() + requestQty;
            if (newQty > stock.getQty()) {
                message = "Product quantity exceeded!";
            } else {
                cart.setQty(newQty);
                status = true;
                message = "User cart updated!";
            }
        } else {
            cart = new Cart();
            cart.setId(sessionCart.size() + 1);
            cart.setStock(stock);
            cart.setQty(requestQty);
            cart.setUser(null);
            sessionCart.add(cart);
            httpSession.setAttribute("sessionCart", sessionCart);
            status = true;
            message = "Product add to the cart";
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    private String guestUserFirstTime(Stock stock, int requestQty, HttpSession httpSession) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        if (requestQty > stock.getQty()) {
            message = "Product quantity exceeded!";
        } else {
            List<Cart> cartList = new ArrayList<>();
            Cart cart = new Cart();
            cart.setId(1);
            cart.setStock(stock);
            cart.setQty(requestQty);
            cart.setUser(null);
            cartList.add(cart);
            httpSession.setAttribute("sessionCart", cartList);
            status = true;
            message = "Product add to the cart";
        }
        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    // helper method prevent warnings
    @SuppressWarnings("unchecked")
    private <T> T getSessionAttribute(HttpSession httpSession) {
        return (T) httpSession.getAttribute("sessionCart");
    }

    public String deUserCarts(String UpcartQty ,HttpServletRequest request) {
        System.out.println("Cart Service" + UpcartQty);
        JsonObject responseObject = new JsonObject();
        return  AppUtil.GSON.toJson(responseObject);
    }
}

//package lk.dropti.ninewood.service;
//
//import com.google.gson.JsonObject;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import lk.dropti.ninewood.dto.CartDTO;
//import lk.dropti.ninewood.entity.Cart;
//import lk.dropti.ninewood.entity.User;
//import lk.dropti.ninewood.util.AppUtil;
//import lk.dropti.ninewood.util.HibernateUtil;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CartService {
//
//    // ===============================
//    // GET ALL CART ITEMS (SESSION + DB)
//    // ===============================
//    public String getAllUserCarts(HttpServletRequest request) {
//
//        JsonObject response = new JsonObject();
//        boolean status = false;
//        String message;
//
//        HttpSession httpSession = request.getSession(false);
//
//        if (httpSession == null) {
//            message = "Session expired";
//            return buildResponse(response, status, message);
//        }
//
//        // ==========================
//        // GET USER ID FROM SESSION
//        // ==========================
//        Integer userId = (Integer) httpSession.getAttribute("userId");
//
//        // ==========================
//        // USER NOT LOGGED IN → SESSION CART
//        // ==========================
//        if (userId == null) {
//            List<CartDTO> sessionCart = getSessionCart(httpSession);
//
//            if (sessionCart == null || sessionCart.isEmpty()) {
//                message = "Your cart is empty!";
//            } else {
//                response.add("cartItems", AppUtil.GSON.toJsonTree(sessionCart));
//                status = true;
//                message = "Cart items loaded successfully";
//            }
//
//            return buildResponse(response, status, message);
//        }
//
//        // ==========================
//        // USER LOGGED IN → DB CART
//        // ==========================
//        Session hibernateSession = null;
//        Transaction tx = null;
//
//        try {
//            hibernateSession = HibernateUtil.getSessionFactory().openSession();
//            tx = hibernateSession.beginTransaction();
//
//            // FETCH CART WITH STOCK + PRODUCT (NO LAZY ISSUE)
//            List<Cart> cartList = hibernateSession.createQuery(
//                            "SELECT c FROM Cart c " +
//                                    "JOIN FETCH c.stock s " +
//                                    "JOIN FETCH s.product p " +
//                                    "WHERE c.user.id = :uid", Cart.class
//                    ).setParameter("uid", userId)
//                    .getResultList();
//
//            if (cartList.isEmpty()) {
//                System.out.println(cartList);
//                message = "Your cart is empty";
//            } else {
//                List<CartDTO> cartDTOs = mapToDTO(cartList);
//                response.add("cartItems", AppUtil.GSON.toJsonTree(cartDTOs));
//                status = true;
//                message = "Cart items loaded successfully";
//            }
//
//            tx.commit();
//
//        } catch (Exception e) {
//            if (tx != null) tx.rollback();
//            e.printStackTrace();
//            message = "Failed to load cart items";
//
//        } finally {
//            if (hibernateSession != null) hibernateSession.close();
//        }
//
//        return buildResponse(response, status, message);
//    }
//
//    // ===============================
//    // MAP ENTITY → DTO
//    // ===============================
//    private List<CartDTO> mapToDTO(List<Cart> cartList) {
//
//        List<CartDTO> dtoList = new ArrayList<>();
//
//        for (Cart cart : cartList) {
//            CartDTO dto = new CartDTO();
//            dto.setCartId(cart.getId());
//            dto.setStockId(cart.getStock().getId());
//            dto.setProductTitle(cart.getStock().getProduct().getTitle());
//            dto.setImages(cart.getStock().getProduct().getImages());
//            dto.setQty(cart.getQty());
//            dto.setPrice(cart.getStock().getPrice());
//
//            dtoList.add(dto);
//        }
//        return dtoList;
//    }
//
//    // ===============================
//    // SESSION CART (GUEST USER)
//    // ===============================
//    @SuppressWarnings("unchecked")
//    private List<CartDTO> getSessionCart(HttpSession session) {
//        return (List<CartDTO>) session.getAttribute("cart");
//    }
//
//    // ===============================
//    // JSON RESPONSE BUILDER
//    // ===============================
//    private String buildResponse(JsonObject obj, boolean status, String message) {
//        obj.addProperty("status", status);
//        obj.addProperty("message", message);
//        return AppUtil.GSON.toJson(obj);
//    }
//}


