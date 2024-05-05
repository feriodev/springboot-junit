package com.pe.feriodev.code.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.feriodev.code.model.Cuenta;
import com.pe.feriodev.code.model.TransaccionRequest;

@Tag("Test-WebClient")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CuentaControllerWebClientTest {

	@Autowired
	private WebTestClient client;
	
	ObjectMapper mapper;
	
	@BeforeEach
	void setUp() {
		mapper = new ObjectMapper();
	}
	
	@Test
	@Order(1)
	void testWebTransferir() throws JsonProcessingException {
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
		client
			.post()
			.uri("/api/cuentas/transferir")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			
		//then
			.exchange()
			.expectStatus().isOk()			
			.expectBody()
				.consumeWith(res -> {
					try {
						JsonNode json = mapper.readTree(res.getResponseBody());
						assertEquals("Transferencia realizada con exito", json.path("message").asText());
						assertEquals(LocalDate.now().toString(), json.path("date").asText());
					} catch (IOException e) {
						e.printStackTrace();
					}
				})
				.jsonPath("$.message").isNotEmpty()
				.jsonPath("$.message").value(is("Transferencia realizada con exito"))
				.jsonPath("$.message").value(valor -> assertEquals("Transferencia realizada con exito", valor))
				.jsonPath("$.date").isEqualTo(LocalDate.now().toString())
				.json(mapper.writeValueAsString(response));
	}

	
	
	@Test
	@Order(2)
	void testWebDetalle() throws Exception {
		Cuenta origen = new Cuenta(1L, "Andres", new BigDecimal("900"));
		client.get().uri("/api/cuentas/1")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.persona").isEqualTo("Andres")
				.jsonPath("$.saldo").isEqualTo(900)
				.json(mapper.writeValueAsString(origen));
				
	}
	
	@Test
	@Order(3)
	void testWebDetalle2() throws Exception {
				
		client.get().uri("/api/cuentas/2")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody(Cuenta.class)
				.consumeWith(response -> {
					Cuenta cuenta = response.getResponseBody();
					assertEquals("Fernando", cuenta.getPersona());
					assertEquals("2100.00", cuenta.getSaldo().toPlainString());
				});
				
	}
	
	@Test
	@Order(4)
	void testWebListar() throws Exception {
		client.get().uri("/api/cuentas/")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$[0].persona").isEqualTo("Andres")
			.jsonPath("$[1].persona").isEqualTo("Fernando")
			.jsonPath("$").isArray()
			.jsonPath("$").value(hasSize(2));
	}
	
	@Test
	@Order(5)
	void testWebListar2() throws Exception {
		client.get().uri("/api/cuentas/")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Cuenta.class)
				.consumeWith(response -> {
					List<Cuenta> lista = response.getResponseBody();
					
					assertEquals(2, lista.size());
					assertFalse(lista.isEmpty());
					assertEquals("Fernando", lista.get(1).getPersona());
					assertEquals("2100.00", lista.get(1).getSaldo().toPlainString());
					assertEquals("Andres", lista.get(0).getPersona());
					assertEquals("900.00", lista.get(0).getSaldo().toPlainString());
				})
				.hasSize(2)
				.value(hasSize(2));
			
	}
	
	@Test
	@Order(6)
	void testWebGuardar() throws Exception {
		
		//given
		Cuenta cuenta = new Cuenta(null, "Luis", new BigDecimal("3000"));
		
		//when
		client
			.post()
			.uri("/api/cuentas/guardar")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(cuenta)
			
		//then
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.persona").value(is("Luis"))
			.jsonPath("$.id").isEqualTo(3L);
	}
	
	@Test
	@Order(7)
	void testWebEliminar() throws Exception {

		client.get().uri("/api/cuentas/")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Cuenta.class)
			.hasSize(3);
		
		client.delete().uri("/api/cuentas/eliminar/2")
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();
			
		client.get().uri("/api/cuentas/")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Cuenta.class)
			.hasSize(2);
		
		client.get().uri("/api/cuentas/2")
			.exchange()
			//.expectStatus().is5xxServerError();
			.expectStatus().isNotFound()
				.expectBody().isEmpty();
	}
}
