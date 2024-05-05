package com.pe.feriodev.code.service;

import java.math.BigDecimal;
import java.util.List;

import com.pe.feriodev.code.model.Cuenta;

public interface CuentaService {

	Cuenta findById(Long id);
	
	int revisarTotalTransferencia(Long bancoId);
	
	BigDecimal revisarSaldo(Long cuentaId);
	
	void transferir(Long cuentaOrigen, Long cuentaDestino, BigDecimal monto, Long bancoId);
	
	List<Cuenta> findAll();
	
	Cuenta save(Cuenta cuenta);
	
	void delete(Long id);
}
