package com.utsppk.bookingpa.controller;

import com.utsppk.bookingpa.dto.response.ApiResponse;
import com.utsppk.bookingpa.dto.response.UserResponse;
import com.utsppk.bookingpa.model.Role;
import com.utsppk.bookingpa.model.User;
import com.utsppk.bookingpa.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin Management", description = "Endpoint untuk admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    @Operation(summary = "Lihat semua pengguna (Admin)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Daftar pengguna", userResponses));
    }

    @GetMapping("/users/role/{role}")
    @Operation(summary = "Lihat pengguna berdasarkan role (Admin)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable String role) {
        List<User> users = userRepository.findByRole(Role.valueOf(role.toUpperCase()));
        List<UserResponse> userResponses = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Daftar pengguna role " + role, userResponses));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Hapus pengguna (Admin)")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Pengguna berhasil dihapus"));
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
        return response;
    }
}
