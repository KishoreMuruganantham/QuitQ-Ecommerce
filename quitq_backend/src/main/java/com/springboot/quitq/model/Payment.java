package com.springboot.quitq.model;

import com.springboot.quitq.enums.PaymentMethod;
import com.springboot.quitq.enums.PaymentStatus;
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
public class Payment { //pay

    @Id // This makes id a Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This makes id auto_increment
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Orders orders; //pay.orders

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentMethod paymentMethod; //pay.payment_method

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus paymentStatus; //pay.payment_status

    @Column(length = 100)
    private String transactionId; //pay.transaction_id

    @Column(nullable = false) //This adds constraint NOT NULL
    private Double amount; //pay.amount

    @CreationTimestamp
    private Instant paymentDate; //pay.payment_date
}
