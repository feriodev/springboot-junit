package com.pe.feriodev.code.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransaccionRequest {

	private Long cuentaOrigen;
	private Long cuentaDestino;
	private BigDecimal monto;
	private Long bancoId;
}
