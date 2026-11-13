package com.jubhub.security;

import com.jubhub.exception.DuplicateResourceException;
import com.jubhub.model.entity.User;
import com.jubhub.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DuplicateResourceException("Không tìm thấy user với email" + email, HttpStatus.NOT_FOUND));
        return new CustomUserDetails(user);
    }
}
