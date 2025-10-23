package com.utsppk.bookingpa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dosen_id", nullable = false)
    private User dosen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek hari;

    @Column(nullable = false)
    private LocalTime jamMulai;

    @Column(nullable = false)
    private LocalTime jamSelesai;

    @Column(nullable = false)
    private Integer maxSlot; // Maksimal slot konsultasi mahasiswa per hari

    @Column(nullable = false)
    private Integer slotTerisi = 0;

    @Column(nullable = false)
    private Boolean available = true;
}