package com.unimag.services.implmnts;


import com.unimag.api.dto.BookingDtos;
import com.unimag.dominio.entidades.Booking;
import com.unimag.dominio.repositories.BookingRepository;
import com.unimag.dominio.repositories.PassengerRepository;
import com.unimag.exception.NotFoundException;
import com.unimag.services.BookingService;
import com.unimag.services.mappers.BookingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;

    @Override
    public BookingDtos.BookingResponse createBooking(BookingDtos.BookingCreateRequest request) {
        var Passenger = passengerRepository.findById(request.passenger_id())
                .orElseThrow(() -> new RuntimeException("Passenger with id " + request.passenger_id() + " not found"));
        var booking = Booking.builder().createdAt(OffsetDateTime.now()).passenger(Passenger).build();

        return BookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtos.BookingResponse getBooking(Long id) {
        return bookingRepository.findById(id).map(BookingMapper::toResponse).orElseThrow(
                () -> new NotFoundException("Booking %d not found.".formatted(id))
        );
    }


    @Override
    public Page<BookingDtos.BookingResponse> listBookingsByPassengerEmailAndOrderedMostRecently(String passenger_email, Pageable pageable) {
        return null;
    }

    @Override
    public BookingDtos.BookingResponse getBookingWithAllDetails(Long id) {
        return null;
    }

    @Override
    public BookingDtos.BookingResponse updateBooking(Long id, Long passenger_id) {
        return null;
    }

    @Override
    public void deleteBooking(Long id) {

    }
}
