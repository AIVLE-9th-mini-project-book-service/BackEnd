package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
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
import java.util.Map;

@Service
@RequiredArgsConstructor // final 붙은 키워드들만 초기화
public class BookService {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public Book findById(Long id){
        return bookRepository.findById(id).orElseThrow(()
                ->new BookNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Book> findAll(){
        return bookRepository.findAll();
    }



    @Transactional(readOnly = true)
    public long count(){
        return bookRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Book> searchByTitle(String title){
        return bookRepository.findByTitle(title);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByKeyword(String keyword){
        return bookRepository.findByTitleContaining(keyword);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByTitleAndAuthor(String title, String author){
        return bookRepository.findByTitleAndAuthor(title,author);
    }

    @Transactional(readOnly = true)
    public List<String> authorGetTitle(String author){
        List<Book> books = bookRepository.findByAuthor(author);
        return books.stream().map(book-> book.getTitle()).toList();
    }

    @Transactional(readOnly = true)
    public Page<Book> getPage(int page, int size, String sortBy){
        Sort sort = Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return bookRepository.findAll(pageable);
    }

    @Transactional
    public Book bookCreate(Book book) {
        return bookRepository.save(book);
    }


    //--------------------미프 5차----------------------------

    //도서 수정
    @Transactional
    public Book update(Long id, Book book) {
        Book existing = findById(id);

        if(book.getTitle()!= null){
            existing.setTitle(book.getTitle());
        }
        if(book.getAuthor()!= null){
            existing.setAuthor(book.getAuthor());
        }

        if(book.getGenre()!= null){
            existing.setGenre(book.getGenre());
        }

        if(book.getContent()!= null){
            existing.setContent(book.getContent());
        }

        if(book.getTag()!= null){
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


    //도서 휴지통 이동
    @Transactional
    public Book moveToTrash(Long id) {
        Book existing = findById(id);
        existing.setDeletedAt(LocalDateTime.now());
        return bookRepository.save(existing);
    }

    //도서 휴지통 복원
    @Transactional
    public Book restore(Long id) {
        Book existing = findById(id);
        existing.setDeletedAt(null);
        return bookRepository.save(existing);
    }
    //AI 표지 이미지 저장
    @Transactional
    public Book saveImgUrl(Long id, Book book) {
        Book existing = findById(id);
        if (book.getCoverImageUrl() != null) {
            existing.setCoverImageUrl(book.getCoverImageUrl());
        }
        return bookRepository.save(existing);
    }

    //도서 영구 삭제
    @Transactional
    public void deleteBook(Long id){
        if(bookRepository.existsById(id)){
            bookRepository.deleteById(id);
        }else{
            throw new BookNotFoundException(id);
        }
    }



    //삭제되지 않은 도서만 가져오기
    private List<Book> getActiveBooks() {
        return bookRepository.findAll()
                .stream()
                //.filter(book -> book.getDeletedAt() == null)
                .toList();
    }

    //도서 수 통계
    public Map<String, Long> getBookCountStatistics(String type) {
        if (type.equals("genre")) {
            return  getBookCountByGenre();
        }
        if (type.equals("tag")) {
            return getBookCountByTag();
        }

        throw new IllegalArgumentException("type은 genre 또는 tag만 가능합니다.");
    }

    //장르별 도서 수 합계
    private Map<String, Long> getBookCountByGenre() {
        Map<String, Long> bookCount = new HashMap<>();

        for (Book book : getActiveBooks()) {
            String genre = book.getGenre() != null ? book.getGenre() : "기타";
            bookCount.put(genre, bookCount.getOrDefault(genre, 0L) + 1);
        }

        return bookCount;
    }

    //태그별 도서 수 합계
    private Map<String, Long> getBookCountByTag() {
        Map<String, Long> bookCount = new HashMap<>();

        for (Book book : getActiveBooks()) {
            if (book.getTag() == null || book.getTag().trim().isEmpty()) continue;

            String[] tags = book.getTag().split(",");

            for (String tag : tags) {
                String trimTag = tag.trim();
                if (trimTag.isEmpty()) continue;

                bookCount.put(trimTag, bookCount.getOrDefault(trimTag, 0L) + 1);
            }
        }

        return bookCount;
    }


    //좋아요 수 통계
    public Map<String, Integer> getLikesCountStatistics(String type) {
        if (type.equals("genre")) {
            return getLikesCountByGenre();
        }

        if (type.equals("tag")) {
            return getLikesCountByTag();
        }

        throw new IllegalArgumentException("type은 genre 또는 tag만 가능합니다.");
    }

    //장르별 좋아요 수 합계
    private Map<String, Integer> getLikesCountByGenre() {
        Map<String, Integer> likesCount = new HashMap<>();

        for (Book book : getActiveBooks()) {
            String genre = book.getGenre() != null ? book.getGenre() : "기타";
            int likes = book.getLikes() != null ? book.getLikes() : 0;

            likesCount.put(genre, likesCount.getOrDefault(genre, 0) + likes);
        }

        return likesCount;
    }

    //태그별 좋아요 수 합계
    private Map<String, Integer> getLikesCountByTag() {
        Map<String, Integer> likesCount = new HashMap<>();

        for (Book book : getActiveBooks()) {
            if (book.getTag() == null || book.getTag().trim().isEmpty()) continue;

            int likes = book.getLikes() != null ? book.getLikes() : 0;
            String[] tags = book.getTag().split(",");

            for (String tag : tags) {
                String trimTag = tag.trim();
                if (trimTag.isEmpty()) continue;

                likesCount.put(trimTag, likesCount.getOrDefault(trimTag, 0) + likes);
            }
        }

        return likesCount;
    }

}
