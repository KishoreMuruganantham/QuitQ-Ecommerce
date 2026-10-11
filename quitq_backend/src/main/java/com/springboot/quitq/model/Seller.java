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
public class Seller { //s

    @Id // This makes id a Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This makes id auto_increment
    private Long id;

    @OneToOne
    @JoinColumn(name = "users_id", nullable = false, unique = true)
    private User user; //s.user

    @Column(nullable = false, length = 100) //This adds constraint NOT NULL
    private String businessName; //s.business_name

    @Column(nullable = false, length = 20) //This adds constraint NOT NULL
    private String contactNumber; //s.contact_number

    @Column(length = 100)
    private String city;

    @Column(length = 255)
    private String address;

    @CreationTimestamp
    private Instant createdAt;
}
