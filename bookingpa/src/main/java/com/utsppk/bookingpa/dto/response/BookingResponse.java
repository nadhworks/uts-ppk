package com.utsppk.bookingpa.dto.response;

import com.utsppk.bookingpa.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;

    // Mahasiswa Info
    private Long mahasiswaId;
    private String namaMahasiswa;
    private String nimMahasiswa;
    private String emailMahasiswa;

    // Schedule Info
    private Long scheduleId;
    private Long dosenId;
    private String namaDosen;
    private DayOfWeek hari;
    private String hariIndonesia;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private String waktuKonsultasi; // Format: "10:00 - 12:00"

    // Booking Info
    private Booking.BookingStatus status;
    private String statusIndonesia;
    private String keterangan;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    // Helper method untuk translate status ke Indonesia
    public void setStatusIndonesia(Booking.BookingStatus status) {
        switch (status) {
            case PENDING:
                this.statusIndonesia = "Menunggu Persetujuan";
                break;
            case APPROVED:
                this.statusIndonesia = "Disetujui";
                break;
            case REJECTED:
                this.statusIndonesia = "Ditolak";
                break;
            case COMPLETED:
                this.statusIndonesia = "Selesai";
                break;
            default:
                this.statusIndonesia = status.toString();
        }
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

    // Helper method untuk format waktu
    public void setWaktuKonsultasi(LocalTime jamMulai, LocalTime jamSelesai) {
        this.waktuKonsultasi = jamMulai.toString() + " - " + jamSelesai.toString();
    }
}
