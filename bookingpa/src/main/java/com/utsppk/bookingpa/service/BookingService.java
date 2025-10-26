package com.utsppk.bookingpa.service;

import com.utsppk.bookingpa.dto.request.CreateBookingRequest;
import com.utsppk.bookingpa.dto.response.BookingResponse;
import com.utsppk.bookingpa.exception.BadRequestException;
import com.utsppk.bookingpa.exception.ResourceNotFoundException;
import com.utsppk.bookingpa.exception.UnauthorizedException;
import com.utsppk.bookingpa.model.Booking;
import com.utsppk.bookingpa.model.Schedule;
import com.utsppk.bookingpa.model.User;
import com.utsppk.bookingpa.repository.BookingRepository;
import com.utsppk.bookingpa.repository.ScheduleRepository;
import com.utsppk.bookingpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BookingResponse createBooking(String username, CreateBookingRequest request) {
        User mahasiswa = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Jadwal tidak ditemukan"));

        if (schedule.getSlotTerisi() >= schedule.getMaxSlot()) {
            throw new BadRequestException("Slot sudah penuh");
        }
        if (!schedule.getAvailable()) {
            throw new BadRequestException("Jadwal tidak tersedia");
        }
        if (mahasiswa.getDosenPa() != null && !schedule.getDosen().getId().equals(mahasiswa.getDosenPa().getId())) {
            throw new BadRequestException("Anda hanya bisa booking jadwal Dosen PA Anda.");
        }

        Booking booking = new Booking();
        booking.setMahasiswa(mahasiswa);
        booking.setSchedule(schedule);
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setKeterangan(request.getKeterangan());

        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String username) {
        User mahasiswa = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        List<Booking> bookings = bookingRepository.findByMahasiswaId(mahasiswa.getId());
        return bookings.stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getPendingBookings(String username) {
        User dosen = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Dosen tidak ditemukan"));

        List<Booking> bookings = bookingRepository.findByScheduleDosenIdAndStatus(
                dosen.getId(),
                Booking.BookingStatus.PENDING
        );
        return bookings.stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse approveBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking tidak ditemukan"));

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BadRequestException("Booking sudah diproses");
        }

        booking.setStatus(Booking.BookingStatus.APPROVED);
        booking.setApprovedAt(LocalDateTime.now());

        Schedule schedule = booking.getSchedule();
        schedule.setSlotTerisi(schedule.getSlotTerisi() + 1);
        scheduleRepository.save(schedule);

        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingResponse(savedBooking);
    }

    @Transactional
    public BookingResponse rejectBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking tidak ditemukan"));

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BadRequestException("Booking sudah diproses");
        }

        booking.setStatus(Booking.BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingResponse(savedBooking);
    }

    @Transactional
    public void deletePendingBooking(String username, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking tidak ditemukan"));

        if (!booking.getMahasiswa().getUsername().equals(username)) {
            throw new UnauthorizedException("Anda tidak memiliki izin untuk menghapus booking ini");
        }
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BadRequestException("Hanya permintaan yang PENDING yang bisa dihapus. Status saat ini: " + booking.getStatus());
        }

        bookingRepository.delete(booking);
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        BookingResponse dto = new BookingResponse();
        dto.setId(booking.getId());

        if (booking.getMahasiswa() != null) {
            dto.setMahasiswaId(booking.getMahasiswa().getId());
            dto.setNamaMahasiswa(booking.getMahasiswa().getNama());
            dto.setNimMahasiswa(booking.getMahasiswa().getNimNip());
            dto.setEmailMahasiswa(booking.getMahasiswa().getEmail());
        }

        if (booking.getSchedule() != null) {
            dto.setScheduleId(booking.getSchedule().getId());
            dto.setHari(booking.getSchedule().getHari());
            dto.setHariIndonesia(booking.getSchedule().getHari());
            dto.setJamMulai(booking.getSchedule().getJamMulai());
            dto.setJamSelesai(booking.getSchedule().getJamSelesai());
            dto.setWaktuKonsultasi(booking.getSchedule().getJamMulai(), booking.getSchedule().getJamSelesai());

            if (booking.getSchedule().getDosen() != null) {
                dto.setDosenId(booking.getSchedule().getDosen().getId());
                dto.setNamaDosen(booking.getSchedule().getDosen().getNama());
            }
        }

        dto.setStatus(booking.getStatus());
        dto.setStatusIndonesia(booking.getStatus());
        dto.setKeterangan(booking.getKeterangan());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setApprovedAt(booking.getApprovedAt());

        return dto;
    }
}
