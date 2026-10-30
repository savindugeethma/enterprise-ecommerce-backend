package lk.dropti.ninewood.entity.entity;

import jakarta.persistence.*;
import lk.dropti.ninewood.entity.BaseEntity;
import lk.dropti.ninewood.entity.OrderItem;
import lk.dropti.ninewood.entity.Status;
import lk.dropti.ninewood.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Orders extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private lk.dropti.ninewood.entity.Status status;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private lk.dropti.ninewood.entity.User user;

    @OneToMany(mappedBy = "order")
    private List<lk.dropti.ninewood.entity.OrderItem> orderItems = new ArrayList<>();

    @JoinColumn(name = "price")
    private Double price;

//    @JoinColumn(name = "updated_at")
//    @Temporal(TemporalType.TIMESTAMP)
//    private LocalDateTime updatedAt;
//
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public lk.dropti.ninewood.entity.Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public lk.dropti.ninewood.entity.User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getPrice() {
        return price;
    }
}
