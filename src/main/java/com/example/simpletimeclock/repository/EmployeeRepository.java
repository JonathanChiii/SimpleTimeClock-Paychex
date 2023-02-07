package com.example.simpletimeclock.repository;

import com.example.simpletimeclock.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUsername (String username);
    @Query(value = "SELECT SUM(total_hours) from shift WHERE employee_id = :id", nativeQuery = true)
    Double getTotalShiftHours(@Param("id") Long employee_id);
}
