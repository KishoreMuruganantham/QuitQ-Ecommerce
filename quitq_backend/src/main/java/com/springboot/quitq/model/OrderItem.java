package com.springboot.quitq.model;

import com.springboot.quitq.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class OrderItem { //oi

    @Id // This makes id a Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This makes id auto_increment
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders; //oi.order_id

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; //oi.product

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller; //oi.seller

    @Column(nullable = false) //This adds constraint NOT NULL
    private Integer quantity; //oi.quantity

    @Column(nullable = false) //This adds constraint NOT NULL
    private Double pricePerUnit; //oi.price_per_unit

    @Column(nullable = false) //This adds constraint NOT NULL
    private Double totalPrice; //oi.total_price

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status; //oi.status
}
