package com.springboot.quitq.model;

import com.springboot.quitq.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Product { //p

    @Id // This makes id a Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This makes id auto_increment
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; //p.category

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller; //p.seller

    @Column(nullable = false, length = 255) //This adds constraint NOT NULL
    private String name;

    @Column(length = 100)
    private String brand;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(nullable = false) //This adds constraint NOT NULL
    private Double price; //p.price

    @Column(nullable = false) //This adds constraint NOT NULL
    private Integer stockQty; //p.stock_qty

    @Column(length = 255)
    private String imageUrl; //p.image_url

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductStatus status; //p.status

    @CreationTimestamp
    private Instant createdAt;
}
