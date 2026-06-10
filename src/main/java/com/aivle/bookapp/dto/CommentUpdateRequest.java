package com.aivle.bookapp.dto;

public record CommentUpdateRequest(
        String text,
        String password
) {}