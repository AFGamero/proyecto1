package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Setter
@Getter
@Table(name = "bookings")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Booking {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "booking_id")
    private Long id;

    @Column(nullable = false, name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    private List<BookingItem> items;

    public void addItem(BookingItem bookingItem) {
        items.add(bookingItem);
        bookingItem.setBooking(this);
    }

}
