package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Setter
@Getter
@Table(name = "tags")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Tag {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Flight> flights = new HashSet<>();

}
