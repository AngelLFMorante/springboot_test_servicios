package org.afernandez.test.springboot.app;

import org.afernandez.test.springboot.app.models.Banco;
import org.afernandez.test.springboot.app.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
	//Hemos quitado las constantes y hemos creado metodos estaticos para las llamadas multiples en los test.
	//ya que pueden verse afectados los valores, al tener constantes y llamar de un metodo a otro puede no tener los valores iniciales.
	//al crear metodos siempres si o si, nos aseguramos que el valor siempre es el inicial.

	//hemos cambiado el return ya que nos devuelve un optional y no el objeto cuenta
	//el Optional.of es para decir que es un optional de ( Clase Cuenta) etc
	public static Optional<Cuenta> crearCuenta001(){
		return  Optional.of(new Cuenta(1L, "Ángel", new BigDecimal("1000")));
	}
	public static Optional<Cuenta> crearCuenta002(){
		return  Optional.of(new Cuenta(2L, "Jhon", new BigDecimal("2000")));
	}
	public static Optional<Banco> crearBanco(){
		return Optional.of(new Banco(1L, "Banco de España", 0));
	}
}
