package org.afernandez.test.springboot.app.services;

import org.afernandez.test.springboot.app.models.Banco;
import org.afernandez.test.springboot.app.models.Cuenta;
import org.afernandez.test.springboot.app.repositories.IBancoRepository;
import org.afernandez.test.springboot.app.repositories.ICuentaRepository;

import java.math.BigDecimal;

public class CuentaServiceImpl implements ICuentaService{
	private ICuentaRepository cuentaRepository;
	private IBancoRepository bancoRepository;

	public CuentaServiceImpl(ICuentaRepository cuentaRepository, IBancoRepository bancoRepository) {
		this.cuentaRepository = cuentaRepository;
		this.bancoRepository = bancoRepository;
	}

	@Override
	public Cuenta findById(Long id) {
		return cuentaRepository.findById(id);
	}

	@Override
	public int revisarTotalTransferencias(Long bancoId) {
		Banco banco = bancoRepository.findById(bancoId);
		return banco.getTotalTransferencias();
	}

	@Override
	public BigDecimal revisarSaldo(Long cuentaId) {
		Cuenta cuenta = cuentaRepository.findById(cuentaId);
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
		Cuenta cuentaOrigen = cuentaRepository.findById(numCuentaOrigen);
		cuentaOrigen.debito(monto);
		cuentaRepository.update(cuentaOrigen);

		//ahora el credito para la cuenta destino
		Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino);
		cuentaDestino.credito(monto);
		cuentaRepository.update(cuentaDestino);

		//Si todo sale bien se modifica el banco
		Banco banco = bancoRepository.findById(bancoId);
		int totalTransferencias = banco.getTotalTransferencias();
		banco.setTotalTransferencias(++totalTransferencias); //pre-incremento
		//hay que actualizar
		bancoRepository.update(banco);
	}
}
