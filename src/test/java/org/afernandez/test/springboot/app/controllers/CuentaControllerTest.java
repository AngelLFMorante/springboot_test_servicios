package org.afernandez.test.springboot.app.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.afernandez.test.springboot.app.Datos;
import org.afernandez.test.springboot.app.models.Cuenta;
import org.afernandez.test.springboot.app.models.TransaccionDto;
import org.afernandez.test.springboot.app.services.ICuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@WebMvcTest(CuentaController.class) //vamos a implementar un test de controller de webMvcTest
class CuentaControllerTest {

	@Autowired
	private MockMvc mvc; //ya viene configurado,  es simulado, el request, servidor etc.

	@MockBean
	private ICuentaService cuentaService;

	ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}

	@Test
	void testDetalle() throws Exception {
		//No se llama al metodo real de la cuenta service
		//Given
		when(cuentaService.findById(1L)).thenReturn(Datos.crearCuenta001().orElseThrow());

		//Then
		//perfom de llevar a cabo, es como si fuera el When
		mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())//esperado la respuesta es como si fuera el Then
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.persona").value("Ángel"))
				.andExpect(jsonPath("$.saldo").value("1000"));

		verify(cuentaService).findById(1L);
	}

	@Test
	void testTransferir() throws Exception, JsonProcessingException {
		//Given
		TransaccionDto dto = new TransaccionDto();
		dto.setCuentaOrigenId(1L);
		dto.setCuentaDestinoId(2L);
		dto.setMonto(new BigDecimal("100"));
		dto.setBancoId(1L);

		//When
		mvc.perform(post("/api/cuentas/transferir")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
		//Then
		.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
				.andExpect(jsonPath("$.mensaje").value("Transferencia realizada con éxito"))
				.andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(1L));

	}

	@Test
	void testTransferir2() throws Exception, JsonProcessingException {
		//Given
		TransaccionDto dto = new TransaccionDto();
		dto.setCuentaOrigenId(1L);
		dto.setCuentaDestinoId(2L);
		dto.setMonto(new BigDecimal("100"));
		dto.setBancoId(1L);

		System.out.println(objectMapper.writeValueAsString(dto));

		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "OK");
		response.put("mensaje", "Transferencia realizada con éxito");
		response.put("transaccion", dto);

		System.out.println(objectMapper.writeValueAsString(response));

		//When
		mvc.perform(post("/api/cuentas/transferir")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
		//Then
		.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
				.andExpect(jsonPath("$.mensaje").value("Transferencia realizada con éxito"))
				.andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(1L))
				.andExpect(content().json(objectMapper.writeValueAsString(response)));

	}

	@Test
	void testListar() throws Exception {
		//Given
		List<Cuenta> cuentas = Arrays.asList(
				Datos.crearCuenta001().orElseThrow(),
				Datos.crearCuenta002().orElseThrow()
		);
		when(cuentaService.findAll()).thenReturn(cuentas);

		//When
		mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))

		//Then
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].persona").value("Ángel"))
				.andExpect(jsonPath("$[1].persona").value("Jhon"))
				.andExpect(jsonPath("$[0].saldo").value("1000"))
				.andExpect(jsonPath("$[1].saldo").value("2000"))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(content().json(objectMapper.writeValueAsString(cuentas)));
		verify(cuentaService).findAll();
	}


	@Test
	void testGuardar() throws Exception{
		//Given
		Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		//vamos a implementar la id del objeto cuenta
		when(cuentaService.save(any())).then(invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});

		//When
		mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(cuenta)))
				//Then
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.persona", is("Pepe")))
				.andExpect(jsonPath("$.saldo", is(3000)));

		verify(cuentaService).save(any());
	}
}