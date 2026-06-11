package com.aivle.bookapp.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({BookNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(BookNotFoundException e) {
        Map<String, String> body = Map.of("error", "Book not found", "message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // POST BadRequest
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(MethodArgumentNotValidException e){
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        Map<String, String> body = Map.of("error", "Bad Request", "message", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // PATCH BadRequest,
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException (IllegalArgumentException e) {
        Map<String, String> body = Map.of("error","Bad Request", "message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({OpenAiException.class})
    public ResponseEntity<Map<String, String>> handleOpenAiException(OpenAiException e) {
        Map<String, String> body = Map.of("error", "OpenAI Error", "message", e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(body);
    }

}
