package com.aivle.bookapp.controller;

import com.aivle.bookapp.dto.CommentCreateRequest;
import com.aivle.bookapp.dto.CommentResponse;
import com.aivle.bookapp.dto.CommentUpdateRequest;
import com.aivle.bookapp.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Comment API", description = "댓글 관련 API")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 등록", description = "특정 도서에 댓글을 등록합니다.")
    @PostMapping("/books/{bookId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long bookId, @Valid @RequestBody CommentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(commentService.createComment(bookId, request)));
    }

    @Operation(summary = "댓글 수정", description = "특정 도서의 댓글을 수정합니다.")
    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> commentUpdate(@PathVariable Long id, @Valid @RequestBody CommentUpdateRequest dto) {
        return ResponseEntity.ok(CommentResponse.from(commentService.commentUpdate(id, dto)));
    }

    @Operation(summary = "댓글 삭제", description = "특정 도서의 댓글을 삭제합니다.")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "댓글 조회", description = "특정 도서의 댓글을 조회합니다.")
    @GetMapping("/books/{bookId}/comments")
    public ResponseEntity<List<CommentResponse>> findComments(@PathVariable Long bookId) {
        List<CommentResponse> responses = commentService.findComments(bookId).stream()
                .map(CommentResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
