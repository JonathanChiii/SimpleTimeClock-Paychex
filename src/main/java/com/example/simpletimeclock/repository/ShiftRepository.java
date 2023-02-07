package com.example.simpletimeclock.repository;

import com.example.simpletimeclock.model.Employee;
import com.example.simpletimeclock.model.Shift;
import com.example.simpletimeclock.model.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findShiftsByEmployee(Employee employee);
    Optional<Shift> findShiftByEmployeeAndIsActive(Employee employee, Status status);
    @Query(value = "SELECT SUM(total_hours) from break WHERE shift_id = :id", nativeQuery = true)
    Double getTotalBreakHours(@Param("id") Long shift_id);
}
