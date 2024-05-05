package com.pe.feriodev.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.pe.feriodev.code.model.Cuenta;
import com.pe.feriodev.code.repository.CuentaRepository;

@DataJpaTest
class IntegrationJpaTest {

	@Autowired
	CuentaRepository cuentaRepository;
	
	@Test
	void testFindById(){
		Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
		assertTrue(cuenta.isPresent());
		assertEquals("Andres", cuenta.orElseThrow().getPersona());
	}
	
	@Test
	void testFindByPersona(){
		Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andres");
		assertTrue(cuenta.isPresent());
		assertEquals("Andres", cuenta.orElseThrow().getPersona());
	}
	
	@Test
	void testBuscarPersona(){
		Optional<Cuenta> cuenta = cuentaRepository.buscarPorPersona("Fernando");
		assertTrue(cuenta.isPresent());
		assertEquals("Fernando", cuenta.orElseThrow().getPersona());
		assertEquals("2000.00", cuenta.orElseThrow().getSaldo().toPlainString());
	}
	
	@Test
	void testFindByPersonaThrow(){
		Optional<Cuenta> cuenta = cuentaRepository.buscarPorPersona("Juan");
		assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
		assertFalse(cuenta.isPresent());
	}
	
	@Test
	void findAll() {
		List<Cuenta> cuentas = cuentaRepository.findAll();
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
	}
	
	@Test
	void testSave() {
		Cuenta cuentaNueva = new Cuenta(null, "Jose", new BigDecimal("3000"));
		Cuenta cuenta = cuentaRepository.save(cuentaNueva);
		
		cuentaRepository.findById(cuenta.getId()).orElseThrow();
		
		assertEquals("Jose", cuenta.getPersona());
		assertEquals("3000", cuenta.getSaldo().toPlainString());
	}
	
	@Test
	void testUpdate() {
		Cuenta cuentaNueva = new Cuenta(null, "Jose", new BigDecimal("3000"));
		Cuenta cuenta = cuentaRepository.save(cuentaNueva);
		
		cuentaRepository.findById(cuenta.getId()).orElseThrow();
		
		assertEquals("Jose", cuenta.getPersona());
		assertEquals("3000", cuenta.getSaldo().toPlainString());
		
		cuenta.setSaldo(new BigDecimal("3800"));
		Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
		
		assertEquals("Jose", cuentaActualizada.getPersona());
		assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());
	}
	
	@Test
	void testDelete() {
		Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();		
		assertEquals("Fernando", cuenta.getPersona());
		
		cuentaRepository.delete(cuenta);
		
		assertThrows(NoSuchElementException.class, ()->{
			cuentaRepository.findByPersona("Fernando").orElseThrow();
		});
	}	
}
