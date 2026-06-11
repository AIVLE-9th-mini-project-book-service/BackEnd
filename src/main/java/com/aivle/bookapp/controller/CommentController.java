package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.Comment;
import com.aivle.bookapp.dto.CommentCreateRequest;
import com.aivle.bookapp.dto.CommentUpdateRequest;
import com.aivle.bookapp.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Comment API", description = "댓글 관련 API")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록
    @Operation(summary = "댓글 등록", description = "특정 도서에 댓글을 등록합니다.")
    @PostMapping("/books/{bookId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long bookId, @RequestBody CommentCreateRequest request) {
        Comment saved = commentService.createComment(bookId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 도서 댓글 수정
    @Operation(summary = "댓글 수정", description = "특정 도서의 댓글을 수정합니다.")
    @PatchMapping("/comments/{id}")
    public ResponseEntity<Map<String, Object>> commentUpdate(@PathVariable Long id, @RequestBody CommentUpdateRequest dto) {
        if (dto.text() == null || dto.password() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", 400,
                    "message", "수정할 내용과 비밀번호를 모두 입력해주세요."
            ));
        }

        Comment updatedComment = commentService.commentUpdate(id, dto);
        Map<String, Object> body = Map.of(
                "id", updatedComment.getId(),
                "text", updatedComment.getText()
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    // 도서 댓글 삭제
    @Operation(summary = "댓글 삭제", description = "특정 도서의 댓글을 삭제합니다.")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    // 댓글 조회
    @Operation(summary = "댓글 조회", description = "특정 도서의 댓글을 조회합니다.")
    @GetMapping("/books/{bookId}/comments")
    public ResponseEntity<List<Comment>> findComments(@PathVariable Long bookId) {
        List<Comment> comments = commentService.findComments(bookId);
        return ResponseEntity.ok(comments);
    }
}
