package com.pe.feriodev.code.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.feriodev.code.model.Banco;
import com.pe.feriodev.code.model.Cuenta;
import com.pe.feriodev.code.repository.BancoRepository;
import com.pe.feriodev.code.repository.CuentaRepository;
import com.pe.feriodev.code.service.CuentaService;

@Service
public class CuentaServiceImpl implements CuentaService {

	private CuentaRepository cuentaRepository;

	private BancoRepository bancoRepository;

	public CuentaServiceImpl(CuentaRepository cuentaRepository, BancoRepository bancoRepository) {
		this.cuentaRepository = cuentaRepository;
		this.bancoRepository = bancoRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Cuenta findById(Long id) {
		return cuentaRepository.findById(id).orElseThrow();
	}

	@Override
	@Transactional(readOnly = true)
	public int revisarTotalTransferencia(Long bancoId) {
		Banco banco = bancoRepository.findById(bancoId).orElseThrow();
		return banco.getTotalTransferencia();
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal revisarSaldo(Long cuentaId) {
		Cuenta cuenta = cuentaRepository.findById(cuentaId).orElseThrow();
		return cuenta.getSaldo();
	}

	@Override
	@Transactional
	public void transferir(Long cuentaOrigen, Long cuentaDestino, BigDecimal monto, Long bancoId) {
		Cuenta origen = cuentaRepository.findById(cuentaOrigen).orElseThrow();
		origen.debito(monto);
		cuentaRepository.save(origen);

		Cuenta destino = cuentaRepository.findById(cuentaDestino).orElseThrow();
		destino.credito(monto);
		cuentaRepository.save(destino);
		
		Banco banco = bancoRepository.findById(bancoId).orElseThrow();
		int totalTransferencia = banco.getTotalTransferencia();
		banco.setTotalTransferencia(++totalTransferencia);
		bancoRepository.save(banco);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Cuenta> findAll() {
		return cuentaRepository.findAll();
	}

	@Override
	@Transactional
	public Cuenta save(Cuenta cuenta) {
		return cuentaRepository.save(cuenta);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		cuentaRepository.deleteById(id);
	}

}
