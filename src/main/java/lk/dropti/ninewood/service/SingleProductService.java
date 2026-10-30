package lk.dropti.ninewood.service;

import com.google.gson.JsonObject;
import lk.dropti.ninewood.dto.ProductDTO;
import lk.dropti.ninewood.entity.Model;
import lk.dropti.ninewood.entity.Stock;
import lk.dropti.ninewood.util.AppUtil;
import lk.dropti.ninewood.util.HibernateUtil;
import lk.dropti.ninewood.validation.Validator;
import org.hibernate.Session;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SingleProductService {
//    public String getSimilarProducts(String sId) {
//        JsonObject responseObject = new JsonObject();
//        boolean status = false;
//        String message = "";
//
//        if (sId == null || sId.isBlank()) {
//            message = "Product ID not found!";
//        } else if (!sId.matches(Validator.IS_INTEGER)) {
//            message = "Invalid product number!";
//        } else {
//            int stockId = Integer.parseInt(sId);
//            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//            Stock stock = hibernateSession.find(Stock.class, stockId);
//            if (stock == null) {
//                message = "Product not found!";
//            } else {
//                List<Model> modelList = hibernateSession.createQuery("FROM Model m WHERE m.brand=:brand", Model.class)
//                        .setParameter("brand", stock.getProduct().getModel().getBrand())
//                        .getResultList();
//                // sub query
//                List<Stock> stockList = hibernateSession.createQuery("FROM Stock s WHERE s.product.model IN :modelList AND s != :stock", Stock.class)
//                        .setParameter("modelList", modelList)
//                        .setParameter("stock", stock)
//                        .getResultList();
//                List<ProductDTO> productDTOList = new ArrayList<>();
//                for (Stock s : stockList) {
//                    ProductDTO productDTO = new ProductDTO();
//                    productDTO.setStockId(s.getId());
//                    productDTO.setTitle(s.getProduct().getTitle());
//                    productDTO.setImages(s.getProduct().getImages());
//                    productDTO.setPrice(s.getPrice());
////                    productDTO.setStorageValue(s.getProduct().getStorage().getValue());
//                    productDTO.setColorValue(s.getProduct().getColor().getValue());
//                    productDTOList.add(productDTO);
//                }
//                responseObject.add("similarProducts",AppUtil.GSON.toJsonTree(productDTOList));
//                status=true;
//            }
//            hibernateSession.close();
//        }
//
//        responseObject.addProperty("status", status);
//        responseObject.addProperty("message", message);
//        return AppUtil.GSON.toJson(responseObject);
//    }


     ////////////////////////////////////////////////////////



//    public String getSingleProduct(String sId) {
//        System.out.println("getSingleProduct");
//        JsonObject responseObject = new JsonObject();
//        boolean status = false;
//        String message = "";
//        if (sId == null || sId.isBlank()) {
//            message = "Product number not found!";
//        } else if (!sId.matches(Validator.IS_INTEGER)) {
//            message = "Invalid product number!";
//        } else {
//            int stockId = Integer.parseInt(sId);
//            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//            Stock stock = hibernateSession.find(Stock.class, stockId);
//
//            ProductDTO productDTO = new ProductDTO();
//            productDTO.setStockId(stock.getId());
//            productDTO.setTitle(stock.getProduct().getTitle());
//            productDTO.setPrice(stock.getPrice());
//            productDTO.setQty(stock.getQty());
//            productDTO.setBrandName(stock.getProduct().getModel().getBrand().getName());
////            productDTO.setModelName(stock.getProduct().getModel().getName());
//            productDTO.setQualityValue(stock.getProduct().getQuality().getValue());
//            productDTO.setImages(stock.getProduct().getImages());
//            productDTO.setColorValue(stock.getProduct().getColor().getValue());
////            productDTO.setStorageValue(stock.getProduct().getStorage().getValue());
//            productDTO.setDescription(stock.getProduct().getDescription());
//
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd yyyy");
//            productDTO.setCreatedAt(formatter.format(stock.getCreatedAt()));
//            responseObject.add("singleProduct", AppUtil.GSON.toJsonTree(productDTO));
//            status = true;
//            hibernateSession.close();
//        }
//        responseObject.addProperty("status", status);
//        responseObject.addProperty("message", message);
//        return AppUtil.GSON.toJson(responseObject);
//    }


    public String getSingleProduct(String sId) {
        System.out.println("getSingleProduct called with Stock ID: " + sId);
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (sId == null || sId.isBlank()) {
            message = "Product number not found!";
        } else if (!sId.matches(Validator.IS_INTEGER)) {
            message = "Invalid product number!";
        } else {
            int stockId = Integer.parseInt(sId);
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            try {
                Stock stock = hibernateSession.find(Stock.class, stockId);

                if (stock != null && stock.getProduct() != null) {
                    ProductDTO productDTO = new ProductDTO();
                    productDTO.setStockId(stock.getId());
                    productDTO.setTitle(stock.getProduct().getTitle());
                    productDTO.setPrice(stock.getPrice());
                    productDTO.setQty(stock.getQty());

                    if (stock.getProduct().getModel() != null) {
                        if (stock.getProduct().getModel().getBrand() != null) {
                            productDTO.setBrandName(stock.getProduct().getModel().getBrand().getName());
                        }
                    }

                    if (stock.getProduct().getQuality() != null) {
                        productDTO.setQualityValue(stock.getProduct().getQuality().getValue());
                    }
                    if (stock.getProduct().getColor() != null) {
                        productDTO.setColorValue(stock.getProduct().getColor().getValue());
                    }

                    productDTO.setDescription(stock.getProduct().getDescription());

                    if (stock.getProduct().getImages() != null) {
                        productDTO.setImages(new ArrayList<>(stock.getProduct().getImages()));
                    } else {
                        productDTO.setImages(new ArrayList<>());
                    }

                    if (stock.getCreatedAt() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd yyyy");
                        productDTO.setCreatedAt(formatter.format(stock.getCreatedAt()));
                    }

                    responseObject.add("singleProduct", AppUtil.GSON.toJsonTree(productDTO));
                    status = true;
                    message = "Product loaded successfully! 🚀";

                } else {
                    message = "Furniture stock item not found in the database!";
                }

            } catch (Exception e) {
                e.printStackTrace();
                message = "Server Error: " + e.getMessage();
            } finally {
                hibernateSession.close();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return AppUtil.GSON.toJson(responseObject);
    }
}
