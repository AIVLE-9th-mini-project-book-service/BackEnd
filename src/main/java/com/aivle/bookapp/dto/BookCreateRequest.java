package com.aivle.bookapp.dto;

import jakarta.validation.constraints.NotBlank;

public record BookCreateRequest(

        @NotBlank
        String title,

        @NotBlank
        String author,

        @NotBlank
        String genre,

        String content,

        String tag,

        String coverImageUrl
) {
}