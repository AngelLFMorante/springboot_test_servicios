package org.afernandez.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.afernandez.test.springboot.app.models.Cuenta;
import org.afernandez.test.springboot.app.models.TransaccionDto;
import org.hibernate.annotations.OnDelete;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerestRestTemplateTest {

	@Autowired
	private TestRestTemplate client;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}

	@Test
	@Order(1)
	void testTransferir() throws JsonProcessingException {
		TransaccionDto dto = new TransaccionDto();
		dto.setMonto(new BigDecimal("100"));
		dto.setCuentaOrigenId(1L);
		dto.setCuentaDestinoId(2L);
		dto.setBancoId(1L);

		ResponseEntity<String> response = client.postForEntity("/api/cuentas/transferir", dto, String.class);

		String json = response.getBody();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertNotNull(json);
		assertTrue(json.contains("Transferencia realizada con éxito"));

		//convertir nuestro json en JsonNode, para navegar desde el jsonPath
		JsonNode jsonNode = objectMapper.readTree(json);
		assertEquals("Transferencia realizada con éxito", jsonNode.path("mensaje").asText());
		assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
		assertEquals("100", jsonNode.path("transaccion").path("monto").asText());
		assertEquals(1L, jsonNode.path("transaccion").path("cuentaOrigenId").asLong());

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("date", LocalDate.now().toString());
		responseMap.put("status", "OK");
		responseMap.put("mensaje", "Transferencia realizada con éxito");
		responseMap.put("transaccion", dto);

		assertEquals(objectMapper.writeValueAsString(responseMap), json);

	}

	@Test
	@Order(2)
	void testDetalle() {
		ResponseEntity<Cuenta> respuesta = client.getForEntity("/api/cuentas/1", Cuenta.class);
		Cuenta cuenta = respuesta.getBody();
		assertNotNull(cuenta);
		assertEquals(HttpStatus.OK, respuesta.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());
		assertEquals(1L, cuenta.getId());
		assertEquals("Ángel", cuenta.getPersona());
		assertEquals("900.00", cuenta.getSaldo().toPlainString());

	}

	@Test
	@Order(3)
	void testListar() {
		ResponseEntity<Cuenta[]> respuesta = client.getForEntity("/api/cuentas", Cuenta[].class);
		assertNotNull(respuesta.getBody());
		List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());

		assertNotNull(cuentas);
		assertEquals(HttpStatus.OK, respuesta.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

		assertEquals(1L, cuentas.get(0).getId());
		assertEquals("Ángel", cuentas.get(0).getPersona());
		assertEquals("900.00", cuentas.get(0).getSaldo().toPlainString());

		assertEquals(2L, cuentas.get(1).getId());
		assertEquals("Jhon", cuentas.get(1).getPersona());
		assertEquals("2100.00", cuentas.get(1).getSaldo().toPlainString());

	}

	@Test
	@Order(4)
	void testGuardar() {
		Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("3000"));

		ResponseEntity<Cuenta> respuesta = client.postForEntity("/api/cuentas", cuenta, Cuenta.class);

		assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

		Cuenta cuentaCreada = respuesta.getBody();
		assertNotNull(cuentaCreada);
		assertEquals(3L, cuentaCreada.getId());
		assertEquals("Pepa", cuentaCreada.getPersona());
		assertEquals("3000", cuentaCreada.getSaldo().toPlainString());
	}

	@Test
	@Order(5)
	void testEliminar() {

		ResponseEntity<Cuenta[]> respuesta = client.getForEntity("/api/cuentas", Cuenta[].class);
		assertNotNull(respuesta.getBody());
		List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());
		assertEquals(3, cuentas.size());

		client.delete("/api/cuentas/3");

		respuesta = client.getForEntity("/api/cuentas", Cuenta[].class);
		assertNotNull(respuesta.getBody());
		cuentas = Arrays.asList(respuesta.getBody());
		assertEquals(2, cuentas.size());

		ResponseEntity<Cuenta> respuestaCuenta = client.getForEntity("/api/cuentas/3", Cuenta.class);
		assertEquals(HttpStatus.NOT_FOUND, respuestaCuenta.getStatusCode());
		assertFalse(respuestaCuenta.hasBody());

	}
}