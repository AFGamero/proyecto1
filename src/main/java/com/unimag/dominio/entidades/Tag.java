package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@Setter
@Getter
@Table(name = "tags")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    //private Flight flights;
    @Builder.Default
    private Set<Flight> flights = new HashSet<>();

}
