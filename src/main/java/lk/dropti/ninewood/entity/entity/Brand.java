package lk.dropti.ninewood.entity.entity;

import jakarta.persistence.*;
import lk.dropti.ninewood.entity.Model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Brand implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 100, nullable = false)
    private String brand;

    @OneToMany(mappedBy = "brand")
    private Set<lk.dropti.ninewood.entity.Model> models = new HashSet<>();

    public Set<Model> getModels() {
        return models;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return brand;
    }

    public void setName(String brand) {
        this.brand = brand;
    }
}
