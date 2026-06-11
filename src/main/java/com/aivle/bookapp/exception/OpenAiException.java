package com.aivle.bookapp.exception;

import lombok.Getter;

@Getter
public class OpenAiException extends RuntimeException {
    private final int statusCode;

    public OpenAiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
