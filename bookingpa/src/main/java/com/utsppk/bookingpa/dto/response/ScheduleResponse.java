package com.utsppk.bookingpa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    private Long id;
    private Long dosenId;
    private String namaDosen;
    private String emailDosen;
    private DayOfWeek hari;
    private String hariIndonesia; // Senin, Selasa, etc
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private String waktuKonsultasi; // Format: "10:00 - 12:00"
    private Integer maxSlot;
    private Integer slotTerisi;
    private Integer sisaSlot;
    private Boolean available;

    // Helper method untuk format waktu
    public void setWaktuFromTimes(LocalTime jamMulai, LocalTime jamSelesai) {
        this.waktuKonsultasi = jamMulai.toString() + " - " + jamSelesai.toString();
    }

    // Helper method untuk translate hari ke Indonesia
    public void setHariIndonesia(DayOfWeek hari) {
        switch (hari) {
            case MONDAY:
                this.hariIndonesia = "Senin";
                break;
            case TUESDAY:
                this.hariIndonesia = "Selasa";
                break;
            case WEDNESDAY:
                this.hariIndonesia = "Rabu";
                break;
            case THURSDAY:
                this.hariIndonesia = "Kamis";
                break;
            case FRIDAY:
                this.hariIndonesia = "Jumat";
                break;
            case SATURDAY:
                this.hariIndonesia = "Sabtu";
                break;
            case SUNDAY:
                this.hariIndonesia = "Minggu";
                break;
            default:
                this.hariIndonesia = hari.toString();
        }
    }

    // Calculate sisa slot
    public void calculateSisaSlot() {
        this.sisaSlot = this.maxSlot - this.slotTerisi;
    }
}
