//package lk.dropti.ninewood.entity.service;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.ws.rs.core.Context;
//import lk.dropti.ninewood.entity.*;
//import lk.dropti.ninewood.entity.dto.OrderDTO;
//import lk.dropti.ninewood.entity.dto.ProductDTO;
//import lk.dropti.ninewood.entity.dto.UserDTO;
//import lk.dropti.ninewood.entity.entity.User;
//import lk.dropti.ninewood.entity.util.AppUtil;
//import lk.dropti.ninewood.entity.util.HibernateUtil;
//import org.hibernate.Session;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.List;
//
//public class AdminService {
//    public String loadOverview(@Context HttpServletRequest request){
//        System.out.println("done");
//        return null;
//    }
//
//    public String loadProducts(@Context HttpServletRequest request){
//        System.out.println("loadProducts service");
//        JsonObject responseObject = new JsonObject();
//        Session hibernateSession = lk.dropti.ninewood.entity.util.HibernateUtil.getSessionFactory().openSession();
//        List<lk.dropti.ninewood.entity.dto.ProductDTO> productDTOList = new ArrayList<>();
//        List<lk.dropti.ninewood.entity.entity.Stock> stockList = hibernateSession.createQuery("FROM Stock s ORDER BY s.createdAt DESC", lk.dropti.ninewood.entity.entity.Stock.class)
//                .setMaxResults(8)
//                .getResultList();
//
//        System.out.println("Admin load products Query done");
//        for(lk.dropti.ninewood.entity.entity.Stock stock :stockList){
//            lk.dropti.ninewood.entity.entity.Product product = stock.getProduct();
//            lk.dropti.ninewood.entity.dto.ProductDTO productDTO = new ProductDTO();
//            productDTO.setProductId(product.getId());
//            productDTO.setTitle(product.getTitle());
//            productDTO.setColorId(product.getColor().getId());
//            productDTO.setBrandId(product.getModel().getBrand().getId());
//            productDTO.setModelId(product.getModel().getId());
//            productDTO.setQualityId(product.getQuality().getId());
//            productDTO.setColorValue(product.getColor().getValue());
//            productDTO.setImages(product.getImages());
//            productDTO.setStockId(stock.getId());
//            productDTO.setQty(stock.getQty());
//            productDTO.setPrice(stock.getPrice());
//            productDTOList.add(productDTO);
//        }
//        hibernateSession.close();
//        responseObject.add("newArrivals", lk.dropti.ninewood.entity.util.AppUtil.GSON.toJsonTree(productDTOList));
//        System.out.println("returning products");
//        return lk.dropti.ninewood.entity.util.AppUtil.GSON.toJson(responseObject);
//    }
//
////    public String loadOrders(@Context HttpServletRequest request){
////        System.out.println("loadOrders service");
////        JsonObject responseObject = new JsonObject();
////        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
////        List<OrderDTO> orderDTOList = new ArrayList<>();
////        List<Orders> orderList = hibernateSession.createQuery("SELECT o FROM Orders o", Orders.class)
////                .getResultList();
////
////        System.out.println("Admin load orders Query done");
////        for(Orders order :orderList){
////            OrderDTO orderDTO = new OrderDTO();
////            orderDTO.setId(1);
////            orderDTO.setStatusId(1);
////            orderDTO.setUserId(1);
////
////        }
////        hibernateSession.close();
////        responseObject.add("newArrivals", AppUtil.GSON.toJsonTree(orderDTOList));
////        System.out.println("returning products");
////        return AppUtil.GSON.toJson(responseObject);
////    }
//    public String loadOrders(@Context HttpServletRequest request) {
//        System.out.println("loadOrders service");
//
//        JsonObject responseObject = new JsonObject();
//        List<lk.dropti.ninewood.entity.dto.OrderDTO> orderDTOList = new ArrayList<>();
//
//        Session session = lk.dropti.ninewood.entity.util.HibernateUtil.getSessionFactory().openSession();
//        try {
//            // Fetch orders with user info in one query
//            List<lk.dropti.ninewood.entity.entity.Orders> orders = session.createQuery(
//                    "FROM Orders o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.status ORDER BY o.createdAt DESC",
//                    lk.dropti.ninewood.entity.entity.Orders.class
//            ).getResultList();
//
//            System.out.println("Admin load orders Query done, total orders: " + orders.size());
//
//            for (lk.dropti.ninewood.entity.entity.Orders order : orders) {
//                lk.dropti.ninewood.entity.dto.OrderDTO dto = new OrderDTO();
//                dto.setId(order.getId());
//                dto.setPrice(order.getPrice());
////                dto.setUpdatedAt(order.getUpdatedAt());
//
//                // Customer info
//                lk.dropti.ninewood.entity.entity.User user = order.getUser();
//                dto.setUserId(user != null ? user.getId() : 0);
//                dto.setUserName(user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown");
//
//                // Status info
//                lk.dropti.ninewood.entity.entity.Status status = order.getStatus();
//                dto.setStatusId(status != null ? status.getId() : 0);
//                dto.setStatusName(status != null ? status.getValue() : "Unknown");
//
//                // Order items info
////                List<OrderItem> items = order.getOrderItems();
////                dto.setOrderItemsCount(items != null ? items.size() : 0);
//
//                // Calculate total amount from items
////                double totalAmount = 0;
////                if (items != null) {
////                    totalAmount = items.stream()
////                            .mapToDouble(item -> item.getPrice() * item.getQuantity())
////                            .sum();
////                }
////                dto.setAmount(totalAmount);
//
//                orderDTOList.add(dto);
//            }
//
//            // Convert to JSON
//            JsonArray ordersJson = lk.dropti.ninewood.entity.util.AppUtil.GSON.toJsonTree(orderDTOList).getAsJsonArray();
//            responseObject.add("orders", ordersJson);
//            responseObject.addProperty("total_orders", orderDTOList.size());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            responseObject.addProperty("error", "Failed to load orders");
//        } finally {
//            session.close();
//        }
//
//        return lk.dropti.ninewood.entity.util.AppUtil.GSON.toJson(responseObject);
//    }
////    public String loadOrders(@Context HttpServletRequest request) {
////        System.out.println("loadOrders service");
////
////        JsonObject responseObject = new JsonObject();
////        List<OrderDTO> orderDTOList = new ArrayList<>();
////
////        Session session = HibernateUtil.getSessionFactory().openSession();
////        try {
////            List<Orders> orders = session
////                    .createQuery("FROM Orders o ORDER BY o.createdAt DESC", Orders.class)
////                    .getResultList();
////
////            for (Orders order : orders) {
////                OrderDTO dto = new OrderDTO();
////                dto.setId(order.getId());
////                dto.setStatusId(order.getStatus() != null ? order.getStatus().getId() : 0);
////                dto.setUserId(order.getUser() != null ? order.getUser().getId() : 0);
////                dto.setCreatedAt(order.getCreatedAt());
////                dto.setUpdatedAt(order.getUpdatedAt());
////                dto.setOrderItemsCount(order.getOrderItems() != null ? order.getOrderItems().size() : 0);
////
////                orderDTOList.add(dto);
////            }
////
////            // Convert to JSON
////            JsonArray ordersJson = AppUtil.GSON.toJsonTree(orderDTOList).getAsJsonArray();
////            responseObject.add("orders", ordersJson);
////            responseObject.addProperty("total_orders", orderDTOList.size());
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        } finally {
////            session.close();
////        }
////
////        return AppUtil.GSON.toJson(responseObject);
////    }
//
////    public String loadOrders(@Context HttpServletRequest request) {
////
////        System.out.println("loadOrders service");
////
////        Session session = HibernateUtil.getSessionFactory().openSession();
////        JsonObject responseObject = new JsonObject();
////
////        try {
////            List<Orders> orderList = session
////                    .createQuery("SELECT o FROM Orders o", Orders.class)
////                    .getResultList();
////
////            System.out.println("Orders count: " + orderList.size());
////
////            responseObject.add("orders", AppUtil.GSON.toJsonTree(orderList));
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        } finally {
////            session.close();
////        }
////
////        return AppUtil.GSON.toJson(responseObject);
////    }
//
//
////    public String loadCustomers(@Context HttpServletRequest request){
////
////    }
//
//    public String loadCustomers(@Context HttpServletRequest request) {
//        System.out.println("load Customers service");
//
//        JsonObject responseObject = new JsonObject();
//        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//
//        try {
//            // Fetch users ordered by creation date descending
//            List<lk.dropti.ninewood.entity.entity.User> customersList = hibernateSession
//                    .createQuery("FROM User u ORDER BY u.createdAt DESC", lk.dropti.ninewood.entity.entity.User.class)
//                    .getResultList();
//
//            System.out.println("Admin load Customers Query done");
//
//            // Convert Users to DTOs
//            List<lk.dropti.ninewood.entity.dto.UserDTO> customerDTOList = new ArrayList<>();
//            for (User customer : customersList) {
//                lk.dropti.ninewood.entity.dto.UserDTO userDTO = new UserDTO();
//                userDTO.setId(customer.getId());
//                userDTO.setFirstName(customer.getFirstName());
//                userDTO.setLastName(customer.getLastName());
//                userDTO.setEmail(customer.getEmail());
//                userDTO.setStatus(customer.getStatus().getValue());
//
//                // Add the DTO to the list
//                customerDTOList.add(userDTO);
//            }
//
//            // Convert DTO list to JSON
//            responseObject.add("customers", lk.dropti.ninewood.entity.util.AppUtil.GSON.toJsonTree(customerDTOList));
//
//            System.out.println("returning customers");
//            return AppUtil.GSON.toJson(responseObject);
//
//        } finally {
//            // Ensure session is always closed
//            hibernateSession.close();
//        }
//    }
//
//}
