package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@Table(name = "passengers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Passenger {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "passenger_id")
    private Long id;

    @Column(nullable = false, name = "full_name")
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToOne @JoinColumn(name = "passenger_profile_id", unique = true)
    private PassengerProfile profile;


}
