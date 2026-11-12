package com.springboot.quitq.service;

import com.springboot.quitq.dto.response.BookDto;
import com.springboot.quitq.model.Book;
import com.springboot.quitq.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<BookDto> searchBooks(Integer minYear) {
        return bookRepository.findByPublishedYearGreaterThanEqual(minYear)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    private BookDto convertToDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getGenre(),
                book.getPublishedYear(),
                book.getPrice(),
                book.getAuthor().getName()
        );
    }
}
