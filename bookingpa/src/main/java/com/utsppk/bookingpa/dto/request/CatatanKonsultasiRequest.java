package com.utsppk.bookingpa.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CatatanKonsultasiRequest {
    @NotNull(message = "Booking ID wajib diisi")
    private Long bookingId;

    @NotBlank(message = "Catatan wajib diisi")
    private String catatan;
}
