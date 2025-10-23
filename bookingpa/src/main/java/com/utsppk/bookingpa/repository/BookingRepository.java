package com.utsppk.bookingpa.repository;

import com.utsppk.bookingpa.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByMahasiswaId(Long mahasiswaId);
    List<Booking> findByScheduleDosenId(Long dosenId);
    List<Booking> findByScheduleDosenIdAndStatus(Long dosenId, Booking.BookingStatus status);
    List<Booking> findByStatus(Booking.BookingStatus status);
}
