package com.utsppk.bookingpa.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Password lama wajib diisi")
    private String oldPassword;

    @NotBlank(message = "Password baru wajib diisi")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String newPassword;
}
