package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Table(name = "seat_invetories")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class SeatInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private Cabin cabin;

    private Integer totalSeats;
    private Integer availableSeats;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @Column(nullable = false)
    private Cabin cabin;
}
