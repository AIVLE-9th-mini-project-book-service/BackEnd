package com.aivle.bookapp.dto;

public record BookUpdateRequest(
        String title,
        String author,
        String genre,
        String content,
        String tag,
        String coverImageUrl,
        String summary
) {}