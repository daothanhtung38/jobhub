package com.jubhub.respository;

import com.jubhub.model.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
    Page<Skill> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    // Kiểm tra trùng tên khi thêm mới
    boolean existsByNameIgnoreCase(String name);

    // Kiểm tra trùng slug khi thêm mới
    boolean existsBySlug(String slug);

    // Kiểm tra trùng tên khi cập nhật (ngoại trừ chính kỹ năng đang sửa)
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    // Kiểm tra trùng slug khi cập nhật
    boolean existsBySlugAndIdNot(String slug, UUID id);

}
