package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Setter
@Getter
@Table(name = "flights")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Flight {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "flight_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false, name = "departure_time")
    private OffsetDateTime departureTime;

    @Column(nullable = false, name = "arrival_time")
    private OffsetDateTime arrivalTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "airline_id")
    private Airline airline;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origin_airport_id", nullable = false)
    private Airport origin;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_airport_id", nullable = false)
    private Airport destination;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @JoinTable(name = "tags_flights",
            joinColumns = @JoinColumn(name = "flight_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Builder.Default
    //
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    //Lo de arriba es pa evitar StackOverFlow
    private Set<Tag> tags = new HashSet<>();

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getFlights().add(this);
    }
}
