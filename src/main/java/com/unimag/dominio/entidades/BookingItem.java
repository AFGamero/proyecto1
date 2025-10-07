package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "booking") // 🔥 evita recursión con Booking
@EqualsAndHashCode(exclude = "booking")
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "booking_item_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cabin cabin;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "segment_order", nullable = false)
    private Integer segmentOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(optional = false)
    @JoinColumn(name = "flight_id")
    private Flight flight;
}
