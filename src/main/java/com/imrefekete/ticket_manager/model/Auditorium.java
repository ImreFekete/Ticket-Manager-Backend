package com.imrefekete.ticket_manager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auditoriums")
public class Auditorium {
    @Id
    @Column(name = "auditorium_id")
    private int id;

    private String name;

    @Column(name = "total_rows")
    private int rows;

    @Column(name = "seats_per_row")
    private int seatsPerRows;
}
