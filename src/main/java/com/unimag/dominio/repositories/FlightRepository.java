package com.unimag.dominio.repositories;

import com.unimag.dominio.entidades.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    /**
     * Lista vuelos operados por una aerolínea (match por nombre).
     */
    List<Flight> findByAirlineName(String airlineName);

    /**
     * Busca vuelos por aerolínea y ventana de salida; con paginación.
     */
    @Query("SELECT f FROM Flight f WHERE f.airline.name LIKE %:airlineName% " +
            "AND f.departureTime BETWEEN :from AND :to")
    Page<Flight> findByAirlineAndDepartureBetween(
            @Param("airlineName") String airlineName,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            Pageable pageable
    );

    /**
     * Filtra por aerolínea y ventana de salida, precargando airline y tags.
     */
    @Query("SELECT DISTINCT f FROM Flight f " +
            "LEFT JOIN FETCH f.airline " +
            "LEFT JOIN FETCH f.tags " +
            "WHERE (:airlineName IS NULL OR f.airline.name LIKE %:airlineName%) " +
            "AND f.departureTime BETWEEN :from AND :to")
    List<Flight> findByAirlineAndDepartureBetweenWithAssociations(
            @Param("airlineName") String airlineName,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );

    /**
     * Devuelve los vuelos que poseen todas las tags indicadas.
     */
    @Query(value = "SELECT f.* FROM flight f " +
            "INNER JOIN flight_tags ft ON f.id = ft.flight_id " +
            "INNER JOIN tag t ON ft.tags_id = t.id " +
            "WHERE t.name IN :tagNames " +
            "GROUP BY f.id " +
            "HAVING COUNT(DISTINCT t.name) = :requiredCount",
            nativeQuery = true)
    List<Flight> findFlightsWithAllTags(@Param("tagNames") Collection<String> tagNames,
                                        @Param("requiredCount") long requiredCount);
}
