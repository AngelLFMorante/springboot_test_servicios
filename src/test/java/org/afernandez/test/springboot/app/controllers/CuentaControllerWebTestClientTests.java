package org.afernandez.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.afernandez.test.springboot.app.models.Cuenta;
import org.afernandez.test.springboot.app.models.TransaccionDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //ordenamos con anotaciones
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //esto es para que nos de un puerto aleatorio para cuando hagamos las pruebas.Levanta un servidor real
class CuentaControllerWebTestClientTests {

	/*
	Para poder probar este test, debemos de levantar la aplicacion para que pueda funcionar y consumir nuestro apiRest
	 */


	@Autowired
	private WebTestClient client;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}

	@Test
	@Order(1)
	void testTransferir() throws JsonProcessingException {
		TransaccionDto dto = new TransaccionDto();
		dto.setCuentaOrigenId(1L);
		dto.setCuentaDestinoId(2L);
		dto.setBancoId(1L);
		dto.setMonto(new BigDecimal("100"));

		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "OK");
		response.put("mensaje", "Transferencia realizada con éxito");
		response.put("transaccion", dto);

		//when
		client.post()
				.uri("/api/cuentas/transferir") //No hace falta poner http://localhost:8080 por que estamos haciendolo en nuestro local.
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(dto)
				//then
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.consumeWith( respuesta ->{
					try {
						JsonNode json =  objectMapper.readTree(respuesta.getResponseBody());
						assertEquals("Transferencia realizada con éxito", json.path("mensaje").asText());
						assertEquals(1L, json.path("transaccion").path("cuentaOrigenId").asLong());
						assertEquals(LocalDate.now().toString(), json.path("date").asText());
						assertEquals("100", json.path("transaccion").path("monto").asText());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}) //se emite el response, es lo mismo que tenemos abajo pero con expresion lambda.
				.jsonPath("$.mensaje").isNotEmpty()
				.jsonPath("$.mensaje").value(is("Transferencia realizada con éxito"))
				.jsonPath("$.mensaje").value(valor -> {
					assertEquals("Transferencia realizada con éxito", valor);
				})
				.jsonPath("$.mensaje").isEqualTo("Transferencia realizada con éxito")
				.jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
				.jsonPath("$.date").isEqualTo(LocalDate.now().toString())
				.json(objectMapper.writeValueAsString(response));

	}
	/*
	Cuando hay test que pueden variar nuestros metodos y se ejecuten antes o despues y varien nuestro resultado lo hacemos con @TestMethodOrder
	 */
	@Test
	@Order(2)
	void testDetalle() {
		client.get().uri("/api/cuentas/1").exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.persona").isEqualTo("Ángel")
				.jsonPath("$.saldo").isEqualTo(900); //se transfiere los datos y tenemos que tenerlo en cuenta
	}

	@Test
	@Order(3)
	void testDetalle2() {
		client.get().uri("/api/cuentas/2").exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Cuenta.class)//se puede mapear por que son los mismos atributos del json que con el object
				.consumeWith( response -> {
					Cuenta cuenta = response.getResponseBody(); //se convierte de forma automatica
					assertNotNull(cuenta);
					assertEquals("Jhon", cuenta.getPersona());
					assertEquals("2100.00", cuenta.getSaldo().toPlainString());
				});
	}

	@Test
	@Order(4)
	void testListar() {
		client.get().uri("/api/cuentas").exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$[0].persona").isEqualTo("Ángel")
				.jsonPath("$[0].id").isEqualTo(1)
				.jsonPath("$[0].saldo").isEqualTo(900)
				.jsonPath("$[1].persona").isEqualTo("Jhon")
				.jsonPath("$[1].id").isEqualTo(2)
				.jsonPath("$[1].saldo").isEqualTo(2100)
				.jsonPath("$").isArray()
				.jsonPath("$").value(hasSize(2));
	}

	@Test
	@Order(5)
	void testListar2() {
		client.get().uri("/api/cuentas").exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Cuenta.class)//esperamo el body de tipo Cuenta por eso no hay que parsearlo luiego en el response a JSON
				.consumeWith(response ->{
					List<Cuenta> cuentas = response.getResponseBody();

					assertNotNull(cuentas);
					assertEquals(2,cuentas.size());

					assertEquals(1L, cuentas.get(0).getId());
					assertEquals("Ángel", cuentas.get(0).getPersona());
					assertEquals(900, cuentas.get(0).getSaldo().intValue());

					assertEquals(2L, cuentas.get(0).getId());
					assertEquals("Jhon", cuentas.get(0).getPersona());
					assertEquals(2100.0, cuentas.get(0).getSaldo().intValue());

				});
	}

	@Test
	@Order(6)
	void testGuardar() {
		//Given
		Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));

		//When
		client.post().uri("/api/cuentas")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(cuenta)
				.exchange()
				//Then
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.id").isEqualTo(3)
				.jsonPath("$.persona").isEqualTo("Pepe")
				.jsonPath("$.saldo").isEqualTo("3000");

	}

	@Test
	@Order(7)
	void testGuardar2() {
		//Given
		Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("3500"));

		//When
		client.post().uri("/api/cuentas")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(cuenta)
				.exchange()
				//Then
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Cuenta.class)
				.consumeWith(response -> {
					Cuenta c = response.getResponseBody();
					assertNotNull(c);
					assertEquals(4L, c.getId());
					assertEquals("Pepa", c.getPersona());
					assertEquals("3500", c.getSaldo().toPlainString());
				});

	}

	@Test
	@Order(8)
	void testEliminar() {
		//elementos que tiene antes de eliminar
		client.get().uri("/api/cuentas").exchange()
						.expectStatus().isOk()
						.expectBodyList(Cuenta.class)
								.hasSize(4);

		client.delete().uri("/api/cuentas/3")
				.exchange()
				.expectStatus().isNoContent()
				.expectBody().isEmpty();

		//comprobamos que sean 3
		client.get().uri("/api/cuentas").exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Cuenta.class)
				.hasSize(3);

		//comprobamos que si se ah eliminado saltaria un error
		client.get().uri("/api/cuentas/3").exchange()
//				.expectStatus().is5xxServerError(); queremos que sea notfound
				.expectStatus().isNotFound()
				.expectBody().isEmpty();
	}
}