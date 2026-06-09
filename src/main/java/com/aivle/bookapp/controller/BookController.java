package com.aivle.bookapp.controller;

import com.aivle.bookapp.dto.CoverImageUpdateRequest;
import com.aivle.bookapp.entity.Book;
import com.aivle.bookapp.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PatchMapping("/{bookId}/cover")
    public ResponseEntity<Map<String, String>> updateCoverImage(
            @PathVariable Long bookId,
            @Valid @RequestBody CoverImageUpdateRequest request) {

        Book updatedBook = bookService.updateCoverImage(bookId, request.getCoverImageUrl());
        return ResponseEntity.ok(Map.of("coverImageUrl", updatedBook.getCoverImageUrl()));
    }
}
