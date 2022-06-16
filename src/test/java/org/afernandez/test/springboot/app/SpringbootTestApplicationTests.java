package org.afernandez.test.springboot.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.afernandez.test.springboot.app.exceptions.DineroInsuficienteException;
import org.afernandez.test.springboot.app.models.Banco;
import org.afernandez.test.springboot.app.models.Cuenta;
import org.afernandez.test.springboot.app.repositories.IBancoRepository;
import org.afernandez.test.springboot.app.repositories.ICuentaRepository;
import org.afernandez.test.springboot.app.services.CuentaServiceImpl;
import org.afernandez.test.springboot.app.services.ICuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;


@SpringBootTest //ya viene con @ExtendWitch
class SpringbootTestApplicationTests {

	ICuentaRepository cuentaRepository;
	IBancoRepository bancoRepository;

	ICuentaService service;

	@BeforeEach
	void setUp(){
		cuentaRepository = mock(ICuentaRepository.class);
		bancoRepository = mock(IBancoRepository.class);
		service = new CuentaServiceImpl(cuentaRepository, bancoRepository);
	}

	@Test
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);//Aqui se invoca el id 1
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		service.transferir(1L, 2L, new BigDecimal("100"), 1L); //aqui tambien se invoca el id 1

		saldoOrigen = service.revisarSaldo(1L); //aqui tambien se invoca id 1
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());

		//INVOCACIONES:
		int totalTransferencias = service.revisarTotalTransferencias(1L);
		assertEquals(1, totalTransferencias);
		//se invoca 3 veces 1 por cada uno..
		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);

		verify(cuentaRepository, times(2)).update(any(Cuenta.class));

		//2 por que hacemos otra llamada a revisarTotalTransferencias
		verify(bancoRepository, times(2)).findById(1L);
		//el times por defecto es 1
		verify(bancoRepository).update(any(Banco.class));

	}


	@Test
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class, ()->{
			service.transferir(1L, 2L, new BigDecimal("1200"), 1L);
		});

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());


		int totalTransferencias = service.revisarTotalTransferencias(1L);
		assertEquals(0, totalTransferencias);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(2)).findById(2L);

		//al realizar uina excepcion no se va a realizar luego las siguientes acciones con lo que update no se realizaria.
		verify(cuentaRepository, never()).update(any(Cuenta.class));


		verify(bancoRepository, times(1)).findById(1L);

		verify(bancoRepository, never()).update(any(Banco.class));

	}


}
