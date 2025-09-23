package com.unimag.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
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

    @OneToMany
    @JoinColumn(name = "bookingItem_id")
    private List<BookingItem> bookingItems;

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;

    @OneToMany
    @JoinColumn(name = "seatInventory_id")
    private List<SeatInventory> seatlnventorysList;

    @ManyToMany(targetEntity = Tag.class)
    //private List<Tag> tags;
    private Set<Tag> tags = new HashSet<>();

    public void addTag(Tag tag){
        tags.add(tag);
           tag.getFlights().add(this);
    }

}
