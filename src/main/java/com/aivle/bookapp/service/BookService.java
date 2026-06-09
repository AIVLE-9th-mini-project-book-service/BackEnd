package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.exception.BookNotFountException;
import com.aivle.bookapp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor // final 붙은 키워드들만 초기화
public class BookService {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public Book findById(Long id){
        return bookRepository.findById(id).orElseThrow(()
                ->new BookNotFountException(id));
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
            throw new BookNotFountException(id);
        }
    }

}
