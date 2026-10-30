package lk.dropti.ninewood.entity.entity;

import jakarta.persistence.*;
import lk.dropti.ninewood.entity.BaseEntity;
import lk.dropti.ninewood.entity.Product;
import lk.dropti.ninewood.entity.Status;
import lk.dropti.ninewood.entity.User;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Seller extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "company_name", length = 200, nullable = false)
    private String companyName;

    @Column(name = "company_mobile", length = 10, nullable = false)
    private String companyMobile;

    @Column(name = "company_email", length = 100, nullable = false)
    private String companyEmail;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id")
    private Status status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private lk.dropti.ninewood.entity.User user;

    @OneToMany(mappedBy = "seller")
    private Set<lk.dropti.ninewood.entity.Product> products = new HashSet<>();

    public Set<Product> getProducts() {
        return products;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyMobile() {
        return companyMobile;
    }

    public void setCompanyMobile(String companyMobile) {
        this.companyMobile = companyMobile;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public Status getStatus() {
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
}
