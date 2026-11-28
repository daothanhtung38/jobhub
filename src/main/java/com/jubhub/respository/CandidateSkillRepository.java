package com.jubhub.respository;

import com.jubhub.model.entity.CandidateSkill;
import com.jubhub.model.entity.CandidateSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, CandidateSkillId> {
    List<CandidateSkill> findByCandidateId(UUID candidateId);
}
