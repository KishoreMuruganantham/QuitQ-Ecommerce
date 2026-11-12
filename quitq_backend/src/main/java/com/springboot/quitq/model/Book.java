package com.springboot.quitq.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 100)
    private String genre;

    @Column(name = "published_year")
    private Integer publishedYear;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
}
