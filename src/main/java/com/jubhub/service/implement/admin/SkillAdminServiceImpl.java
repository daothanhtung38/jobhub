package com.jubhub.service.implement.admin;

import com.jubhub.common.SlugUtil;
import com.jubhub.exception.DuplicateResourceException;
import com.jubhub.mapper.SkillMapper;
import com.jubhub.model.dto.request.SkillCreateRequest;
import com.jubhub.model.dto.request.SkillUpdateRequest;
import com.jubhub.model.dto.response.SkillResponse;
import com.jubhub.model.entity.Skill;
import com.jubhub.respository.SkillRepository;
import com.jubhub.service.interfaces.admin.SkillAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkillAdminServiceImpl implements SkillAdminService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    @Override
    public SkillResponse createSkill(SkillCreateRequest request) {
        // Kiểm tra trùng tên
        if (skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "Kỹ năng " + request.getName() + "đã tồn tại", HttpStatus.NOT_FOUND
            );
        }

        // Tạo slug
        String slug = (request.getSlug() != null && !request.getSlug().isBlank())
                ? SlugUtil.toSlug(request.getSlug())
                : SlugUtil.toSlug(request.getName());

        if (skillRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException(
                    "Slug " + slug + "đã tồn tại", HttpStatus.NOT_FOUND
            );
        }

        Skill skill = skillMapper.toEntity(request, slug);

        Skill saved = skillRepository.save(skill);
        return skillMapper.toResponse(saved);
    }

    @Override
    public Page<SkillResponse> getAllSkills(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, 100), Sort.by("name").ascending());
        Page<Skill> skills = skillRepository.findAll(pageable);
        return skills.map(skillMapper::toResponse);
    }

    @Override
    public SkillResponse getSkillById(UUID id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new DuplicateResourceException("Không tìm thấy skill nào có id " + id, HttpStatus.NOT_FOUND));
        return skillMapper.toResponse(skill);
    }

    @Override
    public SkillResponse updateSkill(UUID id, SkillUpdateRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new DuplicateResourceException("Không tìm thấy skill nào có id " + id, HttpStatus.NOT_FOUND));

        String trimmedName = request.getName().trim();
        // Kiểm tra tên có trùng với kỹ năng khác không
        if (skillRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new DuplicateResourceException("Tên kỹ năng '" + trimmedName + "' đã tồn tại ở kỹ năng khác!", HttpStatus.CONFLICT);
        }

        // Tạo lại slug nếu có thay đổi
        String slug = (request.getSlug() != null && !request.getSlug().isBlank())
                ? SlugUtil.toSlug(request.getSlug())
                : SlugUtil.toSlug(trimmedName);
        if (skillRepository.existsBySlugAndIdNot(slug, id)) {
            throw new DuplicateResourceException("Slug '" + slug + "' đã tồn tại ở kỹ năng khác!", HttpStatus.CONFLICT);
        }
        skillMapper.updateEntityFromRequest(request, trimmedName, slug, skill);
        Skill updated = skillRepository.save(skill);
        return skillMapper.toResponse(updated);
    }

    @Override
    public void deleteSkill(UUID id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new DuplicateResourceException("Không tìm thấy skill nào có id " + id, HttpStatus.NOT_FOUND));
         skillRepository.delete(skill);
    }
}
