package com.aivle.bookapp.exception;

public class BookNotFountException extends RuntimeException{

    public BookNotFountException(Long id){
        super(id+"번의 책이 존재하지 않습니다.");
    }
}
