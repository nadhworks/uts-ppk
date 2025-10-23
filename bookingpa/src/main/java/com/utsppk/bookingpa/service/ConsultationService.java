package com.utsppk.bookingpa.service;

import com.utsppk.bookingpa.dto.request.CatatanKonsultasiRequest;
import com.utsppk.bookingpa.exception.BadRequestException;
import com.utsppk.bookingpa.exception.ResourceNotFoundException;
import com.utsppk.bookingpa.model.Booking;
import com.utsppk.bookingpa.model.Consultation;
import com.utsppk.bookingpa.model.User;
import com.utsppk.bookingpa.repository.BookingRepository;
import com.utsppk.bookingpa.repository.ConsultationRepository;
import com.utsppk.bookingpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Consultation addConsultationNote(CatatanKonsultasiRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking tidak ditemukan"));

        if (booking.getStatus() != Booking.BookingStatus.APPROVED) {
            throw new BadRequestException("Hanya booking yang approved yang bisa diberi catatan");
        }

        Consultation consultation = new Consultation();
        consultation.setBooking(booking);
        consultation.setCatatan(request.getCatatan());
        consultation.setTanggalKonsultasi(LocalDateTime.now());

        // Update booking status to completed
        booking.setStatus(Booking.BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        return consultationRepository.save(consultation);
    }

    public List<Consultation> getMyConsultationHistory(String username) {
        User mahasiswa = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        return consultationRepository.findByBookingMahasiswaId(mahasiswa.getId());
    }

    public Consultation getConsultationByBooking(Long bookingId) {
        return consultationRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Hasil konsultasi tidak ditemukan"));
    }
}
