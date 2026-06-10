package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.dto.*;
import com.aivle.bookapp.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {

    private final BookService bookService;

    // 도서 목록 조회
    @GetMapping("/books")
    public List<Book> findAll() {
        return bookService.findAll();
    }

    // 도서 상세 조회
    @GetMapping("/books/{id}")
    public Book findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    // 도서 등록
    @PostMapping("/books")
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookCreateRequest request) {
        Book saved = bookService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 도서 검색
    @GetMapping("/books/search")
    public Page<BookSearchResponse> searchBooks(
            @ModelAttribute BookSearchRequest request,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return bookService.search(request, pageable);
    }

    // 인기 도서 조회
    @GetMapping("/books/popular")
    public List<BookSearchResponse> getPopularBooks(@RequestParam(defaultValue = "5") int limit) {
        return bookService.getPopularBooks(limit);
    }

    @GetMapping("/books/page")
    public Page<Book> getPage(@RequestParam int page, @RequestParam int size, @RequestParam String sortBy) {
        return bookService.getPage(page, size, sortBy);
    }

    @GetMapping("/books/count")
    public long getCount() {
        return bookService.count();
    }

    // 도서 수정
    @PatchMapping("/books/{id}")
    public ResponseEntity<Map<String, Object>> updateBook(@PathVariable Long id, @RequestBody BookUpdateRequest dto) {
        Book updatedBook = bookService.update(id, dto);
        Map<String, Object> body = Map.of(
                "id", updatedBook.getId(),
                "message", "도서 수정 성공"
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    // 도서 삭제(휴지통 이동)
    @PatchMapping("/books/trash/{id}")
    public ResponseEntity<Map<String, Object>> moveToTrash(@PathVariable Long id) {
        bookService.moveToTrash(id);
        Map<String, Object> body = Map.of(
                "message", "도서 삭제 성공"
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    // 도서 복원
    @PatchMapping("/books/restore/{id}")
    public ResponseEntity<Map<String, Object>> restore(@PathVariable Long id) {
        bookService.restore(id);
        Map<String, Object> body = Map.of(
                "message", "도서 복원 성공"
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    // 좋아요
    @PatchMapping("/books/{id}/like")
    public ResponseEntity<Map<String, Object>> likeBook(@PathVariable Long id) {
        bookService.likeBook(id);
        Map<String, Object> body = Map.of(
                "message", "좋아요 성공"
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    // AI 표지 이미지 생성 (백엔드에서 OpenAI 호출)
    @PostMapping("/books/{id}/cover/generate")
    public ResponseEntity<Map<String, Object>> generateCover(@PathVariable Long id, @Valid @RequestBody GenerateCoverRequest request) {
        Book updatedBook = bookService.generateCover(id, request);
        Map<String, Object> body = Map.of(
                "id", updatedBook.getId(),
                "message", "표지 이미지 생성 성공",
                "coverImageUrl", updatedBook.getCoverImageUrl()
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    // AI 표지 이미지 저장
    @PatchMapping("/books/{id}/cover")
    public ResponseEntity<Map<String, Object>> saveImgUrl(@PathVariable Long id, @Valid @RequestBody CoverImageUpdateRequest request) {
        Book updatedBook = bookService.saveImgUrl(id, request);
        Map<String, Object> body = Map.of(
                "id", updatedBook.getId(),
                "message", "도서 수정 성공",
                "coverImageUrl", updatedBook.getCoverImageUrl()
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    // 한줄평 저장
    @PatchMapping("/books/{id}/summary")
    public Book saveSummary(@PathVariable Long id, @RequestParam String summary) {
        return bookService.saveSummary(id, summary);
    }

    // 도서 영구 삭제
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        Map<String, Object> body = Map.of(
                "message", "도서 영구 삭제 성공"
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    //도서 수 통계
    @GetMapping("/books/statistics/count")
    public ResponseEntity<Map<String, Object>> getBookCountStatistics(
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(bookService.getBookCountStatistics(type));
    }

    //좋아요 수 통계
    @GetMapping("/books/statistics/likes")
    public ResponseEntity<Map<String, Object>> getLikesCountStatistics(
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(bookService.getLikesCountStatistics(type));
    }
}
