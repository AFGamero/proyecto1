package com.unimag.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface    PassengerProfile extends JpaRepository<PassengerRepository, Long> {

    //este no es necesario
}
