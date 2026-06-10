package com.aivle.bookapp.dto;

public record CommentCreateRequest(
        String author,
        String text,
        String password
) {
}