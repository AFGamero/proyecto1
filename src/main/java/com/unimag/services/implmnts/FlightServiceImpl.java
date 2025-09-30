package com.unimag.services.implmnts;


import com.unimag.api.dto.FlightDtos;
import com.unimag.dominio.entidades.Airline;
import com.unimag.dominio.entidades.Airport;
import com.unimag.dominio.entidades.Flight;
import com.unimag.dominio.repositories.AirlineRepository;
import com.unimag.dominio.repositories.AirportRepository;
import com.unimag.dominio.repositories.FlightRepository;
import com.unimag.dominio.repositories.TagRepository;
import com.unimag.services.FlightService;
import com.unimag.services.mappers.FlightMapper;
import lombok.NoArgsConstructor;
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
    @Override
    public FlightDtos.FlightResponse create(Long airlineId, Long originId, Long destinationId, FlightDtos.FlightCreateRequest request) {
        Airline airline = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new RuntimeException("Airline with id " + airlineId + " not found"));

        Airport destination = airportRepository.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Destination Airport with id " + destinationId + " not found"));
        Airport origin = airportRepository.findById(originId)
                .orElseThrow(() -> new RuntimeException("Origin Airport with id " + originId + " not found"));

        Flight flight = FlightMapper.ToEntity(request);
        flight.setAirline(airline);
        flight.setOrigin(origin);
        flight.setDestination(destination);

        return FlightMapper.toResponse(flightRepository.save(flight));

    }

    @Override
    public FlightDtos.FlightResponse findById(Long id) {

        return flightRepository.findById(id).map(FlightMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Flight with id " + id + " not found"));
    }

    @Override
    public List<FlightDtos.FlightResponse> findAll() {
        return flightRepository.findAll().stream().map(FlightMapper::toResponse).toList();
    }

    @Override
    public FlightDtos.FlightResponse update(Long id, FlightDtos.FlightUpdateRequest request) {
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Flight with id " + id + " not found"));
            FlightMapper.patch(flight,request);
            return FlightMapper.toResponse(flightRepository.save(flight));
    }

    @Override
    public void deleteById(Long id) {
    flightRepository.deleteById(id);

    }

    @Override
    public FlightDtos.FlightResponse addTag(Long flightId, Long tagId) {
        return null;
    }

    @Override
    public FlightDtos.FlightResponse removeTag(Long flightId, Long tagId) {
        return null;
    }
}
