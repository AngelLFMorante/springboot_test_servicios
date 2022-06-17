package org.afernandez.test.springboot.app.repositories;

import org.afernandez.test.springboot.app.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

//para que funcione con jpa hay que extender
//Ahora es un componente de Spring un Bean  al heredar de JpaRepository
public interface ICuentaRepository extends JpaRepository<Cuenta, Long> {
	//Estos metodo eran para cuando no heredaba de spring de JPA
	//	List<Cuenta> findAll();
	//	Cuenta findById(Long id);
	//	void update(Cuenta cuenta);

	//metodo personalizado
//	Cuenta findByPersona(String persona);
	//Otra alternativa es con @Query, sobreescribe el nombre del metodo se le puede llamar como quisiera
	@Query("select c from Cuenta c where c.persona=?1") //no es una consulta nativa
	Optional<Cuenta> findByPersona(String persona); //es mas robusto al tener Optional para cazar las excepciones, esta presente, etc.



}
