package com.aivle.bookapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String author;

    @NotBlank
    @Column(nullable = false)
    private String genre;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column
    private String tag;

    @Column(columnDefinition = "TEXT")
    private String coverImageUrl;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likes = 0;

    @Column
    private java.time.LocalDateTime createdAt;

    @Column
    private java.time.LocalDateTime updatedAt;

    @Column
    private java.time.LocalDateTime deletedAt;
}