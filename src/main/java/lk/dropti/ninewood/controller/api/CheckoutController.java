package lk.dropti.ninewood.controller.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.dropti.ninewood.service.CheckoutService;

@Path("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService = new CheckoutService();

    @POST
    @Path("/place-cod")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeCODOrder(String jsonPayload, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        try {
            JsonObject orderData = JsonParser.parseString(jsonPayload).getAsJsonObject();
            HttpSession session = request.getSession();

            responseObject = checkoutService.processCODOrder(orderData, session);

            return Response.ok(responseObject.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Server Error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObject.toString()).build();
        }
    }

    @POST
    @Path("/initiate-card-payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response initiateCardPayment(String jsonPayload, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        try {
            JsonObject orderData = JsonParser.parseString(jsonPayload).getAsJsonObject();
            HttpSession session = request.getSession();

            responseObject = checkoutService.processCardPaymentInitiation(orderData, session);

            return Response.ok(responseObject.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Server Error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObject.toString()).build();
        }
    }

    @GET
    @Path("/order-history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderHistory(@Context HttpServletRequest request) {

        System.out.println("======> CheckoutController: getOrderHistory API called!");

        String responseJson = checkoutService.getUserOrderHistory(request);

        return Response.ok().entity(responseJson).build();
    }
}
