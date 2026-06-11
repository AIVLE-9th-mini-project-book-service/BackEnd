package com.aivle.bookapp.dto;

import jakarta.validation.constraints.NotBlank;

public record BookCreateRequest(

        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @NotBlank(message = "저자명은 필수입니다.")
        String author,

        @NotBlank(message = "장르는 필수입니다.")
        String genre,

        String content,

        String tag,

        String coverImageUrl
) {
}