package com.elian.proximidade_certa.dto;

import com.elian.proximidade_certa.entities.Establishment;

import java.util.List;
import java.util.stream.Collectors;

public record EstablishmentResponseDTO(Long id, String name, String description, String category, double latitude,
                                       double longitude, Double averageScore,
                                       List<RatingResponseDTO> ratings) {
    public EstablishmentResponseDTO(Establishment entity) {
        this(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getLocation().getY(),
                entity.getLocation().getX(),
                entity.getRatings().stream().mapToInt(r -> r.getScore()).average().orElse(0.0),
                entity.getRatings().stream().map(RatingResponseDTO::new).collect(Collectors.toList())
        );
    }

    public EstablishmentResponseDTO(Establishment entity, Double averageScore) {
        this(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getLocation().getY(),
                entity.getLocation().getX(),
                averageScore,
                null
        );
    }

}
