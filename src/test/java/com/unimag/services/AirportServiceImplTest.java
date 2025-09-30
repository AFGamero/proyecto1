package com.unimag.services;

import com.unimag.api.dto.AirportDtos;
import com.unimag.dominio.entidades.Airport;
import com.unimag.dominio.repositories.AirportRepository;
import com.unimag.services.implmnts.AirportServiceImpl;
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

    @InjectMocks
    AirportServiceImpl service;

    // ═══════════════════════════════════════════════════════════
    // CREATE AIRPORT
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldCreateAirportAndReturnToResponse() {
        // ARRANGE
        var request = new AirportDtos.AirportCreateRequest(
                "BOG",
                "El Dorado International Airport",
                "Bogota"

        );

        when(repo.save(any())).thenAnswer(inv -> {
            Airport a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        // ACT
        var res = service.create(request);

        // ASSERT
        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.code()).isEqualTo("BOG");
        assertThat(res.name()).isEqualTo("El Dorado International Airport");
        assertThat(res.city()).isEqualTo("Bogota");

        verify(repo).save(any(Airport.class));
    }

    // ═══════════════════════════════════════════════════════════
    // FIND BY ID
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldFindAirportById() {
        // ARRANGE
        var airport = Airport.builder()
                .id(10L)
                .code("MDE")
                .name("Jose Maria Cordova")
                .city("Medellin")
                .build();

        when(repo.findById(10L)).thenReturn(Optional.of(airport));

        // ACT
        var response = service.findById(10L);

        // ASSERT
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.code()).isEqualTo("MDE");
        assertThat(response.name()).isEqualTo("Jose Maria Cordova");
        assertThat(response.city()).isEqualTo("Medellin");

        verify(repo).findById(10L);
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
    }

    // ═══════════════════════════════════════════════════════════
    // FIND ALL
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldListAllAirports() {
        // ARRANGE
        when(repo.findAll()).thenReturn(
                List.of(
                        Airport.builder()
                                .id(1L)
                                .code("BOG")
                                .name("El Dorado")
                                .city("Bogota")
                                .build(),
                        Airport.builder()
                                .id(2L)
                                .code("CLO")
                                .name("Alfonso Bonilla Aragon")
                                .city("Cali")
                                .build(),
                        Airport.builder()
                                .id(3L)
                                .code("MIA")
                                .name("Miami International")
                                .city("Miami")
                                .build()
                )
        );

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
    }

    // ═══════════════════════════════════════════════════════════
    // UPDATE AIRPORT
    // ═══════════════════════════════════════════════════════════

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
        when(repo.save(any())).thenReturn(entity);

        var updateReq = new AirportDtos.AirportUpdateRequest(
                "NEW",
                "New Airport Name"
        );

        // ACT
        var updated = service.update(5L, updateReq);

        // ASSERT
        assertThat(updated.code()).isEqualTo("NEW");
        assertThat(updated.name()).isEqualTo("New Airport Name");
        assertThat(updated.city()).isEqualTo("New City");

        verify(repo).findById(5L);
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
        when(repo.save(any())).thenReturn(entity);

        var updateReq = new AirportDtos.AirportUpdateRequest(
                null,
                "Simon Bolivar International"
        );

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

        var updateReq = new AirportDtos.AirportUpdateRequest(
                "TEST",
                "Test Airport"
        );

        // ACT & ASSERT
        assertThatThrownBy(() -> service.update(999L, updateReq))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Airport with id 999 not found");

        verify(repo).findById(999L);
        verify(repo, never()).save(any());
    }

    // ═══════════════════════════════════════════════════════════
    // DELETE AIRPORT
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldDeleteAirport() {
        // ACT
        service.deleteById(100L);

        // ASSERT
        verify(repo).deleteById(100L);
    }
}