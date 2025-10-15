package com.unimag.dominio.repositories;

import com.unimag.dominio.entidades.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {



    Optional<Airline> findByCode(String code);// IATA code (ej. "AV")

}