package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.domain.Comment;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.repository.BookRepository;
import com.aivle.bookapp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    // 후기 등록
    public Comment createComment(Long bookId, Comment comment) {

        bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new BookNotFoundException(bookId));

        comment.setBookId(bookId);

        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(java.time.LocalDateTime.now());
        }

        return commentRepository.save(comment);
    }

    // 후기 조회
    public List<Comment> findComments(Long bookId) {
        return commentRepository.findByBookId(bookId);
    }

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