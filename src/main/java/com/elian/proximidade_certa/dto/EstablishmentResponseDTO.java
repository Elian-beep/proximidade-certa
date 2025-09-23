package com.elian.proximidade_certa.dto;

import com.elian.proximidade_certa.entities.Establishment;

public record EstablishmentResponseDTO(Long id, String name, String description, String category, double latitude, double longitude) {
    public EstablishmentResponseDTO(Establishment entity){
        this(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getLocation().getY(),
                entity.getLocation().getX()
        );
    }
}
