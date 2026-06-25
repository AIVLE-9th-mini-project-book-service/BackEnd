package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.dto.AiBookSummaryResponse;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.exception.GlobalExceptionHandler;
import com.aivle.bookapp.exception.OpenAiException;
import com.aivle.bookapp.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private Book mockBook;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockBook = new Book();
        mockBook.setId(1L);
        mockBook.setTitle("어린왕자");
        mockBook.setAuthor("생텍쥐페리");
        mockBook.setGenre("소설");
        mockBook.setContent("사막에서 만난 어린 왕자 이야기");
        mockBook.setSummary("순수한 시선으로 관계의 의미를 돌아보게 하는 이야기");
        mockBook.setCoverImageUrl("https://example.com/cover.jpg");
        mockBook.setLikes(0);
        mockBook.replaceTags(List.of("모험", "우정"));
    }

    @Test
    @DisplayName("AI 한줄평 생성 - 정상 (200)")
    void generateSummary_success() throws Exception {
        when(bookService.generateSummary(eq(1L), any()))
                .thenReturn(new AiBookSummaryResponse("순수한 시선으로 관계의 의미를 돌아보게 하는 이야기"));

        mockMvc.perform(post("/books/1/summary/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "apiKey": "sk-test123" }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("순수한 시선으로 관계의 의미를 돌아보게 하는 이야기"));
    }

    @Test
    @DisplayName("AI 한줄평 생성 - apiKey 없으면 400")
    void generateSummary_missingApiKey() throws Exception {
        mockMvc.perform(post("/books/1/summary/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("API Key는 필수입니다."));
    }

    @Test
    @DisplayName("AI 한줄평 생성 - 존재하지 않는 도서면 404")
    void generateSummary_bookNotFound() throws Exception {
        when(bookService.generateSummary(eq(999L), any()))
                .thenThrow(new BookNotFoundException(999L));

        mockMvc.perform(post("/books/999/summary/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "apiKey": "sk-test123" }
                                """))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found"));
    }

    @Test
    @DisplayName("AI 한줄평 생성 - API Key 오류면 401")
    void generateSummary_invalidApiKey() throws Exception {
        when(bookService.generateSummary(eq(1L), any()))
                .thenThrow(new OpenAiException(401, "API Key가 올바르지 않습니다."));

        mockMvc.perform(post("/books/1/summary/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "apiKey": "sk-wrong" }
                                """))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("OpenAI Error"))
                .andExpect(jsonPath("$.message").value("API Key가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("AI 한줄평 저장 - 정상 (200)")
    void saveSummary_success() throws Exception {
        when(bookService.saveSummary(eq(1L), eq("순수한 시선으로 관계의 의미를 돌아보게 하는 이야기")))
                .thenReturn(mockBook);

        mockMvc.perform(patch("/books/1/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "summary": "순수한 시선으로 관계의 의미를 돌아보게 하는 이야기" }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("어린왕자"))
                .andExpect(jsonPath("$.summary").value("순수한 시선으로 관계의 의미를 돌아보게 하는 이야기"))
                .andExpect(jsonPath("$.tag").value("모험,우정"));
    }

    @Test
    @DisplayName("AI 한줄평 저장 - summary 없으면 400")
    void saveSummary_missingSummary() throws Exception {
        mockMvc.perform(patch("/books/1/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("한줄평은 비워둘 수 없습니다."));
    }

    @Test
    @DisplayName("AI 한줄평 저장 - 존재하지 않는 도서면 404")
    void saveSummary_bookNotFound() throws Exception {
        when(bookService.saveSummary(eq(999L), eq("저장할 한줄평")))
                .thenThrow(new BookNotFoundException(999L));

        mockMvc.perform(patch("/books/999/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "summary": "저장할 한줄평" }
                                """))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found"));
    }
}
