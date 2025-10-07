package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "items") // 🔥 evita bucles infinitos en toString()
@EqualsAndHashCode(exclude = "items")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "booking_id")
    private Long id;

    @Column(nullable = false, name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @OneToMany(
            mappedBy = "booking",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default // ✅ mantiene inicialización al usar builder()
    private List<BookingItem> items = new ArrayList<>();

    public void addItem(BookingItem bookingItem) {
        if (bookingItem == null) {
            throw new IllegalArgumentException("Booking item cannot be null");
        }
        items.add(bookingItem);
        bookingItem.setBooking(this);
    }

    public void removeItem(BookingItem bookingItem) {
        if (bookingItem != null) {
            items.remove(bookingItem);
            bookingItem.setBooking(null);
        }
    }
}
