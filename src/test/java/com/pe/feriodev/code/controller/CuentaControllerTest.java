package com.pe.feriodev.code.controller;

import static com.pe.feriodev.code.service.Datos.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.feriodev.code.model.Cuenta;
import com.pe.feriodev.code.model.TransaccionRequest;
import com.pe.feriodev.code.service.CuentaService;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	CuentaService service;
	
	ObjectMapper mapper;
	
	@BeforeEach
	void setUp() {
		mapper = new ObjectMapper();
	}
	
	@Test
	void testDetalle() throws Exception {
		//given
		when(service.findById(1L)).thenReturn(crearCuenta001().orElseThrow());
		
		//when
		mvc.perform(get("/api/cuentas/1")
			.contentType(MediaType.APPLICATION_JSON))
		
		//then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.persona").value("Andres"))
			.andExpect(jsonPath("$.saldo").value("1000"));
		
		verify(service).findById(1L);
	}
	
	@Test
	void testTransferir() throws Exception {
		
		//Given
		TransaccionRequest request = TransaccionRequest.builder()
			.bancoId(1L)
			.cuentaOrigen(1L)
			.cuentaDestino(2L)
			.monto(new BigDecimal("100"))
			.build();
		
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "Success");
		response.put("message", "Transferencia realizada con exito");
		
		//when
		mvc.perform(post("/api/cuentas/transferir")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(request)))
		
		//then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
			.andExpect(jsonPath("$.message").value("Transferencia realizada con exito"))
			.andExpect(jsonPath("$.status").value("Success"))
			.andExpect(content().json(mapper.writeValueAsString(response)));
	}
	
	@Test
	void testListar() throws Exception {
		//given
		List<Cuenta> cuentas = Arrays.asList(crearCuenta001().orElseThrow(), 
				crearCuenta002().orElseThrow());
		when(service.findAll()).thenReturn(cuentas);
		
		//when
		mvc.perform(get("/api/cuentas/")
				.contentType(MediaType.APPLICATION_JSON))
		
		//then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].persona").value("Andres"))
			.andExpect(jsonPath("$[1].persona").value("Fernando"))
			.andExpect(jsonPath("$[0].saldo").value("1000"))
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(content().json(mapper.writeValueAsString(cuentas)));
		
		verify(service).findAll();
	}
	
	@Test
	void testGuardar() throws Exception {
		
		//Given
		Cuenta cuenta = new Cuenta(null, "Luis", new BigDecimal("3000"));
		when(service.save(any())).then(invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});

		//when
		mvc.perform(post("/api/cuentas/guardar")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(cuenta)))
		
		//then
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id", is(3)))
			.andExpect(jsonPath("$.persona", is("Luis")))
			.andExpect(jsonPath("$.saldo", is(3000)));
		
		verify(service).save(any());
	}
}
