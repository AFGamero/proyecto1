package com.unimag.services;

import com.unimag.api.dto.AirportDtos;
import com.unimag.dominio.entidades.Airport;
import com.unimag.dominio.repositories.AirportRepository;
import com.unimag.services.implmnts.AirportServiceImpl;
import com.unimag.services.mappers.AirportMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportServiceImplTest {

    @Mock
    AirportRepository repo;

    @Mock
    AirportMapper airportMapper;

    @InjectMocks
    AirportServiceImpl service;

    @Test
    void shouldCreateAirportAndReturnToResponse() {
        // ARRANGE
        var request = new AirportDtos.AirportCreateRequest(
                "BOG",
                "El Dorado International Airport",
                "Bogota"
        );

        var airport = Airport.builder()
                .code("BOG")
                .name("El Dorado International Airport")
                .city("Bogota")
                .build();

        var savedAirport = Airport.builder()
                .id(1L)
                .code("BOG")
                .name("El Dorado International Airport")
                .city("Bogota")
                .build();

        var response = new AirportDtos.AirportResponse(1L, "BOG", "El Dorado International Airport", "Bogota");

        when(airportMapper.toEntity(request)).thenReturn(airport);
        when(repo.save(airport)).thenReturn(savedAirport);
        when(airportMapper.toResponse(savedAirport)).thenReturn(response);

        // ACT
        var res = service.create(request);

        // ASSERT
        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.code()).isEqualTo("BOG");
        assertThat(res.name()).isEqualTo("El Dorado International Airport");
        assertThat(res.city()).isEqualTo("Bogota");

        verify(airportMapper).toEntity(request);
        verify(repo).save(airport);
        verify(airportMapper).toResponse(savedAirport);
    }

    @Test
    void shouldFindAirportById() {
        // ARRANGE
        var airport = Airport.builder()
                .id(10L)
                .code("MDE")
                .name("Jose Maria Cordova")
                .city("Medellin")
                .build();

        var response = new AirportDtos.AirportResponse(10L, "MDE", "Jose Maria Cordova", "Medellin");

        when(repo.findById(10L)).thenReturn(Optional.of(airport));
        when(airportMapper.toResponse(airport)).thenReturn(response);

        // ACT
        var result = service.findById(10L);

        // ASSERT
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.code()).isEqualTo("MDE");
        assertThat(result.name()).isEqualTo("Jose Maria Cordova");
        assertThat(result.city()).isEqualTo("Medellin");

        verify(repo).findById(10L);
        verify(airportMapper).toResponse(airport);
    }

    @Test
    void shouldThrowExceptionWhenAirportNotFound() {
        // ARRANGE
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Airport with id 999 not found");

        verify(repo).findById(999L);
        verifyNoInteractions(airportMapper);
    }

    @Test
    void shouldListAllAirports() {
        // ARRANGE
        var airport1 = Airport.builder().id(1L).code("BOG").name("El Dorado").city("Bogota").build();
        var airport2 = Airport.builder().id(2L).code("CLO").name("Alfonso Bonilla Aragon").city("Cali").build();
        var airport3 = Airport.builder().id(3L).code("MIA").name("Miami International").city("Miami").build();

        var response1 = new AirportDtos.AirportResponse(1L, "BOG", "El Dorado", "Bogota");
        var response2 = new AirportDtos.AirportResponse(2L, "CLO", "Alfonso Bonilla Aragon", "Cali");
        var response3 = new AirportDtos.AirportResponse(3L, "MIA", "Miami International", "Miami");

        when(repo.findAll()).thenReturn(List.of(airport1, airport2, airport3));
        when(airportMapper.toResponse(airport1)).thenReturn(response1);
        when(airportMapper.toResponse(airport2)).thenReturn(response2);
        when(airportMapper.toResponse(airport3)).thenReturn(response3);

        // ACT
        var airports = service.findAll();

        // ASSERT
        assertThat(airports).hasSize(3);
        assertThat(airports).extracting(AirportDtos.AirportResponse::code)
                .containsExactlyInAnyOrder("BOG", "CLO", "MIA");
        assertThat(airports).extracting(AirportDtos.AirportResponse::city)
                .containsExactlyInAnyOrder("Bogota", "Cali", "Miami");

        verify(repo).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoAirports() {
        // ARRANGE
        when(repo.findAll()).thenReturn(List.of());

        // ACT
        var airports = service.findAll();

        // ASSERT
        assertThat(airports).isEmpty();

        verify(repo).findAll();
        verifyNoInteractions(airportMapper);
    }

    @Test
    void shouldUpdateAirportViaPatch() {
        // ARRANGE
        var entity = Airport.builder()
                .id(5L)
                .code("OLD")
                .name("Old Airport Name")
                .city("Old City")
                .build();

        when(repo.findById(5L)).thenReturn(Optional.of(entity));
        when(repo.save(entity)).thenReturn(entity);

        doAnswer(inv -> {
            Airport a = inv.getArgument(1);
            AirportDtos.AirportUpdateRequest req = inv.getArgument(0);
            if (req.code() != null) a.setCode(req.code());
            a.setName(req.name());
            return null;
        }).when(airportMapper).updateEntityFromRequest(any(), any());

        var response = new AirportDtos.AirportResponse(5L, "NEW", "New Airport Name", "Old City");
        when(airportMapper.toResponse(entity)).thenReturn(response);

        var updateReq = new AirportDtos.AirportUpdateRequest("NEW", "New Airport Name");

        // ACT
        var updated = service.update(5L, updateReq);

        // ASSERT
        assertThat(updated.code()).isEqualTo("NEW");
        assertThat(updated.name()).isEqualTo("New Airport Name");
        assertThat(updated.city()).isEqualTo("Old City");

        verify(repo).findById(5L);
        verify(airportMapper).updateEntityFromRequest(updateReq, entity);
        verify(repo).save(entity);
    }

    @Test
    void shouldUpdateAirportPartially() {
        // ARRANGE
        var entity = Airport.builder()
                .id(7L)
                .code("SMR")
                .name("Simon Bolivar")
                .city("Santa Marta")
                .build();

        when(repo.findById(7L)).thenReturn(Optional.of(entity));
        when(repo.save(entity)).thenReturn(entity);

        doAnswer(inv -> {
            Airport a = inv.getArgument(1);
            AirportDtos.AirportUpdateRequest req = inv.getArgument(0);
            if (req.name() != null) a.setName(req.name());
            return null;
        }).when(airportMapper).updateEntityFromRequest(any(), any());

        var response = new AirportDtos.AirportResponse(7L, "SMR", "Simon Bolivar International", "Santa Marta");
        when(airportMapper.toResponse(entity)).thenReturn(response);

        var updateReq = new AirportDtos.AirportUpdateRequest(null, "Simon Bolivar International");

        // ACT
        var updated = service.update(7L, updateReq);

        // ASSERT
        assertThat(updated.code()).isEqualTo("SMR");
        assertThat(updated.name()).isEqualTo("Simon Bolivar International");
        assertThat(updated.city()).isEqualTo("Santa Marta");

        verify(repo).findById(7L);
        verify(repo).save(entity);
    }

    @Test
    void shouldThrowExceptionWhenAirportNotFoundOnUpdate() {
        // ARRANGE
        when(repo.findById(999L)).thenReturn(Optional.empty());

        var updateReq = new AirportDtos.AirportUpdateRequest("TEST", "Test Airport");

        // ACT & ASSERT
        assertThatThrownBy(() -> service.update(999L, updateReq))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Airport with id 999 not found");

        verify(repo).findById(999L);
        verify(repo, never()).save(any());
    }

    @Test
    void shouldDeleteAirport() {
        // ACT
        service.deleteById(100L);

        // ASSERT
        verify(repo).deleteById(100L);
    }
}