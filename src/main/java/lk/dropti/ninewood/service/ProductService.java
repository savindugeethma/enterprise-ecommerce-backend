//package lk.dropti.ninewood.service;
//
//import jakarta.servlet.ServletContext;
//import lk.dropti.ninewood.entity.Category;
//import lk.dropti.ninewood.entity.Product;
//import lk.dropti.ninewood.entity.Product_Images; // 🎯 දැන් මේ Import එක ලස්සනට වැඩ මචං
//import lk.dropti.ninewood.entity.Stock;
//import lk.dropti.ninewood.util.HibernateUtil;
//import org.glassfish.jersey.media.multipart.ContentDisposition;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//import java.io.InputStream;
//
//public class ProductService {
//
//    private final ServletContext servletContext;
//
//    public ProductService(ServletContext servletContext) {
//        this.servletContext = servletContext;
//    }
//
//    public boolean saveProductWithStock(String name, int categoryId, double price, int qty,
//                                        String colour, String model, String description, boolean isHeavy,
//                                        InputStream img1Stream, ContentDisposition img1Detail,
//                                        InputStream img2Stream, ContentDisposition img2Detail) throws Exception {
//
//        FileUploadService fileUploadService = new FileUploadService(servletContext);
//        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//        Transaction transaction = null;
//
//        try {
//            transaction = hibernateSession.beginTransaction();
//
//            // 1. Product Entity එක හදලා සෙට් කරනවා
//            Product product = new Product();
//            product.setTitle(name);
//            product.setDescription(description);
//            product.setHeavy(isHeavy);
//            product.setCreatedAt(java.time.LocalDateTime.now());
//            product.setUpdatedAt(java.time.LocalDateTime.now());
//
//            Category category = hibernateSession.find(Category.class, categoryId);
//            if (category == null) {
//                throw new Exception("Selected Category not found!");
//            }
//            product.setCategory(category);
//
//            // 💡 Product එක මුලින්ම පර්සිස්ට් කරනවා (එතකොට එයාට ID එකක් ජෙනරේට් වෙනවා)
//            hibernateSession.persist(product);
//
//            // ==========================================================
//            // 📸 🎰 HIBERNATE ENTITY FILE UPLOAD LOGIC
//            // ==========================================================
//
//            // පළවෙනි ඉමේජ් එක තිබ්බොත් ඒක Product_Images Entity එක හරහා සේව් කරනවා
//            if (img1Stream != null && img1Detail.getFileName() != null && !img1Detail.getFileName().isEmpty()) {
//                FileUploadService.FileItem fileItem1 = fileUploadService.uploadFile("product", img1Stream, img1Detail);
//
//                Product_Images productImage1 = new Product_Images();
//                productImage1.setProduct(product); // 🎯 කෙලින්ම Product Object එකම දුන්නා මචං (Hibernate මැජික්)
//                productImage1.setImage(fileItem1.getUrl()); // Path එක සෙට් කරා
//
//                hibernateSession.persist(productImage1);
//            }
//
//            // දෙවැනි ඉමේජ් එක තිබ්බොත් ඒකත් ඒ විදිහටම සේව් කරනවා
//            if (img2Stream != null && img2Detail.getFileName() != null && !img2Detail.getFileName().isEmpty()) {
//                FileUploadService.FileItem fileItem2 = fileUploadService.uploadFile("product", img2Stream, img2Detail);
//
//                Product_Images productImage2 = new Product_Images();
//                productImage2.setProduct(product);
//                productImage2.setImage(fileItem2.getUrl());
//
//                hibernateSession.persist(productImage2);
//            }
//            // ==========================================================
//
//            Stock stock = new Stock();
//            stock.setProduct(product);
//            stock.setPrice(price);
//            stock.setQty(qty);
//            stock.setCreatedAt(java.time.LocalDateTime.now());
//            stock.setUpdatedAt(java.time.LocalDateTime.now());
//
//            lk.dropti.ninewood.entity.Status stockStatus = hibernateSession.find(lk.dropti.ninewood.entity.Status.class, 2);
//            if (stockStatus == null) {
//                throw new Exception("Stock Status (ID: 2) not found in Database!");
//            }
//            stock.setStatus(stockStatus);
//
//            hibernateSession.persist(stock);
//
//            transaction.commit();
//            return true;
//
//        } catch (Exception e) {
//            if (transaction != null) transaction.rollback();
//            e.printStackTrace();
//            throw e;
//        } finally {
//            hibernateSession.close();
//        }
//    }
//} // 💡 Parsing Error එක සදහටම ඉවර කරන්න සගල වරහන නූලට වැහුවා!


