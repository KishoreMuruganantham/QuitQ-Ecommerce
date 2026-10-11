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
public class Customer { //c

    @Id // This makes id a Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This makes id auto_increment
    private Long id;

    @OneToOne
    @JoinColumn(name = "users_id", nullable = false, unique = true)
    private User user; //c.user

    @Column(nullable = false, length = 100) //This adds constraint NOT NULL
    private String name;

    @Column(length = 20)
    private String gender;

    @Column(nullable = false, length = 20) //This adds constraint NOT NULL
    private String contactNumber; //c.contact_number

    @CreationTimestamp
    private Instant createdAt;
}
