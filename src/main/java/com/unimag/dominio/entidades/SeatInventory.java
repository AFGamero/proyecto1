package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

@Data
@Setter
@Getter
@Table(name = "seat_inventories")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatInventory {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "seat_inventory_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Cabin cabin;

    @Column(nullable = false, name = "total_seats")
    private Integer totalSeats;

    @Column(nullable = false, name = "available_seats")
    private Integer availableSeats;

    @ManyToOne @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

}
