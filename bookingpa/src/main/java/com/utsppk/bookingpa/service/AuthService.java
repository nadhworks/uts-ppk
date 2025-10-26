package com.utsppk.bookingpa.service;

import com.utsppk.bookingpa.dto.request.ChangePasswordRequest;
import com.utsppk.bookingpa.dto.request.LoginRequest;
import com.utsppk.bookingpa.dto.request.RegisterRequest;
import com.utsppk.bookingpa.dto.response.LoginResponse;
import com.utsppk.bookingpa.dto.response.UserResponse;
import com.utsppk.bookingpa.exception.BadRequestException;
import com.utsppk.bookingpa.exception.UnauthorizedException;
import com.utsppk.bookingpa.model.Role;
import com.utsppk.bookingpa.model.User;
import com.utsppk.bookingpa.repository.UserRepository;
import com.utsppk.bookingpa.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Validasi username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username sudah digunakan");
        }

        // Validasi email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email sudah digunakan");
        }

        // Validasi NIM/NIP
        if (userRepository.existsByNimNip(request.getNimNip())) {
            throw new BadRequestException("NIM/NIP sudah digunakan");
        }

        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNama(request.getNama());
        user.setNimNip(request.getNimNip());
        user.setNoTelepon(request.getNoTelepon());
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        user.setActive(true);

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("User tidak ditemukan"));

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(mapToUserResponse(user));

        return response;
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User tidak ditemukan"));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Password lama tidak sesuai");
        }

        // Set new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setNama(user.getNama());
        response.setNimNip(user.getNimNip());
        response.setNoTelepon(user.getNoTelepon());
        response.setRole(user.getRole());
        response.setActive(user.getActive());
        response.setCreatedAt(user.getCreatedAt());

        if (user.getDosenPa() != null) {
            response.setDosenPaId(user.getDosenPa().getId());
            response.setNamaDosenPa(user.getDosenPa().getNama());
        }

        return response;
    }
}
