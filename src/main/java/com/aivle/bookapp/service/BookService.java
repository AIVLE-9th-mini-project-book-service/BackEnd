package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    // 도서 상세 조회
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    // 도서 목록 조회
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    // 도서 등록
    public Book create(Book book) {
        return bookRepository.save(book);
    }

    // 기본 검색 (제목 OR 저자)
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword);
    }

    // 상세 검색 (장르 AND 태그)
    public List<Book> searchDetail(String genre, String tag) {
        return bookRepository.findByGenreAndTag(genre, tag);
    }

    // 이미지 저장
    public Book saveCoverImage(Long id, String coverImageUrl) {
        Book book = findById(id);

        book.setCoverImageUrl(coverImageUrl);

        return bookRepository.save(book);
    }

    // 한줄평 저장
    public Book saveSummary(Long id, String summary) {
        Book book = findById(id);

        book.setSummary(summary);

        return bookRepository.save(book);
    }
}