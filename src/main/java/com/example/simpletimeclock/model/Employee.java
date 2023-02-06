package com.example.simpletimeclock.model;

import com.example.simpletimeclock.model.utility.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column
    @NotEmpty
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "employee")
    private Set<Shift> shifts = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status isOnShift;

    private Double totalHour;
}
