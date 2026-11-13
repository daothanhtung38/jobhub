package com.jubhub.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Đọc Authorization Header
        final String authHeader = request.getHeader("Authorization");

        // Không có Bearer Token thì chuyển request tiếp
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy JWT từ Header (loại bỏ khoảng trắng thừa nếu có)
        final String jwt = authHeader.substring(7).trim();

        try {
            // Lấy username từ JWT
            final String username = jwtService.extractUsername(jwt);

            // Chỉ xác thực nếu request chưa được xác thực
            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Tìm thông tin User
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                // Kiểm tra JWT
                if (jwtService.isTokenVaild(jwt, userDetails.getUsername())) {

                    // Tạo Authentication
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // Lưu thông tin xác thực
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                } else {
                    log.warn("JWT token không hợp lệ hoặc đã hết hạn đối với user: {}", username);
                }
            }

        } catch (Exception exception) {
            log.error("Lỗi khi xác thực JWT: {}", exception.getMessage());
            SecurityContextHolder.clearContext();
        }

        // Tiếp tục Filter Chain
        filterChain.doFilter(request, response);
    }
}
