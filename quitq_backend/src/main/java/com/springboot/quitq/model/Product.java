package com.springboot.quitq.model;

import com.springboot.quitq.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 100)
    private String brand;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stockQty;

    @Column(length = 255)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductStatus status;

    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;
}
