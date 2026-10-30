package lk.dropti.ninewood.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "product_images")
public class Product_Images implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_images_id")
    private int productImagesId;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "image", length = 255)
    private String image;

    public Product_Images() {
    }

    public int getProductImagesId() {
        return productImagesId;
    }

    public void setProductImagesId(int productImagesId) {
        this.productImagesId = productImagesId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}