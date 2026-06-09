package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.domain.Comment;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.repository.BookRepository;
import com.aivle.bookapp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}