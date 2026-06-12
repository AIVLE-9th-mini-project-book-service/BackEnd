package com.aivle.bookapp.controller;

import com.aivle.bookapp.dto.BookUpdateRequest;
import com.aivle.bookapp.dto.CommentUpdateRequest;
import com.aivle.bookapp.service.BookService;
import com.aivle.bookapp.service.CommentService;
import com.aivle.bookapp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final JwtUtil jwtUtil;
    private final BookService bookService;
    private final CommentService commentService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (!adminUsername.equals(username) || !adminPassword.equals(password)) {
            return ResponseEntity.status(401).body("관리자 인증 실패");
        }

        String token = jwtUtil.generateAccessToken(username, "ADMIN");
        return ResponseEntity.ok(Map.of("accessToken", token));
    }

    @PatchMapping("/books/{id}")
    public ResponseEntity<?> updateBook(
            @PathVariable Long id,
            @RequestBody BookUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        if (!"ADMIN".equals(jwtUtil.extractRole(token))) {
            return ResponseEntity.status(403).body("권한 없음");
        }

        return ResponseEntity.ok(bookService.adminUpdate(id, request));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        if (!"ADMIN".equals(jwtUtil.extractRole(token))) {
            return ResponseEntity.status(403).body("권한 없음");
        }

        bookService.adminDelete(id);
        return ResponseEntity.ok("삭제 완료");
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long id,
            @RequestBody CommentUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        if (!"ADMIN".equals(jwtUtil.extractRole(token))) {
            return ResponseEntity.status(403).body("권한 없음");
        }

        return ResponseEntity.ok(commentService.adminCommentUpdate(id, request));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        if (!"ADMIN".equals(jwtUtil.extractRole(token))) {
            return ResponseEntity.status(403).body("권한 없음");
        }

        commentService.adminDeleteComment(id);
        return ResponseEntity.ok("댓글 삭제 완료");
    }
}