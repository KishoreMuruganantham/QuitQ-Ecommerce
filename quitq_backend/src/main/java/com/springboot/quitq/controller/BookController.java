package com.springboot.quitq.controller;

import com.springboot.quitq.dto.response.BookDto;
import com.springboot.quitq.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/search")
    public List<BookDto> searchBooks(@RequestParam Integer minYear) {
        return bookService.searchBooks(minYear);
    }
}
