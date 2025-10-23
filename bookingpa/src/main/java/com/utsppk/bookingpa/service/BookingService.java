package com.utsppk.bookingpa.service;

import com.utsppk.bookingpa.dto.request.CreateBookingRequest;
import com.utsppk.bookingpa.exception.BadRequestException;
import com.utsppk.bookingpa.exception.ResourceNotFoundException;
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

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Booking createBooking(String username, CreateBookingRequest request) {
        User mahasiswa = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Jadwal tidak ditemukan"));

        // Check if slot is available
        if (schedule.getSlotTerisi() >= schedule.getMaxSlot()) {
            throw new BadRequestException("Slot sudah penuh");
        }

        if (!schedule.getAvailable()) {
            throw new BadRequestException("Jadwal tidak tersedia");
        }

        Booking booking = new Booking();
        booking.setMahasiswa(mahasiswa);
        booking.setSchedule(schedule);
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setKeterangan(request.getKeterangan());

        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings(String username) {
        User mahasiswa = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        return bookingRepository.findByMahasiswaId(mahasiswa.getId());
    }

    public List<Booking> getPendingBookings(String username) {
        User dosen = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Dosen tidak ditemukan"));

        return bookingRepository.findByScheduleDosenIdAndStatus(
                dosen.getId(),
                Booking.BookingStatus.PENDING
        );
    }

    @Transactional
    public Booking approveBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking tidak ditemukan"));

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BadRequestException("Booking sudah diproses");
        }

        booking.setStatus(Booking.BookingStatus.APPROVED);
        booking.setApprovedAt(LocalDateTime.now());

        // Update slot terisi
        Schedule schedule = booking.getSchedule();
        schedule.setSlotTerisi(schedule.getSlotTerisi() + 1);
        scheduleRepository.save(schedule);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking rejectBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking tidak ditemukan"));

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BadRequestException("Booking sudah diproses");
        }

        booking.setStatus(Booking.BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }
}
