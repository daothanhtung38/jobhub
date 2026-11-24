-- ==============================================================================
-- DATABASE SCHEMA & MIGRATION: AUTH & USER MANAGEMENT (JobHub)
-- Nhóm A – Authentication & Authorization
-- PostgreSQL Dialect
-- ==============================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==============================================================================
-- 0. TỰ ĐỘNG XỬ LÝ NÂNG CẤP TỪ BẢNG CŨ (NẾU CÓ)
-- ==============================================================================
ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE IF EXISTS users ALTER COLUMN status TYPE VARCHAR(30);

ALTER TABLE IF EXISTS roles ALTER COLUMN is_system DROP NOT NULL;
ALTER TABLE IF EXISTS roles ALTER COLUMN is_system SET DEFAULT TRUE;

-- Xóa bảng refresh_tokens cũ nếu còn cấu trúc cũ để tự động tạo lại chuẩn hóa với token_hash và revoked_at
DROP TABLE IF EXISTS refresh_tokens CASCADE;

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
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);

-- ==============================================================================
-- 2. BẢNG roles
-- ==============================================================================
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_system BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- 3. BẢNG user_roles: QUAN HỆ User - Role (PK kết hợp user_id, role_id)
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
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- ==============================================================================
-- 5. BẢNG email_verifications: XÁC MINH EMAIL (NẾU CÓ DÙNG)
-- ==============================================================================
CREATE TABLE IF NOT EXISTS email_verifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    verified_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_email_verifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_email_verifications_token ON email_verifications(token);

-- ==============================================================================
-- 6. BẢNG password_reset_tokens: ĐẶT LẠI MẬT KHẨU (NẾU CÓ DÙNG)
-- ==============================================================================
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
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


-- ==============================================================================
-- NHÓM B – CANDIDATE PROFILE (HỒ SƠ ỨNG VIÊN)
-- ==============================================================================

-- 1. BẢNG candidate_profiles: THÔNG TIN HỒ SƠ ỨNG VIÊN (QUAN HỆ 1:1 VỚI USERS)
CREATE TABLE IF NOT EXISTS candidate_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    headline VARCHAR(255),
    date_of_birth DATE,
    gender VARCHAR(20),
    address TEXT,
    province_code VARCHAR(20),
    summary TEXT,
    years_of_experience NUMERIC(4, 1) DEFAULT 0,
    expected_salary_min NUMERIC(15, 2),
    expected_salary_max NUMERIC(15, 2),
    preferred_job_title VARCHAR(255),
    employment_status VARCHAR(30) NOT NULL DEFAULT 'OPEN_TO_WORK',
    is_searchable BOOLEAN NOT NULL DEFAULT FALSE,
    profile_visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_candidate_profiles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_candidate_employment_status CHECK (employment_status IN ('OPEN_TO_WORK', 'NOT_LOOKING')),
    CONSTRAINT chk_candidate_profile_visibility CHECK (profile_visibility IN ('PUBLIC', 'PRIVATE')),
    CONSTRAINT chk_candidate_salary_range CHECK (expected_salary_max IS NULL OR expected_salary_min IS NULL OR expected_salary_max >= expected_salary_min)
);

CREATE INDEX IF NOT EXISTS idx_candidate_profiles_user_id ON candidate_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_candidate_profiles_search ON candidate_profiles(is_searchable, profile_visibility, employment_status);
CREATE INDEX IF NOT EXISTS idx_candidate_profiles_province ON candidate_profiles(province_code);

-- 2. BẢNG candidate_educations: HỌC VẤN CỦA ỨNG VIÊN
CREATE TABLE IF NOT EXISTS candidate_educations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL,
    school_name VARCHAR(255) NOT NULL,
    degree VARCHAR(100),
    major VARCHAR(150),
    start_date DATE NOT NULL,
    end_date DATE,
    description TEXT,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_candidate_educations_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles (id) ON DELETE CASCADE,
    CONSTRAINT chk_education_dates CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE INDEX IF NOT EXISTS idx_candidate_educations_candidate_id ON candidate_educations(candidate_id);

-- 3. BẢNG candidate_experiences: KINH NGHIỆM LÀM VIỆC
CREATE TABLE IF NOT EXISTS candidate_experiences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    job_title VARCHAR(150) NOT NULL,
    employment_type VARCHAR(50),
    start_date DATE NOT NULL,
    end_date DATE,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_candidate_experiences_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles (id) ON DELETE CASCADE,
    CONSTRAINT chk_experience_dates CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE INDEX IF NOT EXISTS idx_candidate_experiences_candidate_id ON candidate_experiences(candidate_id);

