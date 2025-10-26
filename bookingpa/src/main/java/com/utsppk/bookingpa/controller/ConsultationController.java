package com.utsppk.bookingpa.controller;

import com.utsppk.bookingpa.dto.request.CatatanKonsultasiRequest;
import com.utsppk.bookingpa.dto.response.ApiResponse;
import com.utsppk.bookingpa.dto.response.ConsultationResponse;
import com.utsppk.bookingpa.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultation")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Consultation Management", description = "Endpoint untuk manajemen hasil konsultasi akademik")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @PostMapping("/note")
    @PreAuthorize("hasRole('DOSEN')")
    @Operation(summary = "Menambah catatan hasil konsultasi (Dosen)")
    public ResponseEntity<ApiResponse<ConsultationResponse>> addConsultationNote(
            @Valid @RequestBody CatatanKonsultasiRequest request) {
        ConsultationResponse consultation = consultationService.addConsultationNote(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Catatan hasil konsultasi berhasil ditambahkan", consultation));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('MAHASISWA')")
    @Operation(summary = "Melihat riwayat konsultasi (Mahasiswa)")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> getMyConsultationHistory(
            Authentication authentication) {
        List<ConsultationResponse> consultations = consultationService.getMyConsultationHistory(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Riwayat konsultasi ditemukan", consultations));
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Melihat hasil konsultasi berdasarkan booking")
    public ResponseEntity<ApiResponse<ConsultationResponse>> getConsultationByBooking(@PathVariable Long bookingId) {
        ConsultationResponse consultation = consultationService.getConsultationByBooking(bookingId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Hasil konsultasi ditemukan", consultation));
    }
}