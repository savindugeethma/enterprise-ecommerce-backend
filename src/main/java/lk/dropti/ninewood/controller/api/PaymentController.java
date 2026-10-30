//package lk.dropti.ninewood.controller.api;
//
//import com.google.gson.JsonObject;
//import com.stripe.Stripe;
//import com.stripe.model.PaymentIntent;
//import com.stripe.param.PaymentIntentCreateParams;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import lk.dropti.ninewood.util.AppUtil;
//import lk.dropti.ninewood.util.Env;
//
//@Path("/payment")
//public class PaymentController {
//
//    @POST
//    @Path("/checkout")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response processPayment(String jsonData) {
//        JsonObject responseObject = new JsonObject();
//        try {
//            Stripe.apiKey = "sk_test_51Qko9aBs1F8yxRot4qYMWeknqVTqE9bJfHPJwnMCYajkuYbZAPj2VgIKZsowKKwRppkuefey8GxSmmBcG9QpddbS00mihjYEJd";
//            JsonObject requestData = AppUtil.GSON.fromJson(jsonData, JsonObject.class);
//            if (requestData == null || !requestData.has("amount")) {
//                responseObject.addProperty("status", false);
//                responseObject.addProperty("message", "Amount is required!");
//                return Response.status(Response.Status.BAD_REQUEST).entity(AppUtil.GSON.toJson(responseObject)).build();
//            }
//
//            long totalAmount = requestData.get("amount").getAsLong();
//
//            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
//                    .setAmount(totalAmount)
//                    .setCurrency("lkr")
//                    .addPaymentMethodType("card")
//                    .build();
//
//            PaymentIntent intent = PaymentIntent.create(params);
//
//            responseObject.addProperty("status", true);
//            responseObject.addProperty("clientSecret", intent.getClientSecret());
//            return Response.ok().entity(AppUtil.GSON.toJson(responseObject)).build();
//
//        } catch (Exception e) {
//            System.out.println("\u001B[31m❌ [STRIPE ERROR]: Payment initiation failed!\u001B[0m");
//            e.printStackTrace();
//            responseObject.addProperty("status", false);
//            responseObject.addProperty("message", "Payment processing failed. Please try again!");
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(AppUtil.GSON.toJson(responseObject)).build();
//        }
//    }
//}


package lk.dropti.ninewood.controller.api;

import com.google.gson.JsonObject;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.dropti.ninewood.entity.*;
import lk.dropti.ninewood.util.AppUtil;
import lk.dropti.ninewood.util.HibernateUtil;
//import lk.dropti.ninewood.model.*; // 👈 උඹේ Models ඔක්කොම Import කරගන්න මචං
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.ArrayList;
import java.util.List;

@Path("/payment")
public class PaymentController {

    // 🚀 1. මුලින්ම Stripe එකට Request එක දාලා සල්ලි කපන්න ලෑස්ති කරන API එක
    @POST
    @Path("/checkout")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processPayment(String jsonData, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        try {
            Stripe.apiKey = "sk_test_51Qko9aBs1F8yxRot4qYMWeknqVTqE9bJfHPJwnMCYajkuYbZAPj2VgIKZsowKKwRppkuefey8GxSmmBcG9QpddbS00mihjYEJd"; // 👈 උඹේ ඇත්තම දිග Key එක

            JsonObject requestData = AppUtil.GSON.fromJson(jsonData, JsonObject.class);
            long totalAmount = requestData.get("amount").getAsLong();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(totalAmount)
                    .setCurrency("lkr")
                    .addPaymentMethodType("card")
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            responseObject.addProperty("status", true);
            responseObject.addProperty("clientSecret", intent.getClientSecret());
            return Response.ok().entity(AppUtil.GSON.toJson(responseObject)).build();

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Payment session failed!");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(AppUtil.GSON.toJson(responseObject)).build();
        }
    }

    // 🚀 2. [NEW] Stripe එකෙන් සල්ලි කැපුණාට පස්සේ උඹේම COD Logic එකෙන් DB එකට දාන API එක මචං!
    @POST
    @Path("/save-order")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveStripeOrder(String jsonData, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        HttpSession httpSession = request.getSession();
        User user = (User) httpSession.getAttribute("user");

        if (user == null) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Please login to place an order!");
            return Response.status(Response.Status.UNAUTHORIZED).entity(AppUtil.GSON.toJson(responseObject)).build();
        }

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            List<Cart> cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user.id=:id", Cart.class)
                    .setParameter("id", user.getId())
                    .getResultList();

            if (cartList.isEmpty()) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Cart is empty!");
                return Response.ok().entity(AppUtil.GSON.toJson(responseObject)).build();
            }

            transaction = hibernateSession.beginTransaction();

            double subtotal = 0;
            boolean hasHeavyItem = false;

            for (Cart cart : cartList) {
                Stock stock = hibernateSession.find(Stock.class, cart.getStock().getId());
                subtotal += (cart.getQty() * stock.getPrice());

                if (stock.getProduct() != null && stock.getProduct().isHeavy()) {
                    hasHeavyItem = true;
                }
            }

            int deliveryTypeId = hasHeavyItem ? 1 : 2;
            double shippingFee = hasHeavyItem ? 5000.0 : 500.0;
            double grandTotal = subtotal + shippingFee;

            Status orderStatus = hibernateSession.find(Status.class, 1); // 1 = Pending

            // ==========================================================
            // 🎯 ORDERS TABLE INSERT (Stripe විදිහට)
            // ==========================================================
            Orders order = new Orders();
            order.setUser(user);
            order.setPrice(grandTotal);
            order.setStatus(orderStatus);
            order.setDeliveryTypesId(deliveryTypeId);
            order.setPaymentTypesId(2); // 🔥 2 = Card Payments (Stripe) මචං!

            hibernateSession.persist(order);

            // ORDER_ITEMS INSERT & STOCK REDUCTION

            for (Cart cart : cartList) {
                // 1. Stock එක Hibernate Session එකෙන් direct ගන්නවා (Proxy ලෙඩ නැති වෙන්න)
                Stock stock = (Stock) hibernateSession.get(Stock.class, cart.getStock().getId());

                if (stock.getQty() < cart.getQty()) {
                    throw new Exception("Sorry, product is out of stock!");
                }

                // 2. Stock එක අඩු කරලා Merge කරනවා
                stock.setQty(stock.getQty() - cart.getQty());
                stock.setUpdatedAt(java.time.LocalDateTime.now());
                hibernateSession.merge(stock);

                // 3. අලුත් මල කරදරයක් නැති OrderItem එකක් හැදීම
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setStock(stock);
                orderItem.setQty(cart.getQty());
                orderItem.setRating(null);

                // 4. Seller Allotment එක සරලව IDs වලින් විතරක් හැන්ඩ්ල් කරනවා (Infinite Loop එක බ්ලොක් කරන්න)
                if (stock.getProduct() != null && stock.getProduct().getSeller() != null) {
                    Seller managedSeller = (Seller) hibernateSession.get(Seller.class, stock.getProduct().getSeller().getId());
                    orderItem.setSeller(managedSeller);
                } else {
                    Seller defaultSeller = (Seller) hibernateSession.get(Seller.class, 1);
                    orderItem.setSeller(defaultSeller);
                }

                // 5. සේව් කිරීමට පෙර සෙලර්ගේ ලිස්ට් වලට මේක දාන්න යන්න එපා, Direct Persist කරන්න මචං!
                hibernateSession.persist(orderItem);

                // 🔥 [පට්ටම වැදගත්]: හැම අයිටම් එකක්ම සේව් වුණු ගමන් cache එක ක්ලීන් කරනවා ලූප් එක හිර නොවෙන්න!
                hibernateSession.flush();
            }


            //CLEAR CART

            hibernateSession.createQuery("DELETE FROM Cart c WHERE c.user.id=:id")
                    .setParameter("id", user.getId())
                    .executeUpdate();

            transaction.commit();

            responseObject.addProperty("status", true);
            responseObject.addProperty("orderId", "INV-" + order.getId());
            responseObject.addProperty("message", "Your Stripe card order has been placed successfully!");
            return Response.ok().entity(AppUtil.GSON.toJson(responseObject)).build();

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Order Error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(AppUtil.GSON.toJson(responseObject)).build();
        } finally {
            hibernateSession.close();
        }
    }
}