package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.Comment;
import com.aivle.bookapp.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 후기 등록
    @PostMapping("/books/{bookId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long bookId, @RequestBody Comment comment) {
        Comment saved = commentService.createComment(bookId, comment);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 후기 조회
    @GetMapping("/books/{bookId}/comments")
    public ResponseEntity<List<Comment>> findComments(@PathVariable Long bookId) {
        List<Comment> comments = commentService.findComments(bookId);

        return ResponseEntity.ok(comments);
    }
}