package com.example.simpletimeclock.repository;

import com.example.simpletimeclock.model.Employee;
import com.example.simpletimeclock.model.Shift;
import com.example.simpletimeclock.model.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Optional<Shift> findById(Long aLong);
    List<Shift> findShiftsByEmployee(Employee employee);
    Optional<Shift> findShiftByEmployeeAndIsActive(Employee employee, Status status);
    Shift save(Shift shift);
}
