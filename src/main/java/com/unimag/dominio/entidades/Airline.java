package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Setter
@Getter
@Table(name = "airlines")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Airline {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "airline_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 2)
    private String code;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "airline", fetch = FetchType.LAZY)
    private List<Flight> flights;

    public void addFlight(Flight flight) {
        flights.add(flight);
        flight.setAirline(this);
    }

}
