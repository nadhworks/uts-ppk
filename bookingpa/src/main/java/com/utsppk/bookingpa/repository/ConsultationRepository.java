package com.utsppk.bookingpa.repository;

import com.utsppk.bookingpa.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByBookingId(Long bookingId);
    List<Consultation> findByBookingMahasiswaId(Long mahasiswaId);
}
