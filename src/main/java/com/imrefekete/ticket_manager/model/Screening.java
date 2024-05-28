package com.imrefekete.ticket_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "screenings")
public class Screening {
    @Id
    @Column(name = "screening_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "auditorium_id")
    private Auditorium auditorium;

    @Column(name = "screening_start")
    private LocalDateTime start;

    @Column(name = "screening_end")
    private LocalDateTime end;
}


