package com.example.simpletimeclock.model;

import com.example.simpletimeclock.model.utility.BreakType;
import com.example.simpletimeclock.model.utility.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "break")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Break {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BreakType type;
}
