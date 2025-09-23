package com.unimag.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime createdAt;


    @ManyToOne(targetEntity = Passenger.class)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @OneToMany(mappedBy = "booking",targetEntity = BookingItem.class)
    private List<BookingItem> bookingsItems;

    public void addBookingItem(BookingItem bookingItem) {
        bookingsItems.add(bookingItem);
            bookingItem.setBooking(this);
    }



}
