package com.unimag.repositories;

import com.unimag.entidades.Airline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AirlineRepositoryTest extends AbstractRepository {

    @Autowired
    AirlineRepository airlineRepository;

    @Test
    @DisplayName("findByCode should return an airline when it exists")
    void shouldFindByCode(){
        //given
        airlineRepository.save(Airline.builder().code("AV").name("avianca").build());
        airlineRepository.save(Airline.builder().code("AS").name("aereosucre").build());

        //when
        Optional<Airline> airline = airlineRepository.findByCode("AV");
        //then
        assertTrue(airline.isPresent());


    }



}