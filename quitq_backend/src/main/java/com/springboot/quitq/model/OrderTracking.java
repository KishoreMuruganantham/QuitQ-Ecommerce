package com.springboot.quitq.model;

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
public class OrderTracking { //ot

    @Id // This makes id a Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This makes id auto_increment
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders; //ot.orders

    @Column(nullable = false, length = 50) //This adds constraint NOT NULL
    private String status; //ot.status

    @CreationTimestamp
    private Instant statusTime; //ot.status_time

    @Column(length = 255)
    private String remarks;
}
