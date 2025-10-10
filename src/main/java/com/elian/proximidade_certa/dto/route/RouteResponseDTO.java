package com.elian.proximidade_certa.dto.route;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

// Usamos @JsonInclude para não mostrar campos nulos no JSON final
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RouteResponseDTO(Double totalKilometers, Double totalMinutes,
                               // A geometria será uma lista de caminhos, onde cada caminho é uma lista de pontos [lon, lat]
                               List<List<List<Double>>> geometry) {
}
