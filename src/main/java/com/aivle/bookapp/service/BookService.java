package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.dto.CoverImageUpdateRequest;
import com.aivle.bookapp.dto.GenerateCoverRequest;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.exception.OpenAiException;
import tools.jackson.databind.ObjectMapper;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
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

    // 도서 등록
    @Transactional
    public Book create(Book book) {
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

        return bookRepository.save(existing);
    }

    // 도서 휴지통 이동
    @Transactional
    public Book moveToTrash(Long id) {
        Book existing = findById(id);
        existing.setDeletedAt(LocalDateTime.now());
        return bookRepository.save(existing);
    }

    // 도서 휴지통 복원
    @Transactional
    public Book restore(Long id) {
        Book existing = findById(id);
        existing.setDeletedAt(null);
        return bookRepository.save(existing);
    }

    // AI 표지 이미지 저장
    @Transactional
    public Book saveImgUrl(Long id, CoverImageUpdateRequest request) {
        Book existing = findById(id);
        existing.setCoverImageUrl(request.getCoverImageUrl());
        return bookRepository.save(existing);
    }

    // 이미지 저장
    @Transactional
    public Book saveCoverImage(Long id, String coverImageUrl) {
        Book existing = findById(id);
        existing.setCoverImageUrl(coverImageUrl);
        return bookRepository.save(existing);
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
                .filter(book -> book.getDeletedAt() == null)
                .toList();
    }

    // 도서 수 통계
    @Transactional(readOnly = true)
    public Map<String, Long> getBookCountStatistics(String type) {
        if (type.equals("genre")) {
            return getBookCountByGenre();
        }
        if (type.equals("tag")) {
            return getBookCountByTag();
        }

        throw new IllegalArgumentException("type은 genre 또는 tag만 가능합니다.");
    }

    // 장르별 도서 수 합계
    private Map<String, Long> getBookCountByGenre() {
        Map<String, Long> bookCount = new HashMap<>();

        for (Book book : getActiveBooks()) {
            String genre = book.getGenre() != null ? book.getGenre() : "기타";
            bookCount.put(genre, bookCount.getOrDefault(genre, 0L) + 1);
        }

        return bookCount;
    }

    // 태그별 도서 수 합계
    private Map<String, Long> getBookCountByTag() {
        Map<String, Long> bookCount = new HashMap<>();

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

                bookCount.put(trimTag, bookCount.getOrDefault(trimTag, 0L) + 1);
            }
        }

        return bookCount;
    }

    // AI 표지 이미지 생성 (OpenAI 호출 → 압축 → DB 저장)
    public Book generateCover(Long id, GenerateCoverRequest request) {
        Book book = findById(id);
        String prompt = buildPrompt(book);
        String b64Json = callOpenAi(prompt, request.getApiKey(), request.getQuality());
        String dataUrl = compressImage(b64Json);
        return saveCoverImageUrl(id, dataUrl);
    }

    @Transactional
    public Book saveCoverImageUrl(Long id, String dataUrl) {
        Book existing = findById(id);
        existing.setCoverImageUrl(dataUrl);
        return bookRepository.save(existing);
    }

    private String buildPrompt(Book book) {
        return String.format("""
                A high-quality, professional book cover design for a book titled "%s" by %s.
                Genre: %s. Tags: %s.
                Visual concept based on the story: %s
                ---
                Visual direction:
                - Genre-specific mood: %s
                - Composition: cinematic, full-bleed illustration with strong focal point
                - Color palette: rich and thematic, matching the genre and emotional tone
                - Lighting: dramatic and atmospheric
                ---
                Text layout:
                - Title "%s" displayed prominently at the top in elegant, genre-appropriate font
                - Author name "%s" at the bottom in smaller clean serif font
                - Text should be clearly legible and well-integrated into the design
                ---
                Style: professional publishing quality, award-winning book cover art
                """,
                book.getTitle(), book.getAuthor(), book.getGenre(), book.getTag(),
                book.getContent(), getGenreMood(book.getGenre()),
                book.getTitle(), book.getAuthor()
        ).trim();
    }

    private String getGenreMood(String genre) {
        Map<String, String> moods = new HashMap<>();
        moods.put("소설",   "literary fiction, emotional depth, human drama, warm and cinematic tones");
        moods.put("고전",   "timeless elegance, vintage aesthetic, aged paper texture, classical art style");
        moods.put("역사",   "historical epic, aged maps, sepia tones, dramatic lighting, period setting");
        moods.put("IT",    "digital technology, circuit patterns, futuristic interface, clean modern design");
        moods.put("동화",   "whimsical illustration, soft pastel colors, magical fairy tale world, children friendly");
        moods.put("자기계발", "motivational, bright and energetic, clean minimalist design, uplifting mood");
        moods.put("과학",   "scientific discovery, cosmos, molecular structures, clean and precise illustration");
        moods.put("경제",   "professional, financial charts, bold typography, sleek corporate design");
        moods.put("철학",   "deep and contemplative, abstract symbolism, dark and moody, thought-provoking");
        moods.put("예술",   "creative expression, vibrant colors, artistic brushstrokes, gallery-worthy aesthetic");
        return moods.getOrDefault(genre, "artistic, expressive, visually compelling");
    }

    @SuppressWarnings("unchecked")
    private String callOpenAi(String prompt, String apiKey, String quality) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-image-2");
            body.put("prompt", prompt);
            body.put("n", 1);
            body.put("size", "1024x1536");
            body.put("quality", quality != null ? quality : "low");
            body.put("output_format", "png");

            String jsonBody = objectMapper.writeValueAsString(body);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/images/generations"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(120))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 401) throw new OpenAiException(401, "API Key가 올바르지 않습니다.");
            if (response.statusCode() == 429) throw new OpenAiException(429, "요청 한도 초과. 잠시 후 다시 시도해주세요.");
            if (response.statusCode() != 200) throw new OpenAiException(response.statusCode(), "OpenAI 오류: " + response.statusCode());

            Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");
            String b64Json = (String) data.get(0).get("b64_json");

            if (b64Json == null) throw new OpenAiException(500, "응답 형식 오류");
            return b64Json;

        } catch (OpenAiException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenAiException(500, "OpenAI API 호출 실패: " + e.getMessage());
        }
    }

    private String compressImage(String b64Json) {
        try {
            byte[] pngBytes = Base64.getDecoder().decode(b64Json);
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(pngBytes));

            int maxWidth = 400;
            double scale = Math.min(1.0, (double) maxWidth / original.getWidth());
            int w = (int) (original.getWidth() * scale);
            int h = (int) (original.getHeight() * scale);

            BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resized.createGraphics();
            g.drawImage(original.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null);
            g.dispose();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(0.5f);
            ImageOutputStream ios = ImageIO.createImageOutputStream(out);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(resized, null, null), params);
            writer.dispose();

            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            return "data:image/png;base64," + b64Json;
        }
    }

    // 좋아요 수 통계
    @Transactional(readOnly = true)
    public Map<String, Integer> getLikesCountStatistics(String type) {
        if (type.equals("genre")) {
            return getLikesCountByGenre();
        }
        if (type.equals("tag")) {
            return getLikesCountByTag();
        }

        throw new IllegalArgumentException("type은 genre 또는 tag만 가능합니다.");
    }

    // 장르별 좋아요 수 합계
    private Map<String, Integer> getLikesCountByGenre() {
        Map<String, Integer> likesCount = new HashMap<>();

        for (Book book : getActiveBooks()) {
            String genre = book.getGenre() != null ? book.getGenre() : "기타";
            int likes = book.getLikes() != null ? book.getLikes() : 0;

            likesCount.put(genre, likesCount.getOrDefault(genre, 0) + likes);
        }

        return likesCount;
    }

    // 태그별 좋아요 수 합계
    private Map<String, Integer> getLikesCountByTag() {
        Map<String, Integer> likesCount = new HashMap<>();

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

                likesCount.put(trimTag, likesCount.getOrDefault(trimTag, 0) + likes);
            }
        }

        return likesCount;
    }
}
