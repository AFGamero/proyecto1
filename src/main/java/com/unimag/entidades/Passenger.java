package com.unimag.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
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
    @OneToOne(optional  = false)
    @JoinColumn(name = "passengerProfile_id")
    private PassengerProfile passengerProfile;

    @OneToMany(mappedBy = "passenger",targetEntity = Booking.class)
    private List<Booking> bookings;


}
