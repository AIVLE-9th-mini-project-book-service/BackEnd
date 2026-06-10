package com.aivle.bookapp.dto;

import java.util.List;

public record BookSearchRequest(
        String keyword,
        List<String> genres,
        List<String> tags
) {
}
