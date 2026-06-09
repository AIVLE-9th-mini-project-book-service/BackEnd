package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.Comment;
import com.aivle.bookapp.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor // final 붙은 키워드들만 초기화
@CrossOrigin(origins = "http://localhost:3000") // 리액트 주소 허용
public class CommentController {

    private final CommentsService commentsService;


    //도서 후기 수정
    @PatchMapping("/comments/{id}")
    public ResponseEntity<Map<String, Object>>  commentUpdate(@PathVariable Long id, @RequestBody Comment comment){
        if (comment.getText() == null || comment.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", 400,
                    "message", "수정할 내용과 비밀번호를 모두 입력해주세요."
            ));
        }
        Comment updatedComment = commentsService.commentUpdate(id, comment);
        Map<String, Object> body = Map.of(
                "id", updatedComment.getId(),
                "text", comment.getText()
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    //도서 후기 삭제
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        commentsService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
