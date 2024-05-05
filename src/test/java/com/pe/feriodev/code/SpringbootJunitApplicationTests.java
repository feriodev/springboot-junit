package com.pe.feriodev.code;

import static com.pe.feriodev.code.service.Datos.crearBanco001;
import static com.pe.feriodev.code.service.Datos.crearCuenta001;
import static com.pe.feriodev.code.service.Datos.crearCuenta002;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.pe.feriodev.code.exception.DineroException;
import com.pe.feriodev.code.model.Banco;
import com.pe.feriodev.code.model.Cuenta;
import com.pe.feriodev.code.repository.BancoRepository;
import com.pe.feriodev.code.repository.CuentaRepository;
import com.pe.feriodev.code.service.CuentaService;

@SpringBootTest
class SpringbootJunitApplicationTests {

	@MockBean
	CuentaRepository cuentaRepository;
	
	@MockBean
	BancoRepository bancoRepository;
	
	@Autowired
	CuentaService service;
	
	@BeforeEach
	void setUp() {}
	
	@Test
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco001());
		
		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);
		
		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());
		
		service.transferir(1L, 2L, new BigDecimal("100"), 1L);
		
		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);
		
		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());
		
		int total = service.revisarTotalTransferencia(1L);		
		assertEquals(1, total);
		
		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);
		
		verify(cuentaRepository, times(2)).save(any(Cuenta.class));
		
		verify(bancoRepository, times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));
		
		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();
	}

	@Test
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco001());
		
		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);
		
		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());
		
		assertThrows(DineroException.class, ()-> {
			service.transferir(1L, 2L, new BigDecimal("1200"), 1L);
		});
				
		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);
		
		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());
		
		int total = service.revisarTotalTransferencia(1L);		
		assertEquals(0, total);
		
		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(2)).findById(2L);
		
		verify(cuentaRepository, never()).save(any(Cuenta.class));
		
		verify(bancoRepository, times(1)).findById(1L);
		verify(bancoRepository, never()).save(any(Banco.class));
		
		verify(cuentaRepository, times(5)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();
	}
	
	@Test
	void contextLoads3() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		
		Cuenta cuenta1 = service.findById(1L);
		Cuenta cuenta2 = service.findById(1L);
		
		assertSame(cuenta1, cuenta2);
		
		assertEquals("Andres", cuenta1.getPersona());
		assertEquals("Andres", cuenta2.getPersona());
	}
	
	@Test
	void testFindAll() {
		List<Cuenta> datos = Arrays.asList(crearCuenta001().orElseThrow(), 
				crearCuenta002().orElseThrow());
		when(cuentaRepository.findAll()).thenReturn(datos);
		
		List<Cuenta> cuentas = service.findAll();
		
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
		assertTrue(cuentas.contains(crearCuenta001().orElseThrow()));
		
		verify(cuentaRepository).findAll();
		
	}
	
	@Test
	void testSave() {
		Cuenta cuenta = new Cuenta(null, "Luis", new BigDecimal("3000"));
		when(cuentaRepository.save(any())).then(invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});
		
		Cuenta save = service.save(cuenta);
		
		assertEquals(3L, save.getId());
		assertEquals("Luis", cuenta.getPersona());
		assertEquals("3000", save.getSaldo().toPlainString());
		
		verify(cuentaRepository).save(any());
	}
}
