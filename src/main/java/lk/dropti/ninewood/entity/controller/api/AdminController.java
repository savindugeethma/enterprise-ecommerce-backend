package lk.dropti.ninewood.entity.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.dropti.ninewood.annotation.IsUser;
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
}
