package com.unimag.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Table(name = "booking_items")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private Cabin cabin;

    private BigDecimal price;
    private Integer segmentOrder;

    @Column(nullable = false)
    private Cabin cabin;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;





}
