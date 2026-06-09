package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Comment;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor // final 붙은 키워드들만 초기화

public class CommentsService {

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Comment findById(Long id){
        return commentRepository.findById(id).orElseThrow(()
                ->new BookNotFoundException(id));
    }

    //도서 후기 수정
    @Transactional
    public Comment commentUpdate(Long id, Comment comment) {
        Comment existing = findById(id);
        if (existing == null) {
            throw new NoSuchElementException("해당 도서 후기를 찾을 수 없습니다.");
        }


        //401 예외처리 추가되면 주석 풀기
//        if (comments.getPassword() == null || !existing.getPassword().equals(comments.getPassword())) {
//            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
//        }

        if(comment.getText()!= null){
            existing.setText(comment.getText());
        }

        return commentRepository.save(existing);
    }

    @Transactional
    public void deleteComment(Long id){
        if(commentRepository.existsById(id)){
            commentRepository.deleteById(id);
        }else{
            throw new BookNotFoundException(id);
        }
    }
}
