package com.elian.proximidade_certa.dto;

import com.elian.proximidade_certa.entities.Rating;

import java.time.Instant;

public record RatingResponseDTO(Long id, Integer score, String comment, String authorName, Instant createdAt) {
    public RatingResponseDTO(Rating entity) {
        this(entity.getId(), entity.getScore(), entity.getComment(), entity.getAuthorName(), entity.getCreatedAt());
    }
}
