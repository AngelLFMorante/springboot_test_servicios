package org.afernandez.test.springboot.app;

import org.afernandez.test.springboot.app.models.Banco;
import org.afernandez.test.springboot.app.models.Cuenta;

import java.math.BigDecimal;

public class Datos {
	//Hemos quitado las constantes y hemos creado metodos estaticos para las llamadas multiples en los test.
	//ya que pueden verse afectados los valores, al tener constantes y llamar de un metodo a otro puede no tener los valores iniciales.
	//al crear metodos siempres si o si, nos aseguramos que el valor siempre es el inicial.

	public static Cuenta crearCuenta001(){
		return  new Cuenta(1L, "Ángel", new BigDecimal("1000"));
	}
	public static Cuenta crearCuenta002(){
		return  new Cuenta(2L, "Jhon", new BigDecimal("2000"));
	}
	public static Banco crearBanco(){
		return new Banco(1L, "Banco de España", 0);
	}
}
