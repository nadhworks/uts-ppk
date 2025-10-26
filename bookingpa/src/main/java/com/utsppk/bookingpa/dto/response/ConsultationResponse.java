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
public class ConsultationResponse {
    private Long id;
    private String catatan;
    private LocalDateTime tanggalKonsultasi;
    private LocalDateTime createdAt;
    private Long bookingId;
    private Booking.BookingStatus bookingStatus;
    private String bookingStatusIndonesia;
    private Long mahasiswaId;
    private String namaMahasiswa;
    private String nimMahasiswa;
    private Long dosenId;
    private String namaDosen;
    private String nipDosen;
    private DayOfWeek hari;
    private String hariIndonesia;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private String waktuKonsultasi;

    public void setBookingStatusIndonesia(Booking.BookingStatus status) {
        if (status == null) return;
        switch (status) {
            case PENDING:
                this.bookingStatusIndonesia = "Menunggu Persetujuan";
                break;
            case APPROVED:
                this.bookingStatusIndonesia = "Disetujui";
                break;
            case REJECTED:
                this.bookingStatusIndonesia = "Ditolak";
                break;
            case COMPLETED:
                this.bookingStatusIndonesia = "Selesai";
                break;
            default:
                this.bookingStatusIndonesia = status.toString();
        }
    }

    public void setHariIndonesia(DayOfWeek hari) {
        if (hari == null) return;
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

    public void setWaktuKonsultasi(LocalTime jamMulai, LocalTime jamSelesai) {
        if (jamMulai == null || jamSelesai == null) return;
        this.waktuKonsultasi = jamMulai.toString() + " - " + jamSelesai.toString();
    }
}