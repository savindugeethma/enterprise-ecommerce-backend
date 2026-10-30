//package lk.dropti.ninewood.entity;
//
//import jakarta.persistence.*;
//
//import java.io.Serializable;
//
//@Entity
//public class Cart implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @Column(nullable = false)
//    private int qty;
//
//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "users_id")
//    private User user;
//
//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "stock_id")
//    private Stock stock;
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
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public Stock getStock() {
//        return stock;
//    }
//
//    public void setStock(Stock stock) {
//        this.stock = stock;
//    }
//
//}
//
////package lk.dropti.ninewood.entity;
////
////import jakarta.persistence.*;
////
////import java.io.Serializable;
////
////@Entity
////public class Cart implements Serializable {
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private int id;
////
////
////    @Column(nullable = false)
////    private int qty;
////
////    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
////    @JoinColumn(name = "users_id")
////    private User user;
////
////    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
////    @JoinColumn(name = "stock_id")
////    private Stock stock;
////
////    public int getId() {
////        return id;
////    }
////
////    public void setId(int id) {
////        this.id = id;
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
////    public User getUser() {
////        return user;
////    }
////
////    public void setUser(User user) {
////        this.user = user;
////    }
////
////    public Stock getStock() {
////        return stock;
////    }
////
////    public void setStock(Stock stock) {
////        this.stock = stock;
////    }
////}

package lk.dropti.ninewood.entity.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "cart")
public class Cart implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int qty;

    // ❌ NO CASCADE on ManyToOne
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    // ❌ NO CASCADE on ManyToOne
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    // ===== Getters & Setters =====

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}
