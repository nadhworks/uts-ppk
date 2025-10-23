package com.utsppk.bookingpa.repository;

import com.utsppk.bookingpa.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDosenIdAndAvailableTrue(Long dosenId);
    List<Schedule> findByDosenId(Long dosenId);
    List<Schedule> findByHari(DayOfWeek hari);
}