-- 4. BẢNG candidate_projects: DỰ ÁN CỦA ỨNG VIÊN
CREATE TABLE IF NOT EXISTS candidate_projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    role VARCHAR(150),
    technologies TEXT,
    project_url VARCHAR(500),
    github_url VARCHAR(500),
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_candidate_projects_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles (id) ON DELETE CASCADE,
    CONSTRAINT chk_project_dates CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);

CREATE INDEX IF NOT EXISTS idx_candidate_projects_candidate_id ON candidate_projects(candidate_id);

-- 5. BẢNG candidate_certifications: CHỨNG CHỈ CỦA ỨNG VIÊN
CREATE TABLE IF NOT EXISTS candidate_certifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    issuer VARCHAR(255) NOT NULL,
    issued_at DATE NOT NULL,
    expires_at DATE,
    credential_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_candidate_certifications_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles (id) ON DELETE CASCADE,
    CONSTRAINT chk_certification_dates CHECK (expires_at IS NULL OR expires_at >= issued_at)
);

CREATE INDEX IF NOT EXISTS idx_candidate_certifications_candidate_id ON candidate_certifications(candidate_id);

-- 6. BẢNG skills: DANH MỤC KỸ NĂNG CHUẨN
CREATE TABLE IF NOT EXISTS skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_skills_slug ON skills(slug);
CREATE INDEX IF NOT EXISTS idx_skills_category ON skills(category);

-- 7. BẢNG candidate_skills: KỸ NĂNG CỦA ỨNG VIÊN (PK KẾT HỢP candidate_id, skill_id)
CREATE TABLE IF NOT EXISTS candidate_skills (
    candidate_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    proficiency_level VARCHAR(30),
    years_of_experience NUMERIC(4, 1) DEFAULT 0,
    PRIMARY KEY (candidate_id, skill_id),
    CONSTRAINT fk_candidate_skills_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_candidate_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_candidate_skills_candidate_id ON candidate_skills(candidate_id);
CREATE INDEX IF NOT EXISTS idx_candidate_skills_skill_id ON candidate_skills(skill_id);

-- 8. BẢNG companies (BẢNG ĐẠI DIỆN CHO FK CỦA candidate_hidden_companies TRƯỚC KHI TẠO NHÓM C)
CREATE TABLE IF NOT EXISTS companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 9. BẢNG candidate_hidden_companies: CÔNG TY BỊ ỨNG VIÊN CHẶN (PK KẾT HỢP candidate_id, company_id)
CREATE TABLE IF NOT EXISTS candidate_hidden_companies (
    candidate_id UUID NOT NULL,
    company_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (candidate_id, company_id),
    CONSTRAINT fk_hidden_companies_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_hidden_companies_company FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_hidden_companies_candidate_id ON candidate_hidden_companies(candidate_id);
CREATE INDEX IF NOT EXISTS idx_hidden_companies_company_id ON candidate_hidden_companies(company_id);


-- ==============================================================================
-- NHÓM C – QUẢN LÝ CV (CANDIDATE CVS)
-- ==============================================================================

-- 1. BẢNG candidate_cvs: QUẢN LÝ CV ỨNG VIÊN (UPLOADED & ONLINE)
CREATE TABLE IF NOT EXISTS candidate_cvs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    cv_type VARCHAR(20) NOT NULL,
    file_key TEXT,
    original_file_name VARCHAR(255),
    mime_type VARCHAR(100),
    file_size BIGINT,
    template_id UUID,
    content_json JSONB,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_searchable BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_candidate_cvs_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles (id) ON DELETE CASCADE,
    CONSTRAINT chk_candidate_cvs_type CHECK (cv_type IN ('UPLOADED', 'ONLINE'))
);

-- Index tra cứu danh sách CV của ứng viên
CREATE INDEX IF NOT EXISTS idx_candidate_cvs_candidate_id ON candidate_cvs(candidate_id);

-- Index phục vụ tìm kiếm CV cho phép NTD tìm kiếm
CREATE INDEX IF NOT EXISTS idx_candidate_cvs_searchable ON candidate_cvs(candidate_id, is_searchable);

-- PARTIAL UNIQUE INDEX: Mỗi Candidate chỉ có tối đa một CV mặc định (is_default = TRUE)
CREATE UNIQUE INDEX IF NOT EXISTS idx_candidate_cvs_unique_default 
ON candidate_cvs (candidate_id) 
WHERE is_default = TRUE;
