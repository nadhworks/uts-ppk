package com.utsppk.bookingpa.controller;

import com.utsppk.bookingpa.dto.request.ChangePasswordRequest;
import com.utsppk.bookingpa.dto.request.LoginRequest;
import com.utsppk.bookingpa.dto.request.RegisterRequest;
import com.utsppk.bookingpa.dto.response.ApiResponse;
import com.utsppk.bookingpa.dto.response.LoginResponse;
import com.utsppk.bookingpa.dto.response.UserResponse;
import com.utsppk.bookingpa.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoint untuk autentikasi pengguna")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrasi pengguna baru")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Registrasi berhasil", user));
    }

    @PostMapping("/login")
    @Operation(summary = "Login pengguna")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login berhasil", response));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Ganti password", description = "Endpoint untuk mengganti password (harus login)")
    public ResponseEntity<ApiResponse<?>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password berhasil diubah"));
    }
}
