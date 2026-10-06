package com.springboot.quitq.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT AUTO_INCREMENT")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
    private String description;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer seq;

}
