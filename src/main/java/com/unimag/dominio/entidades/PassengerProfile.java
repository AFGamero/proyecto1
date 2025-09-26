package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;


@Setter
@Getter
@Table(name = "passanger_profiles")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String countryCode;

    //clase hija de Passenger
    //relacion uno a uno con Passenger
    @OneToOne
    @JoinColumn(name = "passenger_id", unique = true)
    private Passenger passenger;


}
