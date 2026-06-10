package com.aivle.bookapp.repository;

import com.aivle.bookapp.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findByTitleContaining(String keyword);
    List<Book> findByTitleAndAuthor(String title, String author);

    List<Book> findByTitleContainingOrAuthorContaining(String keyword, String keyword1);

    List<Book> findByGenreAndTag(String genre, String tag);

    @Modifying
    @Query("UPDATE Book b SET b.likes = b.likes + 1 WHERE b.id = :id")
    void incrementLikes(@Param("id") Long id);
}
