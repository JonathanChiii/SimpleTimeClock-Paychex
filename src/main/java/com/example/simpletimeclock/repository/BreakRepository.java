package com.example.simpletimeclock.repository;

import com.example.simpletimeclock.model.Break;
import com.example.simpletimeclock.model.Shift;
import com.example.simpletimeclock.model.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreakRepository extends JpaRepository<Break, Long> {
    List<Break> findAllByShift(Shift shift);
    Optional<Break> findBreakByShiftAndIsActive(Shift shift, Status status);
}
