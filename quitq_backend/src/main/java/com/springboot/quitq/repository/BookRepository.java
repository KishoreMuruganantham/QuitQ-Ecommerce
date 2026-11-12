package com.springboot.quitq.repository;

import com.springboot.quitq.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByPublishedYearGreaterThanEqual(Integer minYear);
}
