package com.aivle.bookapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CoverImageUpdateRequest {

    @NotBlank(message = "coverImageUrl은 필수입니다.")
    private String coverImageUrl;
}
