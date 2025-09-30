package com.unimag.services;

import com.unimag.api.dto.PassengerDtos;
import com.unimag.api.dto.PassengerDtos.*;
import com.unimag.api.dto.PassengerProfileDtos;
import com.unimag.dominio.entidades.Passenger;
import com.unimag.dominio.entidades.PassengerProfile;
import com.unimag.dominio.repositories.PassengerRepository;
import com.unimag.services.implmnts.PassengerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTest {
    @Mock
    PassengerRepository passengerRepository;

    @InjectMocks
    PassengerServiceImpl passengerService;

    @Test
    void shouldCreatePassengerAndReturnToResponse() {
        var profile = new PassengerProfileDto("3220232002", "57");
        var request = new PassengerCreateRequest("Test User", "testuser", profile);

        when(passengerRepository.save(any())).thenAnswer(invocation -> {
            Passenger passenger = invocation.getArgument(0);
            passenger.setId(10l);
            return passenger;
        });

        var res = passengerService.create(request);
        assertThat(res.id()).isEqualTo(10l);
        assertThat(res.fullName()).isEqualTo("Juan Perez");
        assertThat(res.email()).isEqualTo("juan@mail.com");
        assertThat(res.profile().countryCode()).isEqualTo("CO");
        verify(passengerRepository).save(any(Passenger.class));
    }

    @Test
    void shouldUpdatePassengerViaPatch() {
        var entity = Passenger.builder()
                .id(3L)
                .fullName("Old Name")
                .email("old@mail.com")
                .profile(PassengerProfile.builder()
                        .phone("+1")
                        .countryCode("US")
                        .build())
                .build();

        when(passengerRepository.findById(3L)).thenReturn(Optional.of(entity));

        var updateReq = new PassengerCreateUpdateRequest(
                "New Name",
                null,
                new PassengerProfileDto(null, "CO")
        );
        var updated = passengerService.update(3L, updateReq);

        assertThat(updated.fullName()).isEqualTo("New Name");
        assertThat(updated.email()).isEqualTo("old@mail.com");
        assertThat(updated.profile().countryCode()).isEqualTo("CO");

    }

    @Test
    void shouldListAllPassengers() {
        var passengersPage = new PageImpl<>(List.of(
                Passenger.builder().id(1L).fullName("Ana").email("ana@mail.com").build(),
                Passenger.builder().id(2L).fullName("Bob").email("bob@mail.com").build()
        ));

        // Mock: cuando llamen a repo.findAll(Pageable.unpaged()), retorna passengersPage
        when(passengerRepository.findAll(Pageable.unpaged())).thenReturn(passengersPage);

        // Act
        var passengers = passengerService.findAll(); // ahora devuelve List<PassengerResponse>

        // Assert
        assertThat(passengers).hasSize(2);
        assertThat(passengers).extracting(PassengerDtos.PassengerResponse::fullName)
                .containsExactly("Ana", "Bob");

    }
    @Test
    void shouldFindPassengerByEmail() {
        var entity = Passenger.builder()
                .id(7L)
                .fullName("Carlos")
                .email("carlos@mail.com")
                .build();

        when(passengerRepository.findByEmailIgnoreCase("carlos@mail.com"))
                .thenReturn(Optional.of(entity));

        var passenger = passengerService.getByEmail("carlos@mail.com");

        assertThat(passenger.id()).isEqualTo(7L);
        assertThat(passenger.email()).isEqualTo("carlos@mail.com");
    }

}