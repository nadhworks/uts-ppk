package com.utsppk.bookingpa.service;

import com.utsppk.bookingpa.dto.request.CreateScheduleRequest;
import com.utsppk.bookingpa.exception.BadRequestException;
import com.utsppk.bookingpa.exception.ResourceNotFoundException;
import com.utsppk.bookingpa.model.Schedule;
import com.utsppk.bookingpa.model.User;
import com.utsppk.bookingpa.repository.ScheduleRepository;
import com.utsppk.bookingpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Schedule createSchedule(String username, CreateScheduleRequest request) {
        User dosen = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Dosen tidak ditemukan"));

        Schedule schedule = new Schedule();
        schedule.setDosen(dosen);
        schedule.setHari(DayOfWeek.valueOf(request.getHari().toUpperCase()));
        schedule.setJamMulai(LocalTime.parse(request.getJamMulai()));
        schedule.setJamSelesai(LocalTime.parse(request.getJamSelesai()));
        schedule.setMaxSlot(request.getMaxSlot());
        schedule.setSlotTerisi(0);
        schedule.setAvailable(true);

        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getAvailableSchedules(String username) {
        User mahasiswa = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        // Asumsi: mahasiswa melihat jadwal dosen PA-nya
        // Untuk simplifikasi, tampilkan semua jadwal available
        return scheduleRepository.findByDosenIdAndAvailableTrue(mahasiswa.getId());
    }

    public List<Schedule> getMySchedules(String username) {
        User dosen = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Dosen tidak ditemukan"));

        return scheduleRepository.findByDosenId(dosen.getId());
    }

    @Transactional
    public Schedule updateSchedule(Long id, CreateScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jadwal tidak ditemukan"));

        schedule.setHari(DayOfWeek.valueOf(request.getHari().toUpperCase()));
        schedule.setJamMulai(LocalTime.parse(request.getJamMulai()));
        schedule.setJamSelesai(LocalTime.parse(request.getJamSelesai()));
        schedule.setMaxSlot(request.getMaxSlot());

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jadwal tidak ditemukan"));

        scheduleRepository.delete(schedule);
    }
}
