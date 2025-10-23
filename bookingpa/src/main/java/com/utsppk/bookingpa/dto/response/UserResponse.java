package com.utsppk.bookingpa.dto.response;

import com.utsppk.bookingpa.model.Role;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String nama;
    private String nimNip;
    private String noTelepon;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
}
