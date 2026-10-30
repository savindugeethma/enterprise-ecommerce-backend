package lk.dropti.ninewood.controller.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
//import lk.dropti.ninewood.service.CityService;
import lk.dropti.ninewood.service.ContentService;

@Path("/data")
public class ContentController {
    @Path("/new-arrivals")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadNewArrivals() {
        System.out.println("Conetnt controller New Arrivals");
        String responseJson = new ContentService().loadNewArrivalProducts();
        return Response.ok().entity(responseJson).build();
    }

    @Path("/tab-content")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response TabContent() {
        System.out.println("Conetnt controller New Arrivals");
        String responseJson = new ContentService().loadNewArrivalProducts();
        return Response.ok().entity(responseJson).build();
    }

//    @Path("/all-products")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response allProducts() {
//        System.out.println("Conetnt controller All Products");
//        String responseJson = new ContentService().loadAllProducts();
//        return Response.ok().entity(responseJson).build();
//    }

    @Path("/all-products")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response allProducts(
            @QueryParam("search") String search,
            @QueryParam("categories") String categories,
            @QueryParam("colors") String colors,
            @QueryParam("sizes") String sizes) {

        System.out.println("Content controller: Fetching products with dynamic sorting & filters");

        String responseJson = new ContentService().loadAllProducts(search, categories, colors, sizes);
        return Response.ok().entity(responseJson).build();
    }

//    @Path("/product-data")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response loadProductData() {
//        String responseJson = new ContentService().loadProductData();
//        return Response.ok().entity(responseJson).build();
//    }


//    @Path("/cities")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response loadCities() {
//        String loadAllCities = new CityService().loadAllCities();
//        return Response.ok().entity(loadAllCities).build();
//    }
//
//    @Path("/brands")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response loadBrands() {
//        String responseJson = new ContentService().loadBrandDetails();
//        return Response.ok().entity(responseJson).build();
//    }
//
//    @Path("/{brandId}/models")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response loadModels(@PathParam("brandId") int id) {
//        String responseJson = new ContentService().loadModelDetails(id);
//        return Response.ok().entity(responseJson).build();
//    }
//
//    @Path("/specifications")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response loadSpecifications() {
//        String responseJson = new ContentService().loadProductSpecifications();
//        return Response.ok().entity(responseJson).build();
//    }
}
