package com.utsppk.bookingpa.service;

import com.utsppk.bookingpa.dto.request.CatatanKonsultasiRequest;
import com.utsppk.bookingpa.dto.response.ConsultationResponse;
import com.utsppk.bookingpa.exception.BadRequestException;
import com.utsppk.bookingpa.exception.ResourceNotFoundException;
import com.utsppk.bookingpa.model.Booking;
import com.utsppk.bookingpa.model.Consultation;
import com.utsppk.bookingpa.model.Schedule;
import com.utsppk.bookingpa.model.User;
import com.utsppk.bookingpa.repository.BookingRepository;
import com.utsppk.bookingpa.repository.ConsultationRepository;
import com.utsppk.bookingpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ConsultationResponse addConsultationNote(CatatanKonsultasiRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking tidak ditemukan"));

        if (booking.getStatus() != Booking.BookingStatus.APPROVED) {
            throw new BadRequestException("Hanya booking yang approved yang bisa diberi catatan");
        }

        Consultation consultation = new Consultation();
        consultation.setBooking(booking);
        consultation.setCatatan(request.getCatatan());
        consultation.setTanggalKonsultasi(LocalDateTime.now());

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        Consultation savedConsultation = consultationRepository.save(consultation);
        return mapToConsultationResponse(savedConsultation);
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponse> getMyConsultationHistory(String username) {
        User mahasiswa = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        List<Consultation> consultations = consultationRepository.findByBookingMahasiswaId(mahasiswa.getId());
        return consultations.stream()
                .map(this::mapToConsultationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConsultationResponse getConsultationByBooking(Long bookingId) {
        Consultation consultation = consultationRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Hasil konsultasi tidak ditemukan"));
        return mapToConsultationResponse(consultation);
    }

    private ConsultationResponse mapToConsultationResponse(Consultation consultation) {
        ConsultationResponse dto = new ConsultationResponse();
        dto.setId(consultation.getId());
        dto.setCatatan(consultation.getCatatan());
        dto.setTanggalKonsultasi(consultation.getTanggalKonsultasi());
        dto.setCreatedAt(consultation.getCreatedAt());

        Booking booking = consultation.getBooking();
        if (booking != null) {
            dto.setBookingId(booking.getId());
            dto.setBookingStatus(booking.getStatus());
            dto.setBookingStatusIndonesia(booking.getStatus());

            User mahasiswa = booking.getMahasiswa();
            if (mahasiswa != null) {
                dto.setMahasiswaId(mahasiswa.getId());
                dto.setNamaMahasiswa(mahasiswa.getNama());
                dto.setNimMahasiswa(mahasiswa.getNimNip());
            }

            Schedule schedule = booking.getSchedule();
            if (schedule != null) {
                dto.setHari(schedule.getHari());
                dto.setHariIndonesia(schedule.getHari());
                dto.setJamMulai(schedule.getJamMulai());
                dto.setJamSelesai(schedule.getJamSelesai());
                dto.setWaktuKonsultasi(schedule.getJamMulai(), schedule.getJamSelesai());

                User dosen = schedule.getDosen();
                if (dosen != null) {
                    dto.setDosenId(dosen.getId());
                    dto.setNamaDosen(dosen.getNama());
                    dto.setNipDosen(dosen.getNimNip());
                }
            }
        }
        return dto;
    }
}