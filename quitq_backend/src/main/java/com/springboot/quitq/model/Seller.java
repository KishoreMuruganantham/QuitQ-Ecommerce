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
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "users_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 100)
    private String businessName;

    @Column(nullable = false, length = 20)
    private String contactNumber;

    @Column(length = 100)
    private String city;

    @Column(length = 255)
    private String address;

    @CreationTimestamp
    private Instant createdAt;
}
