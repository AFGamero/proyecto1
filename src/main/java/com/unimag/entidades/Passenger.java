package com.unimag.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Table(name = "passangers")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;


    //clase madre de PassengerProfile
    //relacion uno a uno con PassengerProfile
    @OneToOne(mappedBy = "passenger")
    private PassengerProfile passengerProfile;


    //hija
    @OneToMany(mappedBy = "passenger")
    private List<Booking> bookings;


}
