package com.jubhub.respository;

import com.jubhub.model.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Kiểm tra email người dùng đăng ký chưa
    boolean existsByEmail(String email);

    // Tìm user theo email (eager load roles)
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByEmail(String email);
}
