package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor // final 붙은 키워드들만 초기화
public class BookController {

    //-----------테스트용 코드--------

    private final BookService bookService;

    @GetMapping("/books/{id}")
    public Book getBook(@PathVariable Long id){
       return bookService.findById(id);
    }

    @GetMapping("/books")
    public List<Book> getAll(){
       return bookService.findAll();
    }

    @GetMapping("/books/count")
    public long getCount(){
        return bookService.count();
    }

    @GetMapping("/books/search/title")
    public List<Book> searchByTitle(@RequestParam String title){
        return bookService.searchByTitle(title);
    }

    @GetMapping("/books/search")
    public List<Book> searchByKeyword(@RequestParam String keyword){
        return bookService.searchByKeyword(keyword);
    }

    @GetMapping("/books/search/detail")
    public List<Book> searchByTileAndAuthor(@RequestParam String title, String author){
        return bookService.searchByTitleAndAuthor(title,author);
    }

    @GetMapping("/books/search/author")
    public List<String> authorGetTitle(@RequestParam String author){
        return bookService.authorGetTitle(author);
    }

    @GetMapping("/books/page")
    public Page<Book> getPage(@RequestParam int page, @RequestParam int size, @RequestParam String sortBy){
        return bookService.getPage(page, size, sortBy);
    }

    @PostMapping("/books")
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book){

        Book saved = bookService.bookCreate(book);
       return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }







    //-----------5차 미프 update, delete-----------

    //도서 수정
    @PatchMapping("/books/{id}")
    public ResponseEntity<Map<String, Object>> updateBook(@PathVariable Long id, @RequestBody Book book){
        Book updatedBook = bookService.update(id, book);
        Map<String, Object> body = Map.of(
                "id", updatedBook.getId(),
                "message", "도서 수정 성공"
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    //도서 삭제(휴지통 이동)
    @PatchMapping("/books/trash/{id}")
    public ResponseEntity<Map<String, Object>>  moveToTrash(@PathVariable Long id){
        bookService.moveToTrash(id);
        Map<String, Object> body = Map.of(
                "message", "도서 삭제 성공"
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    //도서 복원
    @PatchMapping("/books/restore/{id}")
    public ResponseEntity<Map<String, Object>> restore(@PathVariable Long id){
        bookService.restore(id);
        Map<String, Object> body = Map.of(
                "message", "도서 복원 성공"
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    //AI 표지 이미지 저장
    @PatchMapping("/books/{id}/cover")
    public ResponseEntity<Map<String, Object>>  saveImgUrl(@PathVariable Long id, @RequestBody Book book){
        Book updatedBook = bookService.saveImgUrl(id, book);
        Map<String, Object> body = Map.of(
                "id", updatedBook.getId(),
                "message", "도서 수정 성공",
                "coverImageUrl",updatedBook.getCoverImageUrl()
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    //도서 영구 삭제
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        Map<String, Object> body = Map.of(
                "message", "도서 영구 삭제 성공"
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
