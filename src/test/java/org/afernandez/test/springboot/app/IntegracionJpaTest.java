package org.afernandez.test.springboot.app;

import org.afernandez.test.springboot.app.models.Cuenta;
import org.afernandez.test.springboot.app.repositories.ICuentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest //es importante para el test con base de datos, para que habilite  los repositorios de Spring etc.
public class IntegracionJpaTest {

	@Autowired
	ICuentaRepository cuentaRepository;
	
	@Test
	void testFindById(){
		Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
		assertTrue(cuenta.isPresent());
		assertEquals("Ángel", cuenta.orElseThrow().getPersona());
	}

	@Test
	void testFindByPersona(){
		Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Ángel");
		assertTrue(cuenta.isPresent());
		assertEquals("Ángel", cuenta.orElseThrow().getPersona());
		assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
	}

	@Test
	void testFindByPersonaThrowException(){
		Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Pedro");
		//comprobamos si captura la excepcion,
		//quitamos la funcion flecha y hacemos por referencia ::
		assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
		assertFalse(cuenta.isPresent());
	}

	@Test
	void testFindAll() {
		List<Cuenta> cuentas = cuentaRepository.findAll();
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
	}

	@Test
	void testSave() {
		//Given
		Cuenta cuentaPepe = new Cuenta (null, "Pepe", new BigDecimal("3000"));
		cuentaRepository.save(cuentaPepe);

		//when
		Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
		//para buscar con un id que acabamos de guardar: Pero el save debe ser un objeto de Cuenta: Cuenta save = cuentaRepository.save(cuentaPepe);
//		Cuenta cuenta = cuentaRepository.findByPersona(save.getId()).orElseThrow();


		//Then
		assertEquals("Pepe", cuenta.getPersona());
		assertEquals("3000", cuenta.getSaldo().toPlainString());
		assertEquals(3, cuenta.getId());
	}

	@Test
	void testUpdate() {
		//Given
		Cuenta cuentaPepe = new Cuenta (null, "Pepe", new BigDecimal("3000"));


		//when
		Cuenta cuenta = cuentaRepository.save(cuentaPepe);
		//para buscar con un id que acabamos de guardar: Pero el save debe ser un objeto de Cuenta: Cuenta save = cuentaRepository.save(cuentaPepe);
//		Cuenta cuenta = cuentaRepository.findByPersona(save.getId()).orElseThrow();


		//Then
		assertEquals("Pepe", cuenta.getPersona());
		assertEquals("3000", cuenta.getSaldo().toPlainString());


		cuenta.setSaldo(new BigDecimal("3800"));
		Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

		assertEquals("Pepe", cuentaActualizada.getPersona());
		assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());

	}

	@Test
	void testDelete() {
		Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
		assertEquals("Jhon", cuenta.getPersona());

		cuentaRepository.delete(cuenta);

		assertThrows(NoSuchElementException.class, ()->{
			cuentaRepository.findByPersona("Jhon").orElseThrow();
		});
	}
}
