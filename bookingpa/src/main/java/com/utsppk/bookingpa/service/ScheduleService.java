package com.utsppk.bookingpa.service;

import com.utsppk.bookingpa.dto.request.CreateScheduleRequest;
import com.utsppk.bookingpa.dto.response.ScheduleResponse; // Import DTO
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
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ScheduleResponse createSchedule(String username, CreateScheduleRequest request) { // Return DTO
        User dosen = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Dosen tidak ditemukan"));

        Schedule schedule = new Schedule();
        schedule.setDosen(dosen);
        schedule.setHari(DayOfWeek.valueOf(request.getHari().toUpperCase()));
        schedule.setJamMulai(LocalTime.parse(request.getJamMulai()));
        schedule.setJamSelesai(LocalTime.parse(request.getJamSelesai()));
        schedule.setMaxSlot(request.getMaxSlot());
        schedule.setSlotTerisi(0); // Inisialisasi slot terisi
        schedule.setAvailable(true); // Inisialisasi available

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return mapToScheduleResponse(savedSchedule); // Map ke DTO sebelum return
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAvailableSchedules(String username) { // Return List<DTO>
        User mahasiswa = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        //Ambil Dosen PA dari mahasiswa
        User dosenPa = mahasiswa.getDosenPa();

        //Validasi jika mahasiswa sudah punya PA
        if (dosenPa == null) {
            throw new BadRequestException("Anda belum memiliki Dosen PA. Hubungi Admin.");
        }

        //Cari jadwal berdasarkan ID Dosen PA, bukan ID mahasiswa
        List<Schedule> schedules = scheduleRepository.findByDosenIdAndAvailableTrue(dosenPa.getId());

        //Mapping dari List<Schedule> ke List<ScheduleResponse>
        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getMySchedules(String username) {
        User dosen = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Dosen tidak ditemukan"));

        List<Schedule> schedules = scheduleRepository.findByDosenId(dosen.getId());
        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long id, CreateScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jadwal tidak ditemukan"));

        schedule.setHari(DayOfWeek.valueOf(request.getHari().toUpperCase()));
        schedule.setJamMulai(LocalTime.parse(request.getJamMulai()));
        schedule.setJamSelesai(LocalTime.parse(request.getJamSelesai()));
        schedule.setMaxSlot(request.getMaxSlot());
        // Slot terisi tidak direset saat update

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return mapToScheduleResponse(updatedSchedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jadwal tidak ditemukan"));

        scheduleRepository.delete(schedule);
    }

    private ScheduleResponse mapToScheduleResponse(Schedule schedule) {
        ScheduleResponse dto = new ScheduleResponse();
        dto.setId(schedule.getId());
        if (schedule.getDosen() != null) {
            dto.setDosenId(schedule.getDosen().getId());
            dto.setNamaDosen(schedule.getDosen().getNama());
            dto.setEmailDosen(schedule.getDosen().getEmail());
        }
        dto.setHari(schedule.getHari());
        dto.setHariIndonesia(schedule.getHari());
        dto.setJamMulai(schedule.getJamMulai());
        dto.setJamSelesai(schedule.getJamSelesai());
        dto.setWaktuFromTimes(schedule.getJamMulai(), schedule.getJamSelesai());
        dto.setMaxSlot(schedule.getMaxSlot());
        dto.setSlotTerisi(schedule.getSlotTerisi());
        dto.calculateSisaSlot();
        dto.setAvailable(schedule.getAvailable());
        return dto;
    }
}