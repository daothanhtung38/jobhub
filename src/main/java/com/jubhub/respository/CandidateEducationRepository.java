package com.jubhub.respository;

import com.jubhub.model.entity.CandidateEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateEducationRepository extends JpaRepository<CandidateEducation, UUID> {

    // Lấy danh sách học vấn của ứng viên, sắp xếp ngày bắt đầu mới nhất lên đầu
    List<CandidateEducation> findByCandidateIdOrderByStartDateDesc(UUID candidateId);

    // Tìm một học vấn theo id VÀ phải thuộc đúng ứng viên đó
    Optional<CandidateEducation> findByIdAndCandidateId(UUID id, UUID candidateId);
}
