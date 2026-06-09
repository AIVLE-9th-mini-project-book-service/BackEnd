package com.aivle.bookapp.service;

import com.aivle.bookapp.entity.Book;
import com.aivle.bookapp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public Book updateCoverImage(Long bookId, String coverImageUrl) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서입니다. id=" + bookId));

        book.setCoverImageUrl(coverImageUrl);
        return book;
    }
}
