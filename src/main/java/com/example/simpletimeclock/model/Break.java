package com.example.simpletimeclock.model;

import com.example.simpletimeclock.model.utility.BreakType;
import com.example.simpletimeclock.model.utility.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "break")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Break {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BreakType type = BreakType.Break;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status isActive = Status.Inactive;

    @Column
    private Date start;
    @Column
    private Date end;
    @Column
    private Double totalHour = Double.valueOf(0);
}
