package com.elian.proximidade_certa.services;

import com.elian.proximidade_certa.dto.RatingRequestDTO;
import com.elian.proximidade_certa.dto.RatingResponseDTO;
import com.elian.proximidade_certa.entities.Establishment;
import com.elian.proximidade_certa.entities.Rating;
import com.elian.proximidade_certa.exceptions.ResourceNotFoundException;
import com.elian.proximidade_certa.repositories.EstablishmentRepository;
import com.elian.proximidade_certa.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Transactional
    public RatingResponseDTO create(Long establishmentId, RatingRequestDTO dto) {
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com id: " + establishmentId));

        Rating newRating = new Rating();
        newRating.setScore(dto.score());
        newRating.setComment(dto.comment());
        newRating.setAuthorName(dto.authorName());

        newRating.setEstablishment(establishment);

        Rating savedRating = ratingRepository.save(newRating);

        return new RatingResponseDTO(savedRating);
    }

    @Transactional
    public RatingResponseDTO update(Long establishmentId, Long ratingId, RatingRequestDTO dto) {
        if (!establishmentRepository.existsById(establishmentId)) {
            throw new ResourceNotFoundException("Estabelecimento não encontrado com id: " + establishmentId);
        }

        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com id: " + ratingId));

        if (!rating.getEstablishment().getId().equals(establishmentId)) {
            throw new ResourceNotFoundException("A avaliação " + ratingId + " não pertence ao estabelecimento " + establishmentId);
        }

        rating.setScore(dto.score());
        rating.setComment(dto.comment());
        rating.setAuthorName(dto.authorName());

        Rating updatedRating = ratingRepository.save(rating);
        return new RatingResponseDTO(updatedRating);
    }

    @Transactional
    public void delete(Long establishmentId, Long ratingId) {
        if (!establishmentRepository.existsById(establishmentId)) {
            throw new ResourceNotFoundException("Estabelecimento não encontrado com id: " + establishmentId);
        }

        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com id: " + ratingId));

        if (!rating.getEstablishment().getId().equals(establishmentId)) {
            throw new ResourceNotFoundException("A avaliação " + ratingId + " não pertence ao estabelecimento " + establishmentId);
        }

        ratingRepository.delete(rating);
    }
}
