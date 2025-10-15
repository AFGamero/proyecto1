package com.unimag.services.implmnts;


import com.unimag.api.dto.FlightDtos;
import com.unimag.dominio.entidades.Airline;
import com.unimag.dominio.entidades.Airport;
import com.unimag.dominio.entidades.Flight;
import com.unimag.dominio.repositories.AirlineRepository;
import com.unimag.dominio.repositories.AirportRepository;
import com.unimag.dominio.repositories.FlightRepository;
import com.unimag.dominio.repositories.TagRepository;
import com.unimag.exception.NotFoundException;
import com.unimag.services.FlightService;
import com.unimag.services.mappers.FlightMapper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final TagRepository tagRepository;
    private final FlightMapper flightMapper;
    @Override
    public FlightDtos.FlightResponse create(Long airlineId, Long originId, Long destinationId, FlightDtos.FlightCreateRequest request) {
        Airline airline = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new RuntimeException("Airline with id " + airlineId + " not found"));

        Airport destination = airportRepository.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Destination Airport with id " + destinationId + " not found"));
        Airport origin = airportRepository.findById(originId)
                .orElseThrow(() -> new RuntimeException("Origin Airport with id " + originId + " not found"));

        Flight flight = flightMapper.toEntity(request);
        flight.setAirline(airline);
        flight.setOrigin(origin);
        flight.setDestination(destination);

        return flightMapper.toResponse(flightRepository.save(flight));

    }

    @Override
    public FlightDtos.FlightResponse findById(Long id) {

        return flightRepository.findById(id).map(flightMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Flight with id " + id + " not found"));
    }

    @Override
    public List<FlightDtos.FlightResponse> findAll() {
        return flightRepository.findAll().stream().map(flightMapper::toResponse).toList();
    }

    @Override
    public FlightDtos.FlightResponse update(Long id, FlightDtos.FlightUpdateRequest request) {
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Flight with id " + id + " not found"));
            flightMapper.patch(request, flight);
            return flightMapper.toResponse(flightRepository.save(flight));
    }

    @Override
    public void deleteById(Long id) {
    flightRepository.deleteById(id);

    }

    @Override
    public FlightDtos.FlightResponse addTag(@Nonnull Long flightId, @Nonnull Long tagId) {
        var flight = flightRepository.findById(flightId).orElseThrow(()-> new NotFoundException("Flight with id " + flightId + " not found"));
        var tag = tagRepository.findById(tagId).orElseThrow(() -> new NotFoundException("Tag with id " + tagId + " not found"));
        flight.addTag(tag);
        return flightMapper.toResponse(flight);
    }

    @Override
    public FlightDtos.FlightResponse removeTag(Long flightId, Long tagId) {
        var flight = flightRepository.findById(flightId).orElseThrow(()-> new NotFoundException("Flight with id " + flightId + " not found"));
        var tag = tagRepository.findById(tagId).orElseThrow(() -> new NotFoundException("Tag with id " + tagId + " not found"));
        flight.getTags().remove(tag);
        tag.getFlights().remove(flight);
        return flightMapper.toResponse(flight);
    }
}
