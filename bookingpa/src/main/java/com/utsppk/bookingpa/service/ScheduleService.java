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

        // 1. Ambil Dosen PA dari mahasiswa
        User dosenPa = mahasiswa.getDosenPa();

        // 2. Validasi jika mahasiswa sudah punya PA
        if (dosenPa == null) {
            throw new BadRequestException("Anda belum memiliki Dosen PA. Hubungi Admin.");
        }

        // 3. Cari jadwal berdasarkan ID Dosen PA, bukan ID mahasiswa
        return scheduleRepository.findByDosenIdAndAvailableTrue(dosenPa.getId());
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
