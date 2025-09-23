package com.unimag.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Builder
@Setter
@Getter
@Table(name = "bookings")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    //clase madre
    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @OneToMany(mappedBy = "booking")
    @Builder.Default
    private List<BookingItem> bookingsItems = new ArrayList<>();


}
