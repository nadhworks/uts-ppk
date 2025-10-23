package com.utsppk.bookingpa.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String nama;
    private String email;
    private String noTelepon;
}
