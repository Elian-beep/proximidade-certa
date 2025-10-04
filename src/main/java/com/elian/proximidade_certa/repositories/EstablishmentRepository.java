package com.elian.proximidade_certa.repositories;

import com.elian.proximidade_certa.dto.EstablishmentResponseDTO;
import com.elian.proximidade_certa.entities.Establishment;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.Optional;

@Repository
public interface EstablishmentRepository extends JpaRepository<Establishment, Long>, JpaSpecificationExecutor<Establishment> {
    // Busca os estabelecimentos e já calcula a média de avaliações no mesmo select.
    /*
    new com.elian...DTO(e, ...): O JPQL pode construir DTOs diretamente, o que é muito eficiente.
    LEFT JOIN e.ratings r: Usamos LEFT JOIN para que estabelecimentos sem nenhuma avaliação também apareçam na lista.
    COALESCE(AVG(r.score), 0.0): Calcula a média (AVG). Se a média for NULL (porque não há avaliações), o COALESCE a transforma em 0.0.
    GROUP BY e.id: Agrupa os resultados por estabelecimento para que a média seja calculada corretamente para cada um.
    countQuery: Uma query separada e otimizada que o Spring Data usa para saber o total de elementos e montar a paginação corretamente.
    */
    @Query(value = "SELECT new com.elian.proximidade_certa.dto.EstablishmentResponseDTO(e, COALESCE(AVG(r.score), 0.0)) " +
            "FROM Establishment e LEFT JOIN e.ratings r " +
            "WHERE (LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) " +
            "AND (LOWER(e.category) = LOWER(:category) OR :category IS NULL) " +
            "GROUP BY e.id",
            countQuery = "SELECT COUNT(e) FROM Establishment e " +
                    "WHERE (LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) " +
                    "AND (LOWER(e.category) = LOWER(:category) OR :category IS NULL)")
    Page<EstablishmentResponseDTO> findAllPagedWithAvgRating(
            @Param("name") String name,
            @Param("category") String category,
            Pageable pageable);

    // 'JOIN FETCH' diz ao Hibernate para buscar o estabelecimento E suas avaliações na mesma query.
    // TODO: Revisar a explicação desta query
    @Query("SELECT e FROM Establishment e JOIN FETCH e.ratings WHERE e.id = :id")
    Optional<Establishment> findByIdWithRatings(@Param("id") Long id);

    @Query(nativeQuery = true, value = "SELECT e.* FROM establishments e WHERE ST_DWithin(e.location, :userLocation, :radius) = true")
    Page<Establishment> findNearby(
            @Param("userLocation") Point userLocation,
            @Param("radius") double radius,
            Pageable pageable
    );
}
