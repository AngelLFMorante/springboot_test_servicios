package org.afernandez.test.springboot.app.models;

import org.afernandez.test.springboot.app.exceptions.DineroInsuficienteException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

//persistencia de hibernate
@Entity
@Table(name = "cuentas")//nombre de la tabla que va a ser mapeada
public class Cuenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String persona;
	private BigDecimal saldo;

	public Cuenta() {
	}

	public Cuenta(Long id, String persona, BigDecimal saldo) {
		this.id = id;
		this.persona = persona;
		this.saldo = saldo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPersona() {
		return persona;
	}

	public void setPersona(String persona) {
		this.persona = persona;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	/*
	Monto : refiere al valor del dinero en el futuro, es el capital más los intereses generados,
	igual se le puede llamar capital futuro o valor acumulado.
	Lo que sigue siendo una "suma" o "total"
	 */
	//Debito es cuando nos resta un dinero de nuestra cuenta
	public void debito(BigDecimal monto){
		BigDecimal nuevoSaldo = this.saldo.subtract(monto);
		if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
			throw new DineroInsuficienteException("Dinero insuficiente en la cuenta.");
		}
		this.saldo = nuevoSaldo;
	}

	//Credito es cuando nos dan dinero desde otra cuenta
	public void credito(BigDecimal monto){
		this.saldo = this.saldo.add(monto);
	}

	/*
	Si sobreescribe el método equals() es recomendable sobreescribir también el método hashCode()
	para conservar el contrato entre ambos métodos: dos objetos iguales deben retornar el mismo valor de hash.
	El método equals() no llama al método hashCode() para determinar la igualdad de dos objetos.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, persona, saldo);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		Cuenta cuenta = (Cuenta) obj;
		return Objects.equals(id, cuenta.id) && Objects.equals(persona, cuenta.persona) && Objects.equals(saldo, cuenta.saldo);
	}
}
