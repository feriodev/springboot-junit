package com.pe.feriodev.code.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pe.feriodev.code.model.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Long>{
		
	Optional<Cuenta> findByPersona(String persona);
	
	@Query("select c from Cuenta c where c.persona = ?1")
	Optional<Cuenta> buscarPorPersona(String persona);
}
