package com.unimag.dominio.repositories;

import com.unimag.dominio.entidades.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Pagina las reservas de un pasajero (match por email, ignorando caso)
     */
    @Query("SELECT b FROM Booking b WHERE LOWER(b.passenger.email) = LOWER(:email) ORDER BY b.createdAt DESC")
    Page<Booking> findByPassengerEmailIgnoreCaseOrderByCreatedAtDesc(@Param("email") String email, Pageable pageable);

    /**
     * Trae una reserva por id precargando items, items.flight y passenger.
     */
    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.bookingsItems bi " +
            "LEFT JOIN FETCH bi.flight " +
            "LEFT JOIN FETCH b.passenger " +
            "WHERE b.id = :id")
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);
}
