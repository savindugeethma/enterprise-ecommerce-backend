package lk.dropti.ninewood.controller.api;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.dropti.ninewood.annotation.IsUser;
import lk.dropti.ninewood.entity.User;
import lk.dropti.ninewood.entity.util.AppUtil;
import lk.dropti.ninewood.service.AdminService;
import lk.dropti.ninewood.service.ProfileService;

@Path("/admin-profile")
public class AdminController {
    @IsUser
    @Path("/admin-overview")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadOverview(@Context HttpServletRequest request) {
        String responseJson = new AdminService().loadOverview(request);
        return Response.ok().entity(responseJson).build();
//        System.out.println("Loading Overview");
    }

//    @GET
//    @Path("/overview")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getDashboardOverview() {
//        System.out.println("======> AdminController: getDashboardOverview API called!");
//        String responseJson = new AdminService().getAdminDashboardOverview();
//        return Response.ok().entity(responseJson).build();
//    }

    @GET
    @Path("/overview")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboardOverview(@Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        HttpSession httpSession = request.getSession();
        User user = (User) httpSession.getAttribute("user");

        if (user == null) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Please log in first!");
            return Response.status(Response.Status.UNAUTHORIZED).entity(AppUtil.GSON.toJson(responseObject)).build();
        }

        if (user.getStatus().getId() != 3) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Unauthorized access! Only sellers allowed.");
            return Response.status(Response.Status.FORBIDDEN).entity(AppUtil.GSON.toJson(responseObject)).build();
        }

        String responseJson = new AdminService().getSellerDashboardOverview(user.getId());
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Path("/products")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSellerProducts(@Context HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        User user = (User) httpSession.getAttribute("user");

        if (user == null || user.getStatus().getId() != 3) {
            JsonObject err = new JsonObject();
            err.addProperty("status", false);
            return Response.status(Response.Status.UNAUTHORIZED).entity(AppUtil.GSON.toJson(err)).build();
        }

        String responseJson = new AdminService().getSellerProducts(user.getId());
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Path("/customers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSellerCustomers(@Context HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        User user = (User) httpSession.getAttribute("user");

        if (user == null || user.getStatus().getId() != 3) {
            JsonObject err = new JsonObject();
            err.addProperty("status", false);
            return Response.status(Response.Status.UNAUTHORIZED).entity(AppUtil.GSON.toJson(err)).build();
        }

        String responseJson = new AdminService().getSellerCustomers(user.getId());
        return Response.ok().entity(responseJson).build();
    }


    @IsUser
    @Path("/admin-products")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadProducts(@Context HttpServletRequest request) {
        String responseJson = new AdminService().loadProducts(request);
        System.out.println("Loading loadProducts");
        return Response.ok().entity(responseJson).build();
    }

    @IsUser
    @Path("/admin-orders")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadOrders(@Context HttpServletRequest request) {
        String responseJson = new AdminService().loadOrders(request);
        return Response.ok().entity(responseJson).build();
//        return null;
    }

    @IsUser
    @Path("/admin-customers")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadCustomers(@Context HttpServletRequest request) {
        System.out.println("Loading loadCustomers");
        String responseJson = new AdminService().loadCustomers(request);
        return Response.ok().entity(responseJson).build();
//        return null;
    }

    @IsUser
    @Path("/update-order-details")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrderDetails(
            @Context HttpServletRequest request,
            @FormParam("orderId") String orderId,
            @FormParam("status") String status,
            @FormParam("trackingNumber") String trackingNumber) {

        System.out.println("Executing updateOrderDetails controller...");

        String responseJson = new AdminService().updateOrderDetails(request, orderId, status, trackingNumber);

        return Response.ok().entity(responseJson).build();
    }

}
