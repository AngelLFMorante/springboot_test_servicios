package org.afernandez.test.springboot.app.models;

import javax.persistence.*;

@Entity
@Table(name = "bancos")
public class Banco {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	@Column(name = "total_transferencias")
	private int totalTransferencias;

	//Cuando hibernate y JPA, siempre es necesario crear un constructor vacio
	//para todas las clases entities mapeadas a tablas para que pueda crear y manejar el contexto de persistencia
	public Banco() {
	}

	public Banco(Long id, String nombre, int totalTransferencias) {
		this.id = id;
		this.nombre = nombre;
		this.totalTransferencias = totalTransferencias;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getTotalTransferencias() {
		return totalTransferencias;
	}

	public void setTotalTransferencias(int totalTransferencias) {
		this.totalTransferencias = totalTransferencias;
	}


}
