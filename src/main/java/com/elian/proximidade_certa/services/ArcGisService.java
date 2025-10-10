package com.elian.proximidade_certa.services;

import com.elian.proximidade_certa.dto.route.RouteResponseDTO;
import com.elian.proximidade_certa.services.exceptions.GeocodingException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

@Service
public class ArcGisService {
    @Value("${arcgis.api-key}")
    private String apiKey;

    private static final Logger logger = LoggerFactory.getLogger(ArcGisService.class);

    private final String GEOCODE_URL = "https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer/findAddressCandidates";
    private final String ROUTE_URL = "https://route-api.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World/solve";
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

    public RouteResponseDTO calculateRouteWithGeometry(Point origin, Point destination) {
        String stops = String.format(Locale.US, "%f,%f;%f,%f", origin.getX(), origin.getY(), destination.getX(), destination.getY());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ROUTE_URL)
                .queryParam("f", "json")
                .queryParam("token", apiKey)
                .queryParam("stops", stops)
                .queryParam("returnRoutes", true);

        // ✅ LOG: Imprime a URL que vamos chamar
        String url = builder.toUriString();
        logger.info("Chamando URL da Rota ArcGIS: {}", url);

        try {
            // ✅ LOG: Pega a resposta como String para podermos vê-la
            String jsonResponse = restTemplate.getForObject(url, String.class);
            logger.info("Resposta Bruta da ArcGIS: {}", jsonResponse);

            // Tenta converter a String JSON para nossos objetos
            ObjectMapper objectMapper = new ObjectMapper();
            FullRouteResponse response = objectMapper.readValue(jsonResponse, FullRouteResponse.class);

            if (response != null && response.routes() != null && !response.routes().features().isEmpty()) {
                FullRouteFeature feature = response.routes().features().get(0);
                return new RouteResponseDTO(
                        feature.attributes().totalKilometers(),
                        feature.attributes().totalMinutes(),
                        feature.geometry().paths()
                );
            }
        } catch (Exception e) {
            logger.error("Falha ao processar a resposta da API de Rota da ArcGIS", e);
        }

        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FullRouteResponse(FullRouteDirections routes) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FullRouteDirections(List<FullRouteFeature> features) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FullRouteFeature(RouteAttributes attributes, RouteGeometry geometry) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record RouteAttributes(@JsonProperty("Total_Kilometers") double totalKilometers, @JsonProperty("Total_Minutes") double totalMinutes) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record RouteGeometry(List<List<List<Double>>> paths) {}
}
