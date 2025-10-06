package com.unimag.services;

import com.unimag.api.dto.PassengerDtos;
import com.unimag.api.dto.PassengerDtos.*;
import com.unimag.dominio.entidades.Passenger;
import com.unimag.dominio.entidades.PassengerProfile;
import com.unimag.dominio.repositories.PassengerRepository;
import com.unimag.services.implmnts.PassengerServiceImpl;
import com.unimag.services.mappers.PassengerMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTest {

    @Mock
    PassengerRepository passengerRepository;

    @Mock
    PassengerMapper passengerMapper;

    @InjectMocks
    PassengerServiceImpl passengerService;

    @Test
    void shouldCreatePassengerAndReturnToResponse() {
        // ARRANGE
        var profileDto = new PassengerProfileDto("3220232002", "57");
        var request = new PassengerCreateRequest("Juan Perez", "juan@mail.com", profileDto);

        var passengerToSave = Passenger.builder()
                .fullName("Juan Perez")
                .email("juan@mail.com")
                .profile(PassengerProfile.builder()
                        .phone("3220232002")
                        .countryCode("57")
                        .build())
                .build();

        var savedPassenger = Passenger.builder()
                .id(10L)
                .fullName("Juan Perez")
                .email("juan@mail.com")
                .profile(PassengerProfile.builder()
                        .phone("3220232002")
                        .countryCode("57")
                        .build())
                .build();

        var expectedResponse = new PassengerResponse(
                10L,
                "Juan Perez",
                "juan@mail.com",
                new PassengerProfileDto("3220232002", "57")
        );

        when(passengerMapper.toEntity(request)).thenReturn(passengerToSave);
        when(passengerRepository.save(passengerToSave)).thenReturn(savedPassenger);
        when(passengerMapper.toResponse(savedPassenger)).thenReturn(expectedResponse);

        // ACT
        var res = passengerService.create(request);

        // ASSERT
        assertThat(res.id()).isEqualTo(10L);
        assertThat(res.fullName()).isEqualTo("Juan Perez");
        assertThat(res.email()).isEqualTo("juan@mail.com");
        assertThat(res.profile().phone()).isEqualTo("3220232002");
        assertThat(res.profile().countryCode()).isEqualTo("57");

        verify(passengerMapper).toEntity(request);
        verify(passengerRepository).save(passengerToSave);
        verify(passengerMapper).toResponse(savedPassenger);
    }

    @Test
    void shouldUpdatePassengerViaPatch() {
        // ARRANGE
        var entity = Passenger.builder()
                .id(3L)
                .fullName("Old Name")
                .email("old@mail.com")
                .profile(PassengerProfile.builder()
                        .phone("+1234567890")
                        .countryCode("US")
                        .build())
                .build();

        var updateReq = new PassengerUpdateRequest(
                "New Name",
                null,
                new PassengerProfileDto(null, "57")
        );

        when(passengerRepository.findById(3L)).thenReturn(Optional.of(entity));

        doAnswer(inv -> {
            PassengerUpdateRequest req = inv.getArgument(0);
            Passenger pass = inv.getArgument(1);
            if (req.fullName() != null) pass.setFullName(req.fullName());
            if (req.email() != null) pass.setEmail(req.email());
            if (req.profile() != null && req.profile().countryCode() != null) {
                pass.getProfile().setCountryCode(req.profile().countryCode());
            }
            return null;
        }).when(passengerMapper).patch(any(), any());

        when(passengerRepository.save(entity)).thenReturn(entity);

        var expectedResponse = new PassengerResponse(
                3L,
                "New Name",
                "old@mail.com",
                new PassengerProfileDto("+1234567890", "57")
        );
        when(passengerMapper.toResponse(entity)).thenReturn(expectedResponse);

        // ACT
        var updated = passengerService.update(3L, updateReq);

        // ASSERT
        assertThat(updated).isNotNull();
        assertThat(updated.fullName()).isEqualTo("New Name");
        assertThat(updated.email()).isEqualTo("old@mail.com");
        assertThat(updated.profile().countryCode()).isEqualTo("57");

        verify(passengerRepository).findById(3L);
        verify(passengerMapper).patch(updateReq, entity);
        verify(passengerRepository).save(entity);
        verify(passengerMapper).toResponse(entity);
    }

    @Test
    void shouldListAllPassengers() {
        // ARRANGE
        var passenger1 = Passenger.builder()
                .id(1L)
                .fullName("Ana")
                .email("ana@mail.com")
                .profile(PassengerProfile.builder().build())
                .build();

        var passenger2 = Passenger.builder()
                .id(2L)
                .fullName("Bob")
                .email("bob@mail.com")
                .profile(PassengerProfile.builder().build())
                .build();

        var passengersPage = new PageImpl<>(List.of(passenger1, passenger2));

        when(passengerRepository.findAll(Pageable.unpaged())).thenReturn(passengersPage);

        var response1 = new PassengerResponse(1L, "Ana", "ana@mail.com",
                new PassengerProfileDto(null, null));
        var response2 = new PassengerResponse(2L, "Bob", "bob@mail.com",
                new PassengerProfileDto(null, null));

        when(passengerMapper.toResponse(passenger1)).thenReturn(response1);
        when(passengerMapper.toResponse(passenger2)).thenReturn(response2);

        // ACT
        var passengers = passengerService.findAll();

        // ASSERT
        assertThat(passengers).hasSize(2);
        assertThat(passengers).extracting(PassengerResponse::fullName)
                .containsExactly("Ana", "Bob");

        verify(passengerRepository).findAll(Pageable.unpaged());
        verify(passengerMapper).toResponse(passenger1);
        verify(passengerMapper).toResponse(passenger2);
    }

    @Test
    void shouldFindPassengerByEmail() {
        // ARRANGE
        var entity = Passenger.builder()
                .id(7L)
                .fullName("Carlos")
                .email("carlos@mail.com")
                .profile(PassengerProfile.builder()
                        .phone("1234567890")
                        .countryCode("CO")
                        .build())
                .build();

        when(passengerRepository.findByEmailIgnoreCase("carlos@mail.com"))
                .thenReturn(Optional.of(entity));

        var expectedResponse = new PassengerResponse(
                7L,
                "Carlos",
                "carlos@mail.com",
                new PassengerProfileDto("1234567890", "CO")
        );
        when(passengerMapper.toResponse(entity)).thenReturn(expectedResponse);

        // ACT
        var passenger = passengerService.getByEmail("carlos@mail.com");

        // ASSERT
        assertThat(passenger).isNotNull();
        assertThat(passenger.id()).isEqualTo(7L);
        assertThat(passenger.email()).isEqualTo("carlos@mail.com");
        assertThat(passenger.fullName()).isEqualTo("Carlos");

        verify(passengerRepository).findByEmailIgnoreCase("carlos@mail.com");
        verify(passengerMapper).toResponse(entity);
    }
}