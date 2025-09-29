package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Setter
@Getter
@Table(name = "booking_items")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingItem {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "booking_item_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Cabin cabin;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false, name = "segment_order")
    private Integer segmentOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(optional = false)
    @JoinColumn(name = "flight_id")
    private Flight flight;



}
