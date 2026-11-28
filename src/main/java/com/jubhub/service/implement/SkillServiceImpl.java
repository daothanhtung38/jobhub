package com.jubhub.service.implement;

import com.jubhub.mapper.SkillMapper;
import com.jubhub.model.dto.request.SkillCreateRequest;
import com.jubhub.model.dto.request.SkillUpdateRequest;
import com.jubhub.model.dto.response.SkillResponse;
import com.jubhub.model.entity.Skill;
import com.jubhub.respository.SkillRepository;
import com.jubhub.service.interfaces.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Override
    public Page<SkillResponse> searchSkills(String keyword, int page, int size) {

        // Chuẩn hóa từ khóa
        String searchKeyword = keyword == null
                ? ""
                : keyword.trim();

         // Kiểm tra phân trang
        int validPage = Math.max(page, 0);

        int vaildSize = Math.clamp(size, 1, 50);

        // Tao pageable
        Pageable pageable = PageRequest.of(
                validPage, vaildSize, Sort.by(
                        Sort.Order.asc("name"),
                        Sort.Order.asc("id")
                )
        );

        // Tìm kiếm kĩ năng
        Page<Skill> skills = skillRepository.findByNameContainingIgnoreCase(searchKeyword, pageable);
        return skills.map(skillMapper::toResponse);
    }

}

