package com.utsppk.bookingpa.controller;

import com.utsppk.bookingpa.dto.request.CreateBookingRequest;
import com.utsppk.bookingpa.dto.response.ApiResponse;
import com.utsppk.bookingpa.dto.response.BookingResponse;
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
    @Operation(summary = "Membuat permintaan konsultasi akademik (Mahasiswa)")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            Authentication authentication,
            @Valid @RequestBody CreateBookingRequest request) {
        BookingResponse booking = bookingService.createBooking(authentication.getName(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Permintaan konsultasi akademik berhasil dibuat", booking));
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('MAHASISWA')")
    @Operation(summary = "Melihat permintaan konsultasi akademik saya (Mahasiswa)")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(Authentication authentication) {
        List<BookingResponse> bookings = bookingService.getMyBookings(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Permintaan konsultasi ditemukan", bookings));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('DOSEN')")
    @Operation(summary = "Melihat permintaan konsultasi pending (Dosen)")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getPendingBookings(Authentication authentication) {
        List<BookingResponse> bookings = bookingService.getPendingBookings(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Permintaan konsultasi pending ditemukan", bookings));
    }

    @PatchMapping("/approve/{id}")
    @PreAuthorize("hasRole('DOSEN')")
    @Operation(summary = "Menyetujui permintaan konsultasi akademik (Dosen)")
    public ResponseEntity<ApiResponse<BookingResponse>> approveBooking(@PathVariable Long id) {
        BookingResponse booking = bookingService.approveBooking(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Permintaan konsultasi akademik berhasil disetujui", booking));
    }

    @PatchMapping("/reject/{id}")
    @PreAuthorize("hasRole('DOSEN')")
    @Operation(summary = "Menolak permintaan konsultasi akademik (Dosen)")
    public ResponseEntity<ApiResponse<BookingResponse>> rejectBooking(@PathVariable Long id) {
        BookingResponse booking = bookingService.rejectBooking(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Permintaan konsultasi akademik berhasil ditolak", booking));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('MAHASISWA')")
    @Operation(summary = "Menghapus/Membatalkan permintaan konsultasi yang PENDING (Mahasiswa)")
    public ResponseEntity<ApiResponse<?>> deletePendingBooking(
            Authentication authentication,
            @PathVariable Long id) {
        bookingService.deletePendingBooking(authentication.getName(), id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Permintaan konsultasi berhasil dihapus"));
    }
}
