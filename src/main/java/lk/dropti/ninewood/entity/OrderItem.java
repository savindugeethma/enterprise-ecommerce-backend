//package lk.dropti.ninewood.entity;
//
//import jakarta.persistence.*;
//
//import java.io.Serializable;
//
//@Entity
//@Table(name = "order_items")
//public class OrderItem implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @Column(nullable = false)
//    private int qty;
//
//    private Integer rating;
//
//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id")
//    private Orders order;
//
//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "stock_id")
//    private Stock stock;
//
//    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
//    @JoinColumn(name = "seller_id")
//    private Seller seller;
//
//    public Seller getSeller() {
//        return seller;
//    }
//
//    public void setSeller(Seller seller) {
//        this.seller = seller;
//    }
//
//    public void setStock(Stock stock) {
//        this.stock = stock;
//    }
//
//    public Stock getStock() {
//        return stock;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getQty() {
//        return qty;
//    }
//
//    public void setQty(int qty) {
//        this.qty = qty;
//    }
////
////    public int getRating() {
////        return rating;
////    }
////
////    public void setRating(int rating) {
////        this.rating = rating;
////    }
//
//    public Integer getRating() {
//        return rating;
//    }
//
//    public void setRating(Integer rating) {
//        this.rating = rating;
//    }
//
//    public Orders getOrder() {
//        return order;
//    }
//
//    public void setOrder(Orders order) {
//        this.order = order;
//    }
//}

package lk.dropti.ninewood.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "order_items") // 💡 ERD එකේ තියෙන්නේ order_items කියලා මචං
public class OrderItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "qty", nullable = false)
    private int qty;

    // 💡 ERD එකේ rating VARCHAR(45) නිසා String කරනවා නම් වඩා හොඳයි,
    // හැබැයි Integer වුණත් අවුලක් නැහැ. දැනට Integerම තියාගමු මචං.
    @Column(name = "rating")
    private Integer rating;

    // 🎯 Cascade එක MERGE කරා. ERD එකේ Column Name එක orders_id
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders order;

    // 🎯 Cascade එක MERGE කරා. ERD එකේ Column Name එක stock_id
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    // 🎯 Cascade එක MERGE කරා. ERD එකේ Column Name එක seller_id
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    // ==========================================================
    // ⚙️ GETTERS AND SETTERS
    // ==========================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}