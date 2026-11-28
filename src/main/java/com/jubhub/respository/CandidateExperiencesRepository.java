package com.jubhub.respository;

import com.jubhub.model.entity.CandidateExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateExperiencesRepository extends JpaRepository<CandidateExperience, UUID> {
    List<CandidateExperience> findByCandidateId(UUID candidateId);
    Optional<CandidateExperience> findByIdAndCandidateId(UUID id, UUID candidateId);
}
