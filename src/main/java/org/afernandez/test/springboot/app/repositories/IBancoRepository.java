package org.afernandez.test.springboot.app.repositories;

import org.afernandez.test.springboot.app.models.Banco;

import java.util.List;

public interface IBancoRepository {
	List<Banco> findAll();
	Banco findById(Long id);
	void update(Banco banco);
}
