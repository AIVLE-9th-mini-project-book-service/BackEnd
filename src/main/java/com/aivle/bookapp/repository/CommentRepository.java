package com.aivle.bookapp.repository;

import com.aivle.bookapp.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByBookId(Long bookId);
}
