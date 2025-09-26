package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@Table(name = "airports")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;


    @OneToMany(mappedBy = "origin")
    @Builder.Default
    private List<Flight> originFlights = new ArrayList<>() ;

    @OneToMany(mappedBy = "destination")
    @Builder.Default
    private List<Flight> destinationFlights = new ArrayList<>() ;

}
