package org.afernandez.test.springboot.app.repositories;

import org.afernandez.test.springboot.app.models.Cuenta;

import java.util.List;

public interface ICuentaRepository {
	List<Cuenta> findAll();
	Cuenta findById(Long id);
	void update(Cuenta cuenta);
}
