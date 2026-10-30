package lk.dropti.ninewood.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "payment_types")
public class PaymentTypes implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50)
    private String name;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}