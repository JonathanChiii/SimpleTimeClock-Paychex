package com.example.simpletimeclock.repository;

import com.example.simpletimeclock.model.Break;
import com.example.simpletimeclock.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreakRepository extends JpaRepository<Break, Long> {
    Optional<Break> findById(Long id);
    List<Break> findAllByShift(Shift shift);
    Break save(Break break_);
}
