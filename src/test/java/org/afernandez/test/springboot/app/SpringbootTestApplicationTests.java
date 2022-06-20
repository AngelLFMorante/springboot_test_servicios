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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


@SpringBootTest //ya viene con @ExtendWitch
class SpringbootTestApplicationTests {

	@MockBean //Esta forma es de Spring y @Mock es de mockito. es lo mismo.
	ICuentaRepository cuentaRepository;
	@MockBean
	IBancoRepository bancoRepository;

	//@InjectMocks //con inyectMock necesitamos la implementacion y no la interface
			//como hemos anotado con el bean @Service ahora quitamos injectMockde mockito y ponemos autowired de spring
	@Autowired //con spring nos permite tener la interface y no poner el Impl
	ICuentaService service;

	@BeforeEach
	void setUp(){
//		cuentaRepository = mock(ICuentaRepository.class);
//		bancoRepository = mock(IBancoRepository.class);
//		service = new CuentaServiceImpl(cuentaRepository, bancoRepository);
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


		verify(cuentaRepository, times(2)).save(any(Cuenta.class));

		//2 por que hacemos otra llamada a revisarTotalTransferencias
		verify(bancoRepository, times(2)).findById(1L);
		//el times por defecto es 1
		verify(bancoRepository).save(any(Banco.class));

		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();

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
		verify(cuentaRepository, never()).save(any(Cuenta.class));


		verify(bancoRepository, times(1)).findById(1L);

		verify(bancoRepository, never()).save(any(Banco.class));

		verify(cuentaRepository, times(5)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();

	}

	@Test
	void contextLoads3() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		//dos cuentas atraves del services y mirar que esas dos instancias son iguales
		Cuenta cuenta1 = service.findById(1L);
		Cuenta cuenta2 = service.findById(1L);

		assertSame(cuenta1, cuenta2);
		assertTrue(cuenta1 == cuenta2);
		assertEquals("Ángel", cuenta1.getPersona());
		assertEquals("Ángel", cuenta2.getPersona());

		verify(cuentaRepository, times(2)).findById(1L);
	}

	@Test
	void testFindAll() {
		//Given
		List<Cuenta> datos = Arrays.asList(
				Datos.crearCuenta001().orElseThrow(),
				Datos.crearCuenta002().orElseThrow()
		);
		when(cuentaRepository.findAll()).thenReturn(datos);

		//when
		List<Cuenta> cuentas = service.findAll();

		//then
		assertFalse(cuentas.isEmpty());
		assertEquals(2,cuentas.size());
		assertTrue(cuentas.contains(Datos.crearCuenta002().orElseThrow()));

		verify(cuentaRepository).findAll();
	}

	@Test
	void testSave() {
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaRepository.save(any())).then(invation -> {
			Cuenta c = invation.getArgument(0);
			c.setId(3L);
			return c;
		});

		Cuenta cuenta = service.save(cuentaPepe);

		assertEquals("Pepe", cuenta.getPersona());
		assertEquals(3, cuenta.getId());
		assertEquals("3000", cuenta.getSaldo().toPlainString());

		verify(cuentaRepository).save(any());
	}
}
