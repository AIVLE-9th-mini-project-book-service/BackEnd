package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 도서 목록 조회
    @GetMapping("/books")
    public List<Book> findAll() {
        return bookService.findAll();
    }

    // 도서 상세 조회
    @GetMapping("/books/{id}")
    public Book findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    // 도서 등록
    @PostMapping("/books")
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        Book saved =  bookService.create(book);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 기본 검색
    @GetMapping("/books/search")
    public List<Book> searchBooks(@RequestParam String keyword) {
        return bookService.searchBooks(keyword);
    }

    // 상세 검색
    @GetMapping("/books/search/detail")
    public List<Book> searchDetail(@RequestParam String genre, @RequestParam String tag) {
        return bookService.searchDetail(genre, tag);
    }

    // 이미지 저장
    @PatchMapping("/books/{id}/cover")
    public Book saveCoverImage(@PathVariable Long id, @RequestParam String coverImageUrl) {
        return bookService.saveCoverImage(id, coverImageUrl);
    }

    // 한줄평 저장
    @PatchMapping("/books/{id}/summary")
    public Book saveSummary(@PathVariable Long id, @RequestParam String summary) {
        return bookService.saveSummary(id, summary
        );
    }
}