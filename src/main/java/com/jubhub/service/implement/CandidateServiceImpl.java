package com.jubhub.service.implement;

import com.jubhub.exception.DuplicateResourceException;
import com.jubhub.mapper.CandidateProfileMapper;
import com.jubhub.model.dto.request.UpdateCandidateProfileRequest;
import com.jubhub.model.dto.response.CandidateProfileResponse;
import com.jubhub.model.entity.CandidateProfile;
import com.jubhub.model.entity.User;
import com.jubhub.model.enums.EmploymentStatus;
import com.jubhub.model.enums.ProfileVisibility;
import com.jubhub.respository.CandidateProfileRepository;
import com.jubhub.respository.UserRepository;
import com.jubhub.service.interfaces.CandidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final CandidateProfileMapper candidateProfileMapper;

    @Override
    @Transactional
    public CandidateProfileResponse getMyProfile(User currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElse(currentUser);

        CandidateProfile candidateProfile = candidateProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> initDefaultProfile(user));

        return candidateProfileMapper.toResponse(candidateProfile, user);
    }

    @Override
    @Transactional
    public CandidateProfileResponse updateMyProfile(User currentUser, UpdateCandidateProfileRequest request) {
        // 1. Kiểm tra logic mức lương nếu cả 2 được truyền lên
        if (request.getExpectedSalaryMin() != null && request.getExpectedSalaryMax() != null) {
            if (request.getExpectedSalaryMax().compareTo(request.getExpectedSalaryMin()) < 0) {
                throw new IllegalArgumentException("Mức lương tối đa phải lớn hơn hoặc bằng mức lương tối thiểu");
            }
        }

        // 2. Tìm entity User có trạng thái managed từ database
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản người dùng"));

        // Chỉ cập nhật thông tin User nếu có truyền giá trị mới (giữ nguyên dữ liệu cũ nếu null hoặc rỗng)
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            user.setPhone(request.getPhone().trim());
        }
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isBlank()) {
            user.setAvatarUrl(request.getAvatarUrl().trim());
        }

        User userSaved = userRepository.save(user);

        // 3. Lấy hoặc tự động khởi tạo hồ sơ nếu chưa có
        CandidateProfile profile = candidateProfileRepository.findByUserId(userSaved.getId())
                .orElseGet(() -> initDefaultProfile(userSaved));

        // 4. Map dữ liệu cập nhật vào entity (bỏ qua các trường null để giữ nguyên dữ liệu cũ)
        candidateProfileMapper.updateEntityFromRequest(request, profile);
        CandidateProfile savedCandidateProfile = candidateProfileRepository.save(profile);

        return candidateProfileMapper.toResponse(savedCandidateProfile, userSaved);
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateProfileResponse getProfileByCandidateId(UUID candidateId) {
        CandidateProfile candidateProfile = candidateProfileRepository.findById(candidateId)
                .orElseThrow(() -> new DuplicateResourceException(
                        "Không tìm thấy hồ sơ ứng viên với ID: " + candidateId,
                        HttpStatus.NOT_FOUND
                ));

        return candidateProfileMapper.toResponse(candidateProfile);
    }

    private CandidateProfile initDefaultProfile(User user) {
        CandidateProfile newProfile = CandidateProfile.builder()
                .user(user)
                .yearsOfExperience(BigDecimal.ZERO)
                .employmentStatus(EmploymentStatus.OPEN_TO_WORK)
                .isSearchable(false)
                .profileVisibility(ProfileVisibility.PRIVATE)
                .build();

        return candidateProfileRepository.save(newProfile);
    }
}
