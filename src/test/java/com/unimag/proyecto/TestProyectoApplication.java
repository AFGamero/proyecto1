package com.unimag.proyecto;

import org.springframework.boot.SpringApplication;

public class TestProyectoApplication {

	public static void main(String[] args) {
		SpringApplication.from(ProyectoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
