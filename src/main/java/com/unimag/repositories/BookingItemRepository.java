
package com.unimag.repositories;

import com.unimag.entidades.BookingItem;
import com.unimag.entidades.Cabin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {

    /**
     * Lista los segmentos de una reserva, ordenados por segmentOrder.
     */
    List<BookingItem> findByBookingIdOrderBySegmentOrder(Long bookingId);

    /**
     * Calcula el total de la reserva sumando los precios.
     */
    @Query("SELECT COALESCE(SUM(bi.price), 0) FROM BookingItem bi WHERE bi.booking.id = :bookingId")
    BigDecimal calculateTotalByBookingId(@Param("bookingId") Long bookingId);

    /**
     * Cuenta cuántos asientos han sido vendidos para un vuelo y cabina.
     */
    @Query("SELECT COUNT(bi) FROM BookingItem bi WHERE bi.flight.id = :flightId AND bi.cabin = :cabin")
    long countSeatsSoldByFlightAndCabin(@Param("flightId") Long flightId, @Param("cabin") Cabin cabin);
}