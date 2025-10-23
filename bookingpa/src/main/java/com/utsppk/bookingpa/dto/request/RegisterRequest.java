package com.utsppk.bookingpa.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username wajib diisi")
    @Size(min = 4, max = 50, message = "Username harus 4-50 karakter")
    private String username;

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Nama wajib diisi")
    private String nama;

    @NotBlank(message = "NIM/NIP wajib diisi")
    private String nimNip;

    private String noTelepon;

    @NotBlank(message = "Role wajib diisi")
    private String role; // MAHASISWA, DOSEN, ADMIN
}
