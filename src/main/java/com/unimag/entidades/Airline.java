package com.unimag.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@Table(name = "airlines")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "airline", targetEntity = Flight.class)
    @Builder.Default
    private List<Flight> flights = new ArrayList<>();



}
