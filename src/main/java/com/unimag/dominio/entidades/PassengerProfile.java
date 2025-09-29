package com.unimag.dominio.entidades;

import jakarta.persistence.*;
import lombok.*;

@Data
@Setter
@Getter
@Table(name = "passanger_profiles")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "passenger_profile_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, name = "country_code")
    private String countryCode;


}
