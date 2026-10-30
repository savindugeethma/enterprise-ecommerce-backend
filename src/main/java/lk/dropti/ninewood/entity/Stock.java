//package lk.dropti.ninewood.entity;
//
//import jakarta.persistence.*;
//
////@Entity
////public class Stock extends BaseEntity{
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private int id;
////
////    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
////    @JoinColumn(name = "product_id")
////    private Product product;
////
////    @Column(nullable = false)
////    private double price;
////
////    @Column(nullable = false)
////    private int qty;
////
//////    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
//////    @JoinColumn(name = "discount_id")
//////    private Discount discount;
////
////    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
////    @JoinColumn(name = "status_id")
////    private Status status;
////
////    public int getId() {
////        return id;
////    }
////
////    public void setId(int id) {
////        this.id = id;
////    }
////
////    public Product getProduct() {
////        return product;
////    }
////
////    public void setProduct(Product product) {
////        this.product = product;
////    }
////
////    public double getPrice() {
////        return price;
////    }
////
////    public void setPrice(double price) {
////        this.price = price;
////    }
////
////    public int getQty() {
////        return qty;
////    }
////
////    public void setQty(int qty) {
////        this.qty = qty;
////    }
////
//////    public Discount getDiscount() {
//////        return discount;
//////    }
//////
//////    public void setDiscount(Discount discount) {
//////        this.discount = discount;
//////    }
////
////    public Status getStatus() {
////        return status;
////    }
////
////    public void setStatus(Status status) {
////        this.status = status;
////    }
////}
//
//@Entity
//public class Stock extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
//
//    @Column(nullable = false)
//    private double price;
//
//    @Column(nullable = false)
//    private int qty;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "status_id", nullable = false)
//    private Status status;
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public Product getProduct() {
//        return product;
//    }
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public int getQty() {
//        return qty;
//    }
//
//    public void setQty(int qty) {
//        this.qty = qty;
//    }
//
//    public Status getStatus() {
//        return status;
//    }
//
//    public void setStatus(Status status) {
//        this.status = status;
//    }
//
//    // getters & setters
//}


package lk.dropti.ninewood.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stock")
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Stock always belongs to one Product
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int qty;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    // ===== Getters & Setters =====

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
