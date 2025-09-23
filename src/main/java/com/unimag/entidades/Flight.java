package com.unimag.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Setter
@Getter
@Table(name = "flights")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private OffsetDateTime departureTime;

    @Column(nullable = false)
    private OffsetDateTime arrivalTime;

    @OneToMany(mappedBy = "bookingItem")
    @Builder.Default
    private List<BookingItem> bookingItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;

    @ManyToOne
    @JoinColumn(name = "origin_id")
    private Airport origin;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Airport destination;

    @OneToMany(mappedBy = "flight")
    @Builder.Default
    private List<SeatInventory> seatlnventorysList = new ArrayList<>();

    @ManyToMany

    @JoinTable(
            name = "flight_tags",                                // tabla intermedia
            joinColumns = @JoinColumn(name = "flight_id"),       // FK hacia Flight
            inverseJoinColumns = @JoinColumn(name = "tag_id")    // FK hacia Tag
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getFlights().add(this);
    }

}
