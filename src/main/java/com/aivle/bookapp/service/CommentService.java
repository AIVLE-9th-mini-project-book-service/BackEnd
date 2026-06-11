package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.domain.Comment;
import com.aivle.bookapp.dto.CommentCreateRequest;
import com.aivle.bookapp.dto.CommentUpdateRequest;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.exception.CommentNotFoundException;
import com.aivle.bookapp.repository.BookRepository;
import com.aivle.bookapp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    // 후기 등록
    public Comment createComment(Long bookId, CommentCreateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        Comment comment = new Comment();

        comment.setBook(book);

        comment.setAuthor(request.author() == null || request.author().isBlank() ? "익명" : request.author());

        comment.setText(request.text());

        comment.setPassword(request.password());

        comment.setCreatedAt(java.time.LocalDateTime.now());

        return commentRepository.save(comment);
    }

    // 후기 조회
    public List<Comment> findComments(Long bookId) {
        return commentRepository.findByBook_Id(bookId);
    }

    @Transactional(readOnly = true)
    public Comment findById(Long id){
        return commentRepository.findById(id).orElseThrow(()
                ->new CommentNotFoundException(id));
    }

    //도서 후기 수정
    @Transactional
    public Comment commentUpdate(Long id, CommentUpdateRequest dto) {
        Comment existing = findById(id);
//        if (existing == null) {
//            throw new NoSuchElementException("해당 도서 후기를 찾을 수 없습니다.");
//        }


        if (dto.password() == null || !existing.getPassword().equals(dto.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        if(dto.text()!= null){
            existing.setText(dto.text());
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