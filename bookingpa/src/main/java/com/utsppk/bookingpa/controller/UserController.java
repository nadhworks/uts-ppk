package com.utsppk.bookingpa.controller;

import com.utsppk.bookingpa.dto.request.UpdateProfileRequest;
import com.utsppk.bookingpa.dto.response.ApiResponse;
import com.utsppk.bookingpa.dto.response.UserResponse;
import com.utsppk.bookingpa.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Management", description = "Endpoint untuk manajemen profil pengguna")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Lihat profil pengguna")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication) {
        UserResponse user = userService.getProfile(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Profil ditemukan", user));
    }

    @PatchMapping("/profile/update")
    @Operation(summary = "Update profil pengguna")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse user = userService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profil berhasil diupdate", user));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Hapus akun pengguna")
    public ResponseEntity<ApiResponse<?>> deleteAccount(Authentication authentication) {
        userService.deleteAccount(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Akun berhasil dihapus"));
    }
}