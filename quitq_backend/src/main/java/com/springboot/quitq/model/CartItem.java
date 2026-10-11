package com.springboot.quitq.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class CartItem { //ci

    @Id // This makes id a Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This makes id auto_increment
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart; //ci.cart

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; //ci.product

    @Column(nullable = false) //This adds constraint NOT NULL
    private Integer quantity; //ci.quantity

    @Column(nullable = false) //This adds constraint NOT NULL
    private Double price; //ci.price -- snapshot price
}
