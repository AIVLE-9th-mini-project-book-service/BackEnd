package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.Comment;
import com.aivle.bookapp.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    private final CommentService commentService;

    // 후기 등록
    @PostMapping("/books/{bookId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long bookId, @RequestBody Comment comment) {
        Comment saved = commentService.createComment(bookId, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 도서 후기 수정
    @PatchMapping("/comments/{id}")
    public ResponseEntity<Map<String, Object>> commentUpdate(@PathVariable Long id, @RequestBody Comment comment) {
        if (comment.getText() == null || comment.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", 400,
                    "message", "수정할 내용과 비밀번호를 모두 입력해주세요."
            ));
        }

        Comment updatedComment = commentService.commentUpdate(id, comment);
        Map<String, Object> body = Map.of(
                "id", updatedComment.getId(),
                "text", updatedComment.getText()
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    // 도서 후기 삭제
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    // 후기 조회
    @GetMapping("/books/{bookId}/comments")
    public ResponseEntity<List<Comment>> findComments(@PathVariable Long bookId) {
        List<Comment> comments = commentService.findComments(bookId);
        return ResponseEntity.ok(comments);
    }
}
