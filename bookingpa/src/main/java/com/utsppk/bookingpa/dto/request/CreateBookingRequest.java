package com.utsppk.bookingpa.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookingRequest {
    @NotNull(message = "Schedule ID wajib diisi")
    private Long scheduleId;

    private String keterangan;
}