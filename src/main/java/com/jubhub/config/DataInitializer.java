package com.jubhub.config;

import com.jubhub.model.entity.Role;
import com.jubhub.model.enums.RoleType;
import com.jubhub.respository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        initRoles();
    }

    private void initRoles() {
        for (RoleType roleType : RoleType.values()) {
            if (!roleRepository.existsByName(roleType)) {
                String description = switch (roleType) {
                    case ROLE_CANDIDATE -> "Vai trò ứng viên tìm việc";
                    case ROLE_RECRUITER -> "Vai trò nhà tuyển dụng";
                    case ROLE_ADMIN -> "Vai trò quản trị viên hệ thống";
                };

                Role role = Role.builder()
                        .name(roleType)
                        .description(description)
                        .isSystem(true)
                        .build();

                roleRepository.save(role);
                log.info("Khởi tạo vai trò hệ thống: {}", roleType);
            }
        }
    }
}

