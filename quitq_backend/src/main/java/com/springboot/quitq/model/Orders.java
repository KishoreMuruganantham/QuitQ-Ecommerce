package com.springboot.quitq.model;

import com.springboot.quitq.enums.OrderStatus;
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
@Table(name = "orders")
public class Orders { //o

    @Id // This makes id a Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This makes id auto_increment
    private Long id;

    @Column(nullable = false, unique = true, length = 64) //This adds constraint NOT NULL
    private String orderNumber; //o.order_number

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; //o.customer

    @ManyToOne
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private ShippingAddress shippingAddress; //o.shipping_address

    @Column(nullable = false) //This adds constraint NOT NULL
    private Double totalAmount; //o.total_amount

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus orderStatus; //o.order_status

    @CreationTimestamp
    private Instant orderDate; //o.order_date
}
