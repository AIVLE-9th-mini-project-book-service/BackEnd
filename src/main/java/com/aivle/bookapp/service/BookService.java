package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.dto.AiBookSummaryRequest;
import com.aivle.bookapp.dto.AiBookSummaryResponse;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    // 기본 검색 (제목 OR 저자)
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
    }

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
    public Book update(Long id, Book book) {
        Book existing = findById(id);

        if (book.getTitle() != null) {
            existing.setTitle(book.getTitle());
        }
        if (book.getAuthor() != null) {
            existing.setAuthor(book.getAuthor());
        }
        if (book.getGenre() != null) {
            existing.setGenre(book.getGenre());
        }
        if (book.getContent() != null) {
            existing.setContent(book.getContent());
        }
        if (book.getTag() != null) {
            existing.setTag(book.getTag());
        }
        if (book.getCoverImageUrl() != null) {
            existing.setCoverImageUrl(book.getCoverImageUrl());
        }
        if (book.getSummary() != null) {
            existing.setSummary(book.getSummary());
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

    @Transactional(readOnly = true)
    public AiBookSummaryResponse generateSummary(Long id, AiBookSummaryRequest request) {
        Book book = findById(id);
        String summary = callOpenAiApi(book.getContent(), request.apiKey());
        return new AiBookSummaryResponse(summary);
    }

    private String callOpenAiApi(String content, String apiKey) {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey.trim());
            conn.setDoOutput(true);

            Map<String, Object> body = Map.of(
                    "model", "gpt-4o-mini",
                    "messages", List.of(Map.of("role", "system", "content", "주어진 도서 내용을 바탕으로 한 문장의 한줄평을 작성하세요."),
                            Map.of("role", "user", "content", content))
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(objectMapper.writeValueAsBytes(body));
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == 429) {
                // 이 에러가 뜨면 프론트가 429 코드를 보고 "잠시 대기" 화면을 띄울 수 있습니다.
                throw new RuntimeException("AI 서버 요청 한도 초과(429): 잠시 후 다시 시도해주세요.");
            } else if (responseCode != 200) {
                throw new RuntimeException("AI 서버 오류 (코드: " + responseCode + ")");
            }

            JsonNode root = objectMapper.readTree(conn.getInputStream());
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (RuntimeException e) {
            throw e; // 위에서 던진 429 에러는 그대로 던짐
        } catch (Exception e) {
            throw new RuntimeException("AI 요약 처리 중 예상치 못한 오류 발생: " + e.getMessage());
        }
    }

}
