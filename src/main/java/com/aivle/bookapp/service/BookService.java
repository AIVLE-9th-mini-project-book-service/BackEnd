package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.dto.BookSearchRequest;
import com.aivle.bookapp.dto.BookSearchResponse;
import com.aivle.bookapp.dto.BookUpdateRequest;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookService {

    private static final int DEFAULT_POPULAR_LIMIT = 5;
    private static final int MAX_POPULAR_LIMIT = 50;

    private final BookRepository bookRepository;

    // 도서 상세 조회
    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    // 도서 목록 조회
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    // 도서 등록 + like 0 기본값 추가
    @Transactional
    public Book create(Book book) {
        if (book.getLikes() == null) {
            book.setLikes(0);
        }
        return bookRepository.save(book);
    }

/*    // 기본 검색 (제목 OR 저자)
    @Transactional(readOnly = true)
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword);
    }

    // 제목 검색
    @Transactional(readOnly = true)
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    // 제목 키워드 검색
    @Transactional(readOnly = true)
    public List<Book> searchByKeyword(String keyword) {
        return bookRepository.findByTitleContaining(keyword);
    }

    // 제목 + 저자 검색
    @Transactional(readOnly = true)
    public List<Book> searchByTitleAndAuthor(String title, String author) {
        return bookRepository.findByTitleAndAuthor(title, author);
    }

    // 저자별 도서 제목 조회
    @Transactional(readOnly = true)
    public List<String> authorGetTitle(String author) {
        List<Book> books = bookRepository.findByAuthor(author);
        return books.stream().map(Book::getTitle).toList();
    }

    // 상세 검색 (장르 AND 태그)
    @Transactional(readOnly = true)
    public List<Book> searchDetail(String genre, String tag) {
        return bookRepository.findByGenreAndTag(genre, tag);
    }*/

    @Transactional(readOnly = true)
    public Page<Book> getPage(int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return bookRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public long count() {
        return bookRepository.count();
    }

    // 도서 수정
    @Transactional
    public Book update(Long id, BookUpdateRequest dto) {
        Book existing = findById(id);

        if (dto.title() != null) {
            existing.setTitle(dto.title());
        }
        if (dto.author() != null) {
            existing.setAuthor(dto.author());
        }
        if (dto.genre() != null) {
            existing.setGenre(dto.genre());
        }
        if (dto.content() != null) {
            existing.setContent(dto.content());
        }
        if (dto.tag() != null) {
            existing.setTag(dto.tag());
        }
        if (dto.coverImageUrl() != null) {
            existing.setCoverImageUrl(dto.coverImageUrl());
        }
        if (dto.summary() != null) {
            existing.setSummary(dto.summary());
        }
        existing.setUpdatedAt(LocalDateTime.now());

        return bookRepository.save(existing);
    }

    // 도서 휴지통 이동
    @Transactional
    public Book moveToTrash(Long id) {
        Book existing = findById(id);
        existing.setDeletedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());
        return bookRepository.save(existing);
    }

    // 도서 휴지통 복원
    @Transactional
    public Book restore(Long id) {
        Book existing = findById(id);
        existing.setDeletedAt(null);
        existing.setUpdatedAt(LocalDateTime.now());
        return bookRepository.save(existing);
    }

    // AI 표지 이미지 저장
    @Transactional
    public Book saveImgUrl(Long id, Book book) {
        Book existing = findById(id);
        if (book.getCoverImageUrl() != null) {
            existing.setCoverImageUrl(book.getCoverImageUrl());
        }
        return bookRepository.save(existing);
    }

    // 이미지 저장
    @Transactional
    public Book saveCoverImage(Long id, String coverImageUrl) {
        Book existing = findById(id);
        existing.setCoverImageUrl(coverImageUrl);
        return bookRepository.save(existing);
    }

    // 좋아요 +1
    @Transactional
    public void likeBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.incrementLikes(id);
    }

    // 한줄평 저장
    @Transactional
    public Book saveSummary(Long id, String summary) {
        Book existing = findById(id);
        existing.setSummary(summary);
        return bookRepository.save(existing);
    }

    // 도서 영구 삭제
    @Transactional
    public void deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        } else {
            throw new BookNotFoundException(id);
        }
    }

    // 삭제되지 않은 도서만 가져오기
    @Transactional(readOnly = true)
    public List<Book> getActiveBooks() {
        return bookRepository.findAll()
                .stream()
                //.filter(book -> book.getDeletedAt() == null)
                .toList();
    }

    // 도서 수 통계
    @Transactional(readOnly = true)
    public Map<String, Object> getBookCountStatistics(String type) {
        if (type == null || type.isBlank()) {
            Map<String, Object> bookCountResult = new HashMap<>();

            bookCountResult.put("genre", getBookCountByGenre());
            bookCountResult.put("tag", getBookCountByTag());

            return bookCountResult;
        }

        if (type.equals("genre")) {
            return Map.of("genre", getBookCountByGenre());
        }
        if (type.equals("tag")) {
            return Map.of("tag", getBookCountByTag());
        }

        throw new IllegalArgumentException("type은 genre 또는 tag만 가능합니다.");
    }

    // 장르별 도서 수 합계
    private Map<String, Long> getBookCountByGenre() {
        Map<String, Long> bookCountResult = new HashMap<>();

        for (Book book : getActiveBooks()) {
            String genre = book.getGenre() != null ? book.getGenre() : "기타";
            bookCountResult.put(genre, bookCountResult.getOrDefault(genre, 0L) + 1);
        }

        return bookCountResult;
    }

    // 태그별 도서 수 합계
    private Map<String, Long> getBookCountByTag() {
        Map<String, Long> bookCountResult = new HashMap<>();

        for (Book book : getActiveBooks()) {
            if (book.getTag() == null || book.getTag().trim().isEmpty()) {
                continue;
            }

            String[] tags = book.getTag().split(",");
            for (String tag : tags) {
                String trimTag = tag.trim();
                if (trimTag.isEmpty()) {
                    continue;
                }
                bookCountResult.put(trimTag, bookCountResult.getOrDefault(trimTag, 0L) + 1);
            }
        }

        return bookCountResult;
    }

    // 좋아요 수 통계
    @Transactional(readOnly = true)
    public Map<String, Object> getLikesCountStatistics(String type) {
        if (type == null || type.isBlank()) {
            Map<String, Object> likesCountResult = new HashMap<>();

            likesCountResult.put("genre", getLikesCountByGenre());
            likesCountResult.put("tag", getLikesCountByTag());

            return likesCountResult;
        }

        if (type.equals("genre")) {
            return Map.of("genre", getLikesCountByGenre());
        }
        if (type.equals("tag")) {
            return Map.of("tag", getLikesCountByTag());
        }

        throw new IllegalArgumentException("type은 genre 또는 tag만 가능합니다.");
    }

    // 장르별 좋아요 수 합계
    private Map<String, Integer> getLikesCountByGenre() {
        Map<String, Integer> likesCountResult = new HashMap<>();

        for (Book book : getActiveBooks()) {
            String genre = book.getGenre() != null ? book.getGenre() : "기타";
            int likes = book.getLikes() != null ? book.getLikes() : 0;

            likesCountResult.put(genre, likesCountResult.getOrDefault(genre, 0) + likes);
        }

        return likesCountResult;
    }

    // 태그별 좋아요 수 합계
    private Map<String, Integer> getLikesCountByTag() {
        Map<String, Integer> likesCountResult = new HashMap<>();

        for (Book book : getActiveBooks()) {
            if (book.getTag() == null || book.getTag().trim().isEmpty()) {
                continue;
            }

            int likes = book.getLikes() != null ? book.getLikes() : 0;
            String[] tags = book.getTag().split(",");
            for (String tag : tags) {
                String trimTag = tag.trim();
                if (trimTag.isEmpty()) {
                    continue;
                }

                likesCountResult.put(trimTag, likesCountResult.getOrDefault(trimTag, 0) + likes);
            }
        }

        return likesCountResult;
    }

    // 도서 검색
    @Transactional(readOnly = true)
    public Page<BookSearchResponse> search(BookSearchRequest request, Pageable pageable) {
        List<String> genres = normalizeSearchValues(request.genres());
        List<String> tags = normalizeSearchValues(request.tags());

        return bookRepository.searchBooks(
                        normalizeSearchValue(request.keyword()),
                        emptyListGuard(genres),
                        genres.isEmpty(),
                        emptyListGuard(tags),
                        tags.isEmpty(),
                        pageable
                )
                .map(BookSearchResponse::from);
    }

    // 인기 도서 조회
    @Transactional(readOnly = true)
    public List<BookSearchResponse> getPopularBooks(int limit) {
        Pageable pageable = PageRequest.of(0, normalizeLimit(limit));
        return bookRepository.findPopularBooks(pageable)
                .stream()
                .map(BookSearchResponse::from)
                .toList();
    }

    private int normalizeLimit(int limit) {
        // 인기 도서 조회 개수를 기본값과 최대 허용값 사이로 제한
        if (limit <= 0) return DEFAULT_POPULAR_LIMIT;
        return Math.min(limit, MAX_POPULAR_LIMIT);
    }

    private String normalizeSearchValue(String value) {
        // 검색 조건 비교를 위해 null, 앞뒤 공백, 대소문자 차이를 정리
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private List<String> normalizeSearchValues(List<String> values) {
        // 다중 쿼리 파라미터와 콤마로 묶인 값을 모두 동일한 리스트 조건으로 정리
        if (values == null) return List.of();

        return values.stream()
                .flatMap(value -> List.of(value.split(",")).stream())
                .map(this::normalizeSearchValue)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();
    }

    private List<String> emptyListGuard(List<String> values) {
        // JPQL의 IN 조건에 빈 리스트가 들어가지 않도록 더미 값을 넣어 쿼리 오류를 방지
        return values.isEmpty() ? List.of("__empty__") : values;
    }
}
