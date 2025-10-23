package com.utsppk.bookingpa.repository;

import com.utsppk.bookingpa.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByNimNip(String nimNip);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByNimNip(String nimNip);
    List<User> findByRole(Role role);
}