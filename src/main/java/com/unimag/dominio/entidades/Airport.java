package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Setter
@Getter
@Table(name = "airports")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Airport {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "airport_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 3)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

}
