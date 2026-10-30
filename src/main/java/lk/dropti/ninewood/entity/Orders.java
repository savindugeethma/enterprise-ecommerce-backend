package lk.dropti.ninewood.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Orders extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "price")
    private Double price;

    @Column(name = "delivery_types_id")
    private int deliveryTypesId;

    @Column(name = "payment_types_id")
    private int paymentTypesId;



    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getPrice() {
        return price;
    }

    // 🎯 අන්න අර අඩු වෙලා තිබ්බ සෙටර් එක මෙතනට දැම්මා!
    public void setPrice(Double price) {
        this.price = price;
    }

    // Delivery & Payment IDs වලට සෙටර්ස් සහ ගෙටර්ස්
    public int getDeliveryTypesId() {
        return deliveryTypesId;
    }

    public void setDeliveryTypesId(int deliveryTypesId) {
        this.deliveryTypesId = deliveryTypesId;
    }

    public int getPaymentTypesId() {
        return paymentTypesId;
    }

    public void setPaymentTypesId(int paymentTypesId) {
        this.paymentTypesId = paymentTypesId;
    }
}







//
//package lk.dropti.ninewood.entity;
//
//import jakarta.persistence.*;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@Entity
//@Table(name = "orders")
//public class Orders extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//    @JoinColumn(name = "status_id")
//    private Status status;
//
//    // 💡 ERD එකේ තියෙන්නේ users_id කියලා මචං, ඒ නිසා name="users_id" කරා
//    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
//    @JoinColumn(name = "users_id")
//    private User user;
//
//    // 🎯 ERD එකට අනුව Delivery Types මැපින් එක එකතු කරා
//    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//    @JoinColumn(name = "delivery_types_id")
//    private DeliveryTypes deliveryTypes;
//
//    // 🎯 ERD එකට අනුව Payment Types මැපින් එක එකතු කරා
//    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//    @JoinColumn(name = "payment_types_id")
//    private PaymentTypes paymentTypes;
//
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    private List<OrderItem> orderItems = new ArrayList<>();
//
//    // 🛑 JoinColumn වෙනුවට Column දාන්න ඕනේ මචං
//    @Column(name = "price")
//    private Double price;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "created_at")
//    private Date createdAt;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "updated_at")
//    private Date updatedAt;
//
//
//    // ==========================================================
//    // ⚙️ GETTERS AND SETTERS (අනිවාර්යයෙන්ම ඕන කරන ටික)
//    // ==========================================================
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
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
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public DeliveryTypes getDeliveryTypes() {
//        return deliveryTypes;
//    }
//
//    public void setDeliveryTypes(DeliveryTypes deliveryTypes) {
//        this.deliveryTypes = deliveryTypes;
//    }
//
//    public PaymentTypes getPaymentTypes() {
//        return paymentTypes;
//    }
//
//    public void setPaymentTypes(PaymentTypes paymentType) {
//        this.paymentTypes = paymentType;
//    }
//
//    public List<OrderItem> getOrderItems() {
//        return orderItems;
//    }
//
//    public void setOrderItems(List<OrderItem> orderItems) {
//        this.orderItems = orderItems;
//    }
//
//    public Double getPrice() {
//        return price;
//    }
//
//    public void setPrice(Double price) {
//        this.price = price;
//    }
//
////    public Date getCreatedAt() {
////        return createdAt;
////    }
////
////    public void setCreatedAt(Date createdAt) {
////        this.createdAt = createdAt;
////    }
////    public Date getUpdatedAt() {
////        return updatedAt;
////    }
////
////    public void setUpdatedAt(Date updatedAt) {
////        this.updatedAt = updatedAt;
////    }
//
//
//}
