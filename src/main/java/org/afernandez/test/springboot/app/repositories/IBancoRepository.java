package org.afernandez.test.springboot.app.repositories;

import org.afernandez.test.springboot.app.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IBancoRepository extends JpaRepository<Banco, Long> {
//	List<Banco> findAll();
//	Banco findById(Long id);
//	void update(Banco banco);

}
