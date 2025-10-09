package com.elian.proximidade_certa.services;

import com.elian.proximidade_certa.services.exceptions.GeocodingException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class ArcGisService {
    @Value("${arcgis.api-key}")
    private String apiKey;

    private final String GEOCODE_URL = "https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer/findAddressCandidates";
    private final RestTemplate restTemplate = new RestTemplate();
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public Point geocodeAddress(String street, String city, String state, String postalCode){
        String singleLineAddress = String.format("%s, %s, %s, %s", street, city, state, postalCode);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GEOCODE_URL)
                .queryParam("f", "json")
                .queryParam("token", apiKey)
                .queryParam("singleLine", singleLineAddress)
                .queryParam("outFields", "Match_addr") // Pede o endereço correspondente
                .queryParam("maxLocations", 1);

        // Faz a chamada HTTP e mapeia a resposta JSON para nossos DTOs internos
        ArcGisResponse response = restTemplate.getForObject(builder.toUriString(), ArcGisResponse.class);

        // Extrai as coordenadas da resposta
        if (response != null && !response.candidates().isEmpty()) {
            ArcGisCandidate candidate = response.candidates().get(0);
            double longitude = candidate.location().x();
            double latitude = candidate.location().y();

            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            point.setSRID(4326);
            return point;
        }

        throw new GeocodingException("Não foi possível geocodificar o endereço: " + singleLineAddress);
    }

    // DTOs internos (records) para mapear a resposta JSON da API da ArcGIS
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArcGisResponse(List<ArcGisCandidate> candidates) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArcGisCandidate(ArcGisLocation location) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArcGisLocation(double x, double y) {}
}