package lk.dropti.ninewood.service;

import jakarta.servlet.ServletContext;
import lk.dropti.ninewood.entity.*;
import lk.dropti.ninewood.util.HibernateUtil;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.io.InputStream;
import java.util.ArrayList;

public class ProductService {

    private final ServletContext servletContext;

    public ProductService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public boolean saveProductWithStock(String name, int categoryId, int colorId, int sellerId, double price, int qty,
                                        String modelName, String description, boolean isHeavy,
                                        InputStream img1Stream, ContentDisposition img1Detail,
                                        InputStream img2Stream, ContentDisposition img2Detail) throws Exception {

        FileUploadService fileUploadService = new FileUploadService(servletContext);
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = hibernateSession.beginTransaction();

            // 1. Product Object එක හදනවා
            Product product = new Product();
            product.setTitle(name);
            product.setDescription(description);
            product.setHeavy(isHeavy);
            product.setCreatedAt(java.time.LocalDateTime.now());
            product.setUpdatedAt(java.time.LocalDateTime.now());

            // link Category
            Category category = hibernateSession.find(Category.class, categoryId);
            if (category == null) {
                throw new Exception("Selected Category not found!");
            }
            product.setCategory(category);

            // link Color (front end walin awa eka)
            Color color = hibernateSession.find(Color.class, colorId);
            if (color == null) {
                throw new Exception("Selected Color not found!");
            }
            product.setColor(color);

            // link Seller (logged seller)
            Seller seller = hibernateSession.find(Seller.class, sellerId);
            if (seller == null) {
                throw new Exception("Logged-in Seller not found!");
            }
            product.setSeller(seller);

            // link Quality (default 1- new)
            Quality defaultQuality = hibernateSession.find(Quality.class, 1);
            if (defaultQuality == null) {
                throw new Exception("Default Quality (ID: 1) not found in Database!");
            }
            product.setQuality(defaultQuality);

            // link Model  (Default ID: 1)
            Model defaultModel = hibernateSession.find(Model.class, 1);
            if (defaultModel == null) {
                throw new Exception("Default Model (ID: 1) not found in Database!");
            }
            product.setModel(defaultModel);



            // IMAGE UPLOAD LOGIC

            product.setImages(new ArrayList<>());

            if (img1Stream != null && img1Detail.getFileName() != null && !img1Detail.getFileName().isEmpty()) {
                FileUploadService.FileItem fileItem1 = fileUploadService.uploadFile("product", img1Stream, img1Detail);

                String rawUrl1 = fileItem1.getUrl();
                if (rawUrl1 != null && rawUrl1.contains("uploads")) {
                    String cleanUrl1 = "uploads" + rawUrl1.split("uploads")[1];
                    product.getImages().add(cleanUrl1);
                } else {
                    product.getImages().add(rawUrl1);
                }
            }

            if (img2Stream != null && img2Detail.getFileName() != null && !img2Detail.getFileName().isEmpty()) {
                FileUploadService.FileItem fileItem2 = fileUploadService.uploadFile("product", img2Stream, img2Detail);

                String rawUrl2 = fileItem2.getUrl();
                if (rawUrl2 != null && rawUrl2.contains("uploads")) {
                    String cleanUrl2 = "uploads" + rawUrl2.split("uploads")[1];
                    product.getImages().add(cleanUrl2);
                } else {
                    product.getImages().add(rawUrl2);
                }
            }


            // Product db saved
            hibernateSession.persist(product);



            // 2. Stock Entity  (Status: 2)

            Stock stock = new Stock();
            stock.setProduct(product);
            stock.setPrice(price);
            stock.setQty(qty);
            stock.setCreatedAt(java.time.LocalDateTime.now());
            stock.setUpdatedAt(java.time.LocalDateTime.now());

            Status stockStatus = hibernateSession.find(Status.class, 2);
            if (stockStatus == null) {
                throw new Exception("Stock Status (ID: 2) not found in Database!");
            }
            stock.setStatus(stockStatus);

            hibernateSession.persist(stock);

            transaction.commit();
            return true;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            hibernateSession.close();
        }
    }



}