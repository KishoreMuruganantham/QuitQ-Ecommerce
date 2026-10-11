package com.springboot.quitq.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class ShippingAddress { //sa

    @Id // This makes id a Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This makes id auto_increment
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; //sa.customer

    @Column(nullable = false, length = 100) //This adds constraint NOT NULL
    private String recipientName; //sa.recipient_name

    @Column(nullable = false, length = 20) //This adds constraint NOT NULL
    private String phone;

    @Column(nullable = false, length = 255) //This adds constraint NOT NULL
    private String streetAddress; //sa.street_address

    @Column(nullable = false, length = 100) //This adds constraint NOT NULL
    private String city;

    @Column(nullable = false, length = 100) //This adds constraint NOT NULL
    private String state;

    @Column(nullable = false, length = 20) //This adds constraint NOT NULL
    private String postalCode; //sa.postal_code

    @Column(nullable = false) //This adds constraint NOT NULL
    private boolean isDefault; //sa.is_default
}
