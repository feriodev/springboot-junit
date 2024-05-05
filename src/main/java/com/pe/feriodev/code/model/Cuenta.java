package com.pe.feriodev.code.model;

import java.math.BigDecimal;
import java.util.Objects;

import com.pe.feriodev.code.exception.DineroException;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cuentas")
public class Cuenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String persona;
	private BigDecimal saldo;
	
	public void debito(BigDecimal monto) {
		BigDecimal nuevoSaldo = this.saldo.subtract(monto);
		if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
			throw new DineroException("Dinero insuficiente");
		}
		this.saldo = nuevoSaldo;
	}
	
	public void credito(BigDecimal monto) {
		this.saldo = this.saldo.add(monto);
	}
		
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Cuenta cuenta = (Cuenta)obj;
		return Objects.equals(id, cuenta.id) && 
				Objects.equals(persona, cuenta.persona) &&
				Objects.equals(saldo, cuenta.saldo);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, persona, saldo);
	}
}
