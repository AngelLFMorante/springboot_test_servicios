package org.afernandez.test.springboot.app.services;

import org.afernandez.test.springboot.app.models.Banco;
import org.afernandez.test.springboot.app.models.Cuenta;
import org.afernandez.test.springboot.app.repositories.IBancoRepository;
import org.afernandez.test.springboot.app.repositories.ICuentaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service //permite definir en el contenedor y nos permite luego llamarle con el autowired
public class CuentaServiceImpl implements ICuentaService{
	private ICuentaRepository cuentaRepository;
	private IBancoRepository bancoRepository;

	public CuentaServiceImpl(ICuentaRepository cuentaRepository, IBancoRepository bancoRepository) {
		this.cuentaRepository = cuentaRepository;
		this.bancoRepository = bancoRepository;
	}

	@Override
	public Cuenta findById(Long id) {

		return cuentaRepository.findById(id).orElseThrow();
	}

	@Override
	public int revisarTotalTransferencias(Long bancoId) {
		Banco banco = bancoRepository.findById(bancoId).orElseThrow();
		return banco.getTotalTransferencias();
	}

	@Override
	public BigDecimal revisarSaldo(Long cuentaId) {
		Cuenta cuenta = cuentaRepository.findById(cuentaId).orElseThrow();
		return cuenta.getSaldo();
	}

	@Override
	public void transferir(
			Long numCuentaOrigen,
			Long numCuentaDestino,
			BigDecimal monto,
			Long bancoId
	) {


		//ahora realizamos los cambios en la cuenta
		Cuenta cuentaOrigen = cuentaRepository.findById(numCuentaOrigen).orElseThrow();
		cuentaOrigen.debito(monto);
		cuentaRepository.save(cuentaOrigen);

		//ahora el credito para la cuenta destino
		Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino).orElseThrow();
		cuentaDestino.credito(monto);
		cuentaRepository.save(cuentaDestino);

		//Si todo sale bien se modifica el banco
		Banco banco = bancoRepository.findById(bancoId).orElseThrow();
		int totalTransferencias = banco.getTotalTransferencias();
		banco.setTotalTransferencias(++totalTransferencias); //pre-incremento
		//hay que actualizar
		bancoRepository.save(banco);
	}
}
