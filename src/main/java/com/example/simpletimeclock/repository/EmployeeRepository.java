package com.example.simpletimeclock.repository;

import com.example.simpletimeclock.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    //Optional<Employee> findById(Long id);
    Optional<Employee> findByUsername (String username);
    //Employee save(Employee employee);
}