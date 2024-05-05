package com.pe.feriodev.code.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.feriodev.code.model.Cuenta;
import com.pe.feriodev.code.model.TransaccionRequest;

@Tag("Test-RestTemplate")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CuentaControllerTestRestTemplateTest {

	@Autowired
	private TestRestTemplate client;
	
	private ObjectMapper mapper;

	@LocalServerPort
	private int puerto;
	
	@BeforeEach
	void setUp() throws Exception {
		mapper = new ObjectMapper();
	}

	@Test
	@Order(1)
	void testTransferir() throws JsonMappingException, JsonProcessingException {
		//Given
		TransaccionRequest request = TransaccionRequest.builder()
			.bancoId(1L)
			.cuentaOrigen(1L)
			.cuentaDestino(2L)
			.monto(new BigDecimal("100"))
			.build();
		
		ResponseEntity<String> response = 
				client.postForEntity(getUri("/api/cuentas/transferir"), request, String.class);

		String json = response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertNotNull(json);
		assertTrue(json.contains("Transferencia realizada con exito"));
		
		JsonNode jsonNode = mapper.readTree(json);
		assertEquals("Transferencia realizada con exito", jsonNode.path("message").asText());
		assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
		
		Map<String, Object> response2 = new HashMap<>();
		response2.put("date", LocalDate.now().toString());
		response2.put("status", "Success");
		response2.put("message", "Transferencia realizada con exito");
		
		assertEquals(mapper.writeValueAsString(response2),json);
	}

	@Test
	@Order(2)
	void testDetalle() throws Exception {
		ResponseEntity<Cuenta> response = 
				client.getForEntity(getUri("/api/cuentas/1"), Cuenta.class);
		
		Cuenta cuenta = response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		
		assertNotNull(cuenta);
		assertEquals("Andres", cuenta.getPersona());
		assertEquals(1L, cuenta.getId());
		assertEquals("900.00", cuenta.getSaldo().toPlainString());
		
	}
	
	@Test
	@Order(3)
	void testListar() throws Exception {
		ResponseEntity<Cuenta[]> response = 
				client.getForEntity(getUri("/api/cuentas/"), Cuenta[].class);
		
		List<Cuenta> lista = Arrays.asList(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		
		assertFalse(lista.isEmpty());
		assertEquals(2, lista.size());
		assertEquals(1L, lista.get(0).getId());
		assertEquals("Andres", lista.get(0).getPersona());
		assertEquals("900.00", lista.get(0).getSaldo().toPlainString());

		assertEquals(2L, lista.get(1).getId());
		assertEquals("Fernando", lista.get(1).getPersona());
		assertEquals("2100.00", lista.get(1).getSaldo().toPlainString());
		
		JsonNode json = mapper.readTree(mapper.writeValueAsString(lista));
		assertEquals(1L, json.get(0).path("id").asLong());
		assertEquals("Andres", json.get(0).path("persona").asText());
		assertEquals("900.0", json.get(0).path("saldo").asText());
		
	}
	
	@Test
	@Order(4)
	void testWebGuardar() throws Exception {
		
		//given
		Cuenta cuenta = new Cuenta(null, "Luis", new BigDecimal("3000"));
		
		//when
		ResponseEntity<Cuenta> response =  
			client.postForEntity(getUri("/api/cuentas/guardar"), cuenta, Cuenta.class);		
		Cuenta nuevo = response.getBody();
				
		//then
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertEquals(3L, nuevo.getId());
		assertEquals("Luis", nuevo.getPersona());
		assertEquals("3000", nuevo.getSaldo().toPlainString());
	}
	
	@Test
	@Order(5)
	void testEliminar() throws Exception {

		ResponseEntity<Cuenta[]> response = 
				client.getForEntity(getUri("/api/cuentas/"), Cuenta[].class);
		
		List<Cuenta> lista = Arrays.asList(response.getBody());
		assertEquals(3, lista.size());
		
		//client.delete(getUri("/api/cuentas/eliminar/3"));
		Map<String, Long> variable = new HashMap<>();
		variable.put("id", 3L);
		ResponseEntity<Void> exchange = 
				client.exchange(getUri("/api/cuentas/eliminar/{id}"), HttpMethod.DELETE, null, Void.class, variable);
		
		assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
		assertFalse(exchange.hasBody());
		
		response = client.getForEntity(getUri("/api/cuentas/"), Cuenta[].class);
		lista = Arrays.asList(response.getBody());
		assertEquals(2, lista.size());
		
		ResponseEntity<Cuenta> respuesta = 
				client.getForEntity(getUri("/api/cuentas/3"), Cuenta.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertFalse(respuesta.hasBody());
	}
	
	private String getUri(String uri) {
		return "http://localhost:"+ puerto + uri;
	}
}
