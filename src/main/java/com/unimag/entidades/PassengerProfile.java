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
    @OneToOne(mappedBy = "passenger_id")
    private Passenger passenger;


}
