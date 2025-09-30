package com.unimag.services;

import com.unimag.api.dto.BookingDtos.*;
import com.unimag.dominio.entidades.Booking;
import com.unimag.dominio.entidades.Passenger;
import com.unimag.dominio.repositories.BookingRepository;
import com.unimag.dominio.repositories.PassengerRepository;
import com.unimag.exception.NotFoundException;
import com.unimag.services.implmnts.BookingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    PassengerRepository passengerRepository;

    @InjectMocks
    BookingServiceImpl service;

    // ═══════════════════════════════════════════════════════════
    // CREATE BOOKING
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldCreateBookingAndMapToResponse() {
        // ARRANGE
        var passenger = Passenger.builder()
                .id(10L)
                .fullName("Juan Perez")
                .email("juan@mail.com")
                .build();

        when(passengerRepository.findById(10L)).thenReturn(Optional.of(passenger));
        when(bookingRepository.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(100L);
            return b;
        });

        var request = new BookingCreateRequest(10L);

        // ACT
        var response = service.createBooking(request);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.passenger_name()).isEqualTo("Juan Perez");
        assertThat(response.passenger_email()).isEqualTo("juan@mail.com");
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.items()).isEmpty();

        verify(passengerRepository).findById(10L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenPassengerNotFoundOnCreate() {
        // ARRANGE
        when(passengerRepository.findById(999L)).thenReturn(Optional.empty());

        var request = new BookingCreateRequest(999L);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.createBooking(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Passenger with id 999 not found");

        verify(passengerRepository).findById(999L);
        verify(bookingRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════════════════════
    // GET BOOKING
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldGetBookingById() {
        // ARRANGE
        var passenger = Passenger.builder()
                .id(5L)
                .fullName("Maria Lopez")
                .email("maria@mail.com")
                .build();

        var booking = Booking.builder()
                .id(50L)
                .createdAt(OffsetDateTime.now())
                .passenger(passenger)
                .items(new ArrayList<>())
                .build();

        when(bookingRepository.findById(50L)).thenReturn(Optional.of(booking));

        // ACT
        var response = service.getBooking(50L);

        // ASSERT
        assertThat(response.id()).isEqualTo(50L);
        assertThat(response.passenger_name()).isEqualTo("Maria Lopez");
        assertThat(response.passenger_email()).isEqualTo("maria@mail.com");

        verify(bookingRepository).findById(50L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookingNotFound() {
        // ARRANGE
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.getBooking(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Booking 999 not found");

        verify(bookingRepository).findById(999L);
    }

    // ═══════════════════════════════════════════════════════════
    // UPDATE BOOKING
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldUpdateBookingPassenger() {
        // ARRANGE
        var oldPassenger = Passenger.builder()
                .id(1L)
                .fullName("Old Passenger")
                .email("old@mail.com")
                .build();

        var newPassenger = Passenger.builder()
                .id(2L)
                .fullName("New Passenger")
                .email("new@mail.com")
                .build();

        var booking = Booking.builder()
                .id(30L)
                .createdAt(OffsetDateTime.now())
                .passenger(oldPassenger)
                .items(new ArrayList<>())
                .build();

        when(bookingRepository.findById(30L)).thenReturn(Optional.of(booking));
        when(passengerRepository.findById(2L)).thenReturn(Optional.of(newPassenger));
        when(bookingRepository.save(any())).thenReturn(booking);

        // ACT
        var response = service.updateBooking(30L, 2L);

        // ASSERT
        assertThat(response.passenger_name()).isEqualTo("New Passenger");
        assertThat(response.passenger_email()).isEqualTo("new@mail.com");

        verify(bookingRepository).findById(30L);
        verify(passengerRepository).findById(2L);
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFoundOnUpdate() {
        // ARRANGE
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.updateBooking(999L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Booking with id 999 not found");

        verify(bookingRepository).findById(999L);
        verify(passengerRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPassengerNotFoundOnUpdate() {
        // ARRANGE
        var booking = Booking.builder()
                .id(20L)
                .build();

        when(bookingRepository.findById(20L)).thenReturn(Optional.of(booking));
        when(passengerRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.updateBooking(20L, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Passenger with id 999 not found");

        verify(bookingRepository).findById(20L);
        verify(passengerRepository).findById(999L);
        verify(bookingRepository, never()).save(any());
    }


    @Test
    void shouldDeleteBooking() {

        service.deleteBooking(100L);


        verify(bookingRepository).deleteById(100L);
    }
}