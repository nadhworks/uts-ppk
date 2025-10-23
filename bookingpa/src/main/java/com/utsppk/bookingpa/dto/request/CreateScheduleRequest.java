package com.utsppk.bookingpa.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateScheduleRequest {
    @NotBlank(message = "Hari wajib diisi")
    private String hari; // Senin, Selasa, dst.

    @NotBlank(message = "Jam mulai wajib diisi")
    private String jamMulai; // Format: HH:mm

    @NotBlank(message = "Jam selesai wajib diisi")
    private String jamSelesai;

    @NotNull(message = "slot konsultasi wajib diisi")
    @Min(value = 1, message = "Max slot minimal 1")
    private Integer maxSlot;
}