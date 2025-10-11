package com.unimag.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.api.dto.PassengerDtos.*;
import com.unimag.services.PassengerService;
import com.unimag.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PassengerController.class)
class PassengerControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockitoBean
    PassengerService service;

    @Test
    void create_shouldReturn201AndLocation() throws Exception {
        var req = new PassengerCreateRequest("Andres Gamero", "agamero@mail.com", new PassengerProfileDto("+57", "CO"));
        var resp = new PassengerResponse(10L, "Andres Gamero", "agamero@mail.com", new PassengerProfileDto("+57", "CO"));

        when(service.create(any())).thenReturn(resp);

        mvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/v1/passengers/10")))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void getById_shouldReturn200() throws Exception {
        when(service.getById(5L)).thenReturn(new PassengerResponse(5L, "UCC", "uccnosirve@mail.com", null));

        mvc.perform(get("/api/v1/passengers/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void getById_shouldReturn404WhenNotFound() throws Exception {
        when(service.getById(88L)).thenThrow(new NotFoundException("Passenger 88 not found"));

        mvc.perform(get("/api/v1/passengers/88"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Passenger 88 not found"));
    }

    @Test
    void getByEmail_shouldReturn200() throws Exception {
        when(service.getByEmail("elenanitodelbosque@mail.com"))
                .thenReturn(new PassengerResponse(3L, "Elena Nito Del Bosque", "elenanitodelbosque@mail.com", null));

        mvc.perform(get("/api/v1/passengers/by-email")
                        .param("email", "elenanitodelbosque@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("elenanitodelbosque@mail.com"));
    }

    @Test
    void findAll_shouldReturn200() throws Exception {
        var list = List.of(
                new PassengerResponse(1L, "Nestor", "nestor@mail.com", null),
                new PassengerResponse(2L, "KaCastillo", "kacastillo@mail.com", null)
        );
        when(service.findAll()).thenReturn(list);

        mvc.perform(get("/api/v1/passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        mvc.perform(delete("/api/v1/passengers/3"))
                .andExpect(status().isNoContent());
        verify(service).deleteById(3L);
    }
}
