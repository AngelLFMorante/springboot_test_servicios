package org.afernandez.test.springboot.app.controllers;

import static  org.springframework.http.HttpStatus.*;

import org.afernandez.test.springboot.app.models.Cuenta;
import org.afernandez.test.springboot.app.models.TransaccionDto;
import org.afernandez.test.springboot.app.services.ICuentaService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller Cambiamos a RestController por que vamos a implementar una api rest y no podemos con controller
@RestController //cada metodo devuelve al cliente de forma automatica en un JSON y va en el ResponseBody
@RequestMapping("/api/cuentas")//Para darle una ruta base al controlador
public class CuentaController {

	@Autowired
	private ICuentaService cuentaService;

	@GetMapping
	@ResponseStatus(OK)
	public List<Cuenta> listar(){
		return cuentaService.findAll();
	}

	@PostMapping
	@ResponseStatus(CREATED)
	public Cuenta guardar(@RequestBody Cuenta cuenta){
		return cuentaService.save(cuenta);
	}

	@GetMapping("/{id}") // tenerlo entre llaves es el path variable, para poder pasar argumentos en la url
	@ResponseStatus(OK)
	public Cuenta detalle(@PathVariable(name = "id") Long id){ //corresponde a la id de la url, si se llama igual se puede omitir.
		return cuentaService.findById(id);
	}

	@PostMapping("/transferir")
	public ResponseEntity<?> transferir(@RequestBody TransaccionDto dto){
		cuentaService.transferir(
				dto.getCuentaOrigenId(),
				dto.getCuentaDestinoId(),
				dto.getMonto(),
				dto.getBancoId()
		);

		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "OK");
		response.put("mensaje", "Transferencia realizada con éxito");
		response.put("transaccion", dto);

		//tenemos que devolver tipo Json
		return ResponseEntity.ok(response);
	}
}
