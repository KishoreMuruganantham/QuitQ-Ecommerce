package com.springboot.quitq.model;

import com.springboot.quitq.enums.Role;
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
@Table(name = "users")
public class User { //u

    @Id // This makes id a Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This makes id auto_increment
    private Long id;

    @Column(nullable = false, unique = true, length = 50) //This adds constraint NOT NULL
    private String username;

    @Column(nullable = false, unique = true, length = 100) //This adds constraint NOT NULL
    private String email;

    @Column(nullable = false) //This adds constraint NOT NULL
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role; //u.role

    @CreationTimestamp
    private Instant createdAt;
}
