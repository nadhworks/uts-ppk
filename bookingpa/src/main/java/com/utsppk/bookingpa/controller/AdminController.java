package com.utsppk.bookingpa.controller;

import com.utsppk.bookingpa.dto.response.ApiResponse;
import com.utsppk.bookingpa.dto.response.UserResponse;
import com.utsppk.bookingpa.exception.BadRequestException;
import com.utsppk.bookingpa.exception.ResourceNotFoundException;
import com.utsppk.bookingpa.model.Role;
import com.utsppk.bookingpa.model.User;
import com.utsppk.bookingpa.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
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
        // periksa apakah user ada
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pengguna dengan ID " + id + " tidak ditemukan");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Pengguna berhasil dihapus"));
    }

    @Transactional
    @PatchMapping("/assign-pa/{mahasiswaId}/{dosenId}")
    @Operation(summary = "Tetapkan Dosen PA ke Mahasiswa (Admin)")
    public ResponseEntity<ApiResponse<UserResponse>> assignPaToMahasiswa(
            @PathVariable Long mahasiswaId,
            @PathVariable Long dosenId) {

        User mahasiswa = userRepository.findById(mahasiswaId)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa dengan ID " + mahasiswaId + " tidak ditemukan"));

        User dosen = userRepository.findById(dosenId)
                .orElseThrow(() -> new ResourceNotFoundException("Dosen dengan ID " + dosenId + " tidak ditemukan"));

        // Validasi peran
        if (mahasiswa.getRole() != Role.MAHASISWA) {
            throw new BadRequestException("User yang dipilih (ID: " + mahasiswaId + ") bukan MAHASISWA");
        }
        if (dosen.getRole() != Role.DOSEN) {
            throw new BadRequestException("User yang dipilih untuk PA (ID: " + dosenId + ") bukan DOSEN");
        }

        // Tetapkan Dosen PA
        mahasiswa.setDosenPa(dosen);
        User updatedMahasiswa = userRepository.save(mahasiswa);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Dosen PA " + dosen.getNama() + " berhasil ditetapkan ke " + mahasiswa.getNama(),
                mapToUserResponse(updatedMahasiswa)
        ));
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