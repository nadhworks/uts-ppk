package com.utsppk.bookingpa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.utsppk.bookingpa.model.Role;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}

