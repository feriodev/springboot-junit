package com.pe.feriodev.code.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.pe.feriodev.code.model.Banco;
import com.pe.feriodev.code.model.Cuenta;

public class Datos {

	public static final Cuenta CUENTA_001 = new Cuenta(1L, "Andres", new BigDecimal("1000"));
	public static final Cuenta CUENTA_002 = new Cuenta(2L, "Fernando", new BigDecimal("2000"));
	public static final Banco BANCO_001 = new Banco(1L, "BCP", 0);
	
	public static Optional<Cuenta> crearCuenta001() {
		return Optional.of(new Cuenta(1L, "Andres", new BigDecimal("1000")));
	}
	
	public static Optional<Cuenta> crearCuenta002() {
		return Optional.of(new Cuenta(2L, "Fernando", new BigDecimal("2000")));
	}
	
	public static Optional<Banco> crearBanco001() {
		return Optional.of(new Banco(1L, "BCP", 0));
	}
}
