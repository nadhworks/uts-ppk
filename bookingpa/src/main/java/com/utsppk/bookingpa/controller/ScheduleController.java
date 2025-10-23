package com.utsppk.bookingpa.controller;

import com.utsppk.bookingpa.dto.request.CreateScheduleRequest;
import com.utsppk.bookingpa.dto.response.ApiResponse;
import com.utsppk.bookingpa.model.Schedule;
import com.utsppk.bookingpa.service.ScheduleService;
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
@RequestMapping("/schedule")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Schedule Management", description = "Endpoint untuk manajemen jadwal konsultasi")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('DOSEN')")
    @Operation(summary = "Buat jadwal konsultasi (Dosen)")
    public ResponseEntity<ApiResponse<Schedule>> createSchedule(
            Authentication authentication,
            @Valid @RequestBody CreateScheduleRequest request) {
        Schedule schedule = scheduleService.createSchedule(authentication.getName(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Jadwal berhasil dibuat", schedule));
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('MAHASISWA')")
    @Operation(summary = "Lihat jadwal tersedia (Mahasiswa)")
    public ResponseEntity<ApiResponse<List<Schedule>>> getAvailableSchedules(Authentication authentication) {
        List<Schedule> schedules = scheduleService.getAvailableSchedules(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Jadwal tersedia", schedules));
    }

    @GetMapping("/my-schedules")
    @PreAuthorize("hasRole('DOSEN')")
    @Operation(summary = "Lihat jadwal saya (Dosen)")
    public ResponseEntity<ApiResponse<List<Schedule>>> getMySchedules(Authentication authentication) {
        List<Schedule> schedules = scheduleService.getMySchedules(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Jadwal ditemukan", schedules));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('DOSEN')")
    @Operation(summary = "Update jadwal (Dosen)")
    public ResponseEntity<ApiResponse<Schedule>> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody CreateScheduleRequest request) {
        Schedule schedule = scheduleService.updateSchedule(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Jadwal berhasil diupdate", schedule));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('DOSEN')")
    @Operation(summary = "Hapus jadwal (Dosen)")
    public ResponseEntity<ApiResponse<?>> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Jadwal berhasil dihapus"));
    }
}
