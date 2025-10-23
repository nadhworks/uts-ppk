package com.utsppk.bookingpa.controller;

import com.utsppk.bookingpa.dto.request.CreateBookingRequest;
import com.utsppk.bookingpa.dto.response.ApiResponse;
import com.utsppk.bookingpa.model.Booking;
import com.utsppk.bookingpa.service.BookingService;
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
@RequestMapping("/booking")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Booking Management", description = "Endpoint untuk manajemen permintaan bimbingan akademik")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('MAHASISWA')")
    @Operation(summary = "Buat permintaan konsultasi akademik (Mahasiswa)")
    public ResponseEntity<ApiResponse<Booking>> createBooking(
            Authentication authentication,
            @Valid @RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBooking(authentication.getName(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Permintaan konsultasi akademik berhasil dibuat", booking));
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('MAHASISWA')")
    @Operation(summary = "Lihat permintaan konsultasi akademik saya (Mahasiswa)")
    public ResponseEntity<ApiResponse<List<Booking>>> getMyBookings(Authentication authentication) {
        List<Booking> bookings = bookingService.getMyBookings(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Permintaan konsultasi ditemukan", bookings));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('DOSEN')")
    @Operation(summary = "Lihat permintaan konsultasi pending (Dosen)")
    public ResponseEntity<ApiResponse<List<Booking>>> getPendingBookings(Authentication authentication) {
        List<Booking> bookings = bookingService.getPendingBookings(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Permintaan konsultasi pending ditemukan", bookings));
    }

    @PatchMapping("/approve/{id}")
    @PreAuthorize("hasRole('DOSEN')")
    @Operation(summary = "Persetujuan permintaan konsultasi akademik (Dosen)")
    public ResponseEntity<ApiResponse<Booking>> approveBooking(@PathVariable Long id) {
        Booking booking = bookingService.approveBooking(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Permintaan konsultasi akademik berhasil disetujui", booking));
    }

    @PatchMapping("/reject/{id}")
    @PreAuthorize("hasRole('DOSEN')")
    @Operation(summary = "Tolak permintaan konsultasi akademik (Dosen)")
    public ResponseEntity<ApiResponse<Booking>> rejectBooking(@PathVariable Long id) {
        Booking booking = bookingService.rejectBooking(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Permintaan konsultasi akademik berhasil ditolak", booking));
    }
}