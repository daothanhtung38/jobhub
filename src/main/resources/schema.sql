-- ==============================================================================
-- DATABASE SCHEMA & MIGRATION: AUTH & USER MANAGEMENT (JobHub)
-- PostgreSQL Dialect
-- ==============================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==============================================================================
-- 0. TỰ ĐỘNG XỬ LÝ NÂNG CẤP TỪ BẢNG CŨ (NẾU CÓ)
-- ==============================================================================

-- Bỏ ràng buộc NOT NULL hoặc đặt mặc định cho is_system nếu bảng roles cũ tồn tại
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'roles' AND column_name = 'is_system') THEN
        ALTER TABLE roles ALTER COLUMN is_system DROP NOT NULL;
        ALTER TABLE roles ALTER COLUMN is_system SET DEFAULT TRUE;
    END IF;
END $$;

-- Đồng bộ is_email_verified -> email_verified nếu bảng users cũ tồn tại
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'is_email_verified') THEN
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'email_verified') THEN
            ALTER TABLE users RENAME COLUMN is_email_verified TO email_verified;
        ELSE
            ALTER TABLE users ALTER COLUMN is_email_verified DROP NOT NULL;
            UPDATE users SET email_verified = is_email_verified WHERE email_verified IS NULL;
        END IF;
    END IF;
END $$;

-- ==============================================================================
-- 1. BẢNG users
-- ==============================================================================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    avatar_url TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- ==============================================================================
-- 2. BẢNG roles
-- ==============================================================================
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_system BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- 3. BẢNG user_roles: QUAN HỆ User - Role
-- ==============================================================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);

-- ==============================================================================
-- 4. BẢNG refresh_tokens: QUẢN LÝ REFRESH TOKEN
-- ==============================================================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- ==============================================================================
-- 5. BẢNG email_verifications: XÁC MINH EMAIL
-- ==============================================================================
CREATE TABLE IF NOT EXISTS email_verifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    verified_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_email_verifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_email_verifications_token ON email_verifications(token);

-- ==============================================================================
-- 6. BẢNG password_reset_tokens: ĐẶT LẠI MẬT KHẨU
-- ==============================================================================
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    used_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token ON password_reset_tokens(token);

-- ==============================================================================
-- KHỞI TẠO CÁC ROLE HỆ THỐNG
-- ==============================================================================
INSERT INTO roles (id, name, description, is_system, created_at)
VALUES 
    (gen_random_uuid(), 'ROLE_CANDIDATE', 'Vai trò ứng viên tìm việc', TRUE, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'ROLE_RECRUITER', 'Vai trò nhà tuyển dụng', TRUE, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'ROLE_ADMIN', 'Vai trò quản trị viên hệ thống', TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;
