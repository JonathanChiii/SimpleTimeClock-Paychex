package com.example.simpletimeclock.repository;

import com.example.simpletimeclock.model.Employee;
import com.example.simpletimeclock.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Optional<Shift> findById(Long aLong);
    List<Shift> findShiftsByEmployee(Employee employee);
    Shift save(Shift shift);
}
