package com.example.simpletimeclock.model;

import com.example.simpletimeclock.model.utility.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "shift")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //@Transactional(IsolationLevel.TRANSACTION_REPEATABLE_READ)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date clock_in;

    private Date clock_out;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shift")
    private Set<Break> breaks = new HashSet<>();
}
