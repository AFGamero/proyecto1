package com.unimag.services;

import com.unimag.api.dto.BookingDtos.*;
import com.unimag.dominio.entidades.*;
import com.unimag.dominio.repositories.BookingItemRepository;
import com.unimag.dominio.repositories.BookingRepository;
import com.unimag.dominio.repositories.FlightRepository;
import com.unimag.services.implmnts.BookingItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingItemServiceImplTest {

    @Mock
    BookingItemRepository bookingItemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    FlightRepository flightRepository;

    @InjectMocks
    BookingItemServiceImpl service;

    // ═══════════════════════════════════════════════════════════
    // ADD ITEM
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldAddItemToBooking() {
        // ARRANGE
        var booking = Booking.builder()
                .id(10L)
                .createdAt(OffsetDateTime.now())
                .build();

        var flight = Flight.builder()
                .id(50L)
                .number("AV123")
                .build();

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(50L)).thenReturn(Optional.of(flight));

        var request = new BookingItemCreateRequest("ECONOMY", new BigDecimal("150.00"),10);

        // ACT
        var response = service.addItem(10L, 50L, request);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.cabin()).isEqualTo("ECONOMY");
        assertThat(response.price()).isEqualTo(new BigDecimal("150.00"));
        assertThat(response.flight_number()).isEqualTo("AV123");

        verify(bookingRepository).findById(10L);
        verify(flightRepository).findById(50L);
    }

    // ═══════════════════════════════════════════════════════════
    // GET BOOKING ITEM
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldGetBookingItemById() {
        // ARRANGE
        var flight = Flight.builder()
                .id(30L)
                .number("LA456")
                .build();

        var bookingItem = BookingItem.builder()
                .id(100L)
                .cabin(Cabin.BUSINESS)
                .price(new BigDecimal("500.00"))
                .flight(flight)
                .segmentOrder(1)
                .build();

        when(bookingItemRepository.findById(100L)).thenReturn(Optional.of(bookingItem));

        // ACT
        var response = service.getBookingItem(100L);

        // ASSERT
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.cabin()).isEqualTo("BUSINESS");
        assertThat(response.price()).isEqualTo(new BigDecimal("500.00"));

        verify(bookingItemRepository).findById(100L);
    }

    // ═══════════════════════════════════════════════════════════
    // LIST BY BOOKING
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldListItemsByBookingId() {
        // ARRANGE
        var booking = Booking.builder()
                .id(20L)
                .build();

        var flight1 = Flight.builder().id(1L).number("FL001").build();
        var flight2 = Flight.builder().id(2L).number("FL002").build();

        var items = List.of(
                BookingItem.builder()
                        .id(201L)
                        .cabin(Cabin.ECONOMY)
                        .price(new BigDecimal("100.00"))
                        .flight(flight1)
                        .segmentOrder(1)
                        .build(),
                BookingItem.builder()
                        .id(202L)
                        .cabin(Cabin.BUSINESS)
                        .price(new BigDecimal("300.00"))
                        .flight(flight2)
                        .segmentOrder(2)
                        .build()
        );

        when(bookingRepository.findById(20L)).thenReturn(Optional.of(booking));
        when(bookingItemRepository.findByBookingIdOrderBySegmentOrder(20L)).thenReturn(items);

        // ACT
        var response = service.listByBooking(20L);

        // ASSERT
        assertThat(response).hasSize(2);
        assertThat(response).extracting(BookingItemResponse::cabin)
                .containsExactly("ECONOMY", "BUSINESS");
        assertThat(response).extracting(BookingItemResponse::flight_number)
                .containsExactly("FL001", "FL002");

        verify(bookingRepository).findById(20L);
        verify(bookingItemRepository).findByBookingIdOrderBySegmentOrder(20L);
    }

    // ═══════════════════════════════════════════════════════════
    // UPDATE ITEM
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldUpdateBookingItemFlight() {
        // ARRANGE
        var oldFlight = Flight.builder()
                .id(10L)
                .number("OLD123")
                .build();

        var newFlight = Flight.builder()
                .id(20L)
                .number("NEW456")
                .build();

        var bookingItem = BookingItem.builder()
                .id(500L)
                .cabin(Cabin.ECONOMY)
                .price(new BigDecimal("200.00"))
                .flight(oldFlight)
                .build();

        when(bookingItemRepository.findById(500L)).thenReturn(Optional.of(bookingItem));
        when(flightRepository.findById(20L)).thenReturn(Optional.of(newFlight));

        var updateReq = new BookingItemUpdateRequest("BUSINESS", new BigDecimal("350.00"), 10);

        // ACT
        var response = service.updateItem(500L, 20L, updateReq);

        // ASSERT
        assertThat(response.flight_number()).isEqualTo("NEW456");

        verify(bookingItemRepository).findById(500L);
        verify(flightRepository).findById(20L);
    }

    // ═══════════════════════════════════════════════════════════
    // DELETE ITEM
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldDeleteBookingItem() {
        // ACT
        service.deleteBookingItem(999L);

        // ASSERT
        verify(bookingItemRepository).deleteById(999L);
    }

    @Test
    void shouldRemoveItem() {
        // ACT
        service.removeItem(888L);

        // ASSERT
        verify(bookingItemRepository).deleteById(888L);
    }
}