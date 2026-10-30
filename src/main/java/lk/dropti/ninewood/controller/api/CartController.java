package lk.dropti.ninewood.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.dropti.ninewood.service.CartService;

@Path("/carts")
public class CartController {
    private final CartService cartService = new CartService();

//    @Path("/remove-cart/{cartId}")
//    @DELETE
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response removeCartItem(@PathParam("cartId") String cartId, @Context HttpServletRequest request) {
//        String responseJson = cartService.deleteCartItem(cartId, request);
//        return Response.ok().entity(responseJson).build();
//    }
    @GET
    @Path("/update-cart-qty")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartQty(
            @QueryParam("cartId") Long cartId,
            @QueryParam("action") String action,
            @Context HttpServletRequest request) {

        System.out.println("Cart controller: Updating qty for cart items...");

        String responseJson = cartService.updateCartQuantity(request, cartId, action);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Path("/delete-cart-item")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCartItem(
            @QueryParam("sId") String sId,
            @Context HttpServletRequest request) {

        System.out.println("delete cart item controller called");

        String responseJson = cartService.deleteCartItem(sId, request);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Path("/add-to-cart")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(
            @QueryParam("sId") String sId,
            @QueryParam("qty") String qty,
            @Context HttpServletRequest request) {

        String responseJson = cartService.addToCart(sId, qty, request);
        System.out.println("addtocart controller called");
        return Response.ok().entity(responseJson).build();
    }

    @Path("/all-carts")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadAllCarts(@Context HttpServletRequest request) {
        String responseJson = cartService.getAllUserCarts(request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/test123/{cartQty}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response deUserCarts(@PathParam("cartQty") String cartQty, @Context HttpServletRequest request) {
        String responseJson = cartService.deCarts(cartQty, request);
        return Response.ok().entity(responseJson).build();
    }

//    @Path("/delete-carts")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response deleteCartItem(@Context HttpServletRequest request) {
//        String responseJson = cartService.deleteCartItem(request);
//        return Response.ok().entity(responseJson).build();
//    }

//    @Path("/add-to-cart")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response addToCart(@QueryParam("sId") String sId,
//                              @QueryParam("qty") String qty,
//                              @Context HttpServletRequest request) {
//        String responseJson = cartService.addToCart(sId, qty, request);
//        return Response.ok().entity(responseJson).build();
//    }

}
