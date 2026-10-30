package lk.dropti.ninewood.controller.api;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

import lk.dropti.ninewood.service.ProductService;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;

@Path("/admin") // 💡 admin-dashboard.js එකේ URL එක මේකට මැච් වෙන්න ඕනේ (api/admin/add-product)
public class ProductController {
//
//    @POST
//    @Path("/add-product")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response addProduct(
//            @FormDataParam("name") String name,
//            @FormDataParam("category") String categoryId, // 💡 int වෙනුවට String කරා
//            @FormDataParam("price") String price,          // 💡 double වෙනුවට String කරා
//            @FormDataParam("qty") String qty,              // 💡 int වෙනුවට String කරා
//            @FormDataParam("colour") String colour,
//            @FormDataParam("model") String model,
//            @FormDataParam("description") String description,
//            @FormDataParam("isHeavy") boolean isHeavy, // 💡 Checkbox එකක් නිසා මේක අවුලක් නෑ
//
//            @FormDataParam("image1") InputStream image1Stream,
//            @FormDataParam("image1") FormDataContentDisposition image1Detail,
//            @FormDataParam("image2") InputStream image2Stream,
//            @FormDataParam("image2") FormDataContentDisposition image2Detail,
//            @Context ServletContext servletContext) {
//
//        JsonObject responseObject = new JsonObject();
//
//        try {
//            // 🎯 මෙතනදී අපි ආරක්ෂිතව Parse කරගන්නවා (හිස්ව ආවොත් 0 දානවා)
//            int parsedCategoryId = (categoryId != null && !categoryId.isEmpty()) ? Integer.parseInt(categoryId) : 0;
//            double parsedPrice = (price != null && !price.isEmpty()) ? Double.parseDouble(price) : 0.0;
//            int parsedQty = (qty != null && !qty.isEmpty()) ? Integer.parseInt(qty) : 0;
//
//            ProductService productService = new ProductService(servletContext);
//
//            // 🚀 අපේ සර්විස් එකට parsed වෙච්ච අගයන් ටික පාස් කරනවා
//            boolean isSaved = productService.saveProductWithStock(
//                    name, parsedCategoryId, parsedPrice, parsedQty, colour, model, description, isHeavy,
//                    image1Stream, image1Detail, image2Stream, image2Detail
//            );
//
//            if (isSaved) {
//                responseObject.addProperty("status", true);
//                responseObject.addProperty("message", "Product Saved Successfully! 🚀");
//            } else {
//                responseObject.addProperty("status", false);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            responseObject.addProperty("status", false);
//            responseObject.addProperty("message", "Error: " + e.getMessage());
//        }
//
//        return Response.ok().entity(new Gson().toJson(responseObject)).build();
//    }
//}


    @POST
    @Path("/add-product")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProduct(
            @FormDataParam("name") String name,
            @FormDataParam("category") String categoryId,
            @FormDataParam("color") String colorId,       // 💡 ෆ්‍රන්ට්-එන්ඩ් එකෙන් එවන කලර් ID එක (String විදිහට ගත්තා)
            @FormDataParam("price") String price,
            @FormDataParam("qty") String qty,
            @FormDataParam("colour") String colourText,   // (මේක වෙනම Text එකක් නම් ඒක)
            @FormDataParam("model") String model,
            @FormDataParam("description") String description,
            @FormDataParam("isHeavy") boolean isHeavy,

            @FormDataParam("image1") InputStream image1Stream,
            @FormDataParam("image1") FormDataContentDisposition image1Detail,
            @FormDataParam("image2") InputStream image2Stream,
            @FormDataParam("image2") FormDataContentDisposition image2Detail,
            @Context ServletContext servletContext) {

        JsonObject responseObject = new JsonObject();

        try {

            int parsedCategoryId = (categoryId != null && !categoryId.isEmpty()) ? Integer.parseInt(categoryId) : 0;
            int parsedColorId = (colorId != null && !colorId.isEmpty()) ? Integer.parseInt(colorId) : 1; // 💡
            int parsedSellerId = 1; //

            double parsedPrice = (price != null && !price.isEmpty()) ? Double.parseDouble(price) : 0.0;
            int parsedQty = (qty != null && !qty.isEmpty()) ? Integer.parseInt(qty) : 0;

            ProductService productService = new ProductService(servletContext);


            boolean isSaved = productService.saveProductWithStock(
                    name,
                    parsedCategoryId,
                    parsedColorId,
                    parsedSellerId,
                    parsedPrice,
                    parsedQty,
                    model,
                    description,
                    isHeavy,
                    image1Stream,
                    image1Detail,
                    image2Stream,
                    image2Detail
            );

            if (isSaved) {
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Product Saved Successfully! 🚀");
            } else {
                responseObject.addProperty("status", false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Error: " + e.getMessage());
        }

        return Response.ok().entity(new Gson().toJson(responseObject)).build();
    }


}