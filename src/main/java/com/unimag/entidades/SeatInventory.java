package com.unimag.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
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

    @ManyToOne(targetEntity = Flight.class)
    @JoinColumn(name = "flight_id")
    private Flight flight;
}
