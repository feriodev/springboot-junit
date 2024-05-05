package com.pe.feriodev.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.feriodev.code.model.Banco;

public interface BancoRepository extends JpaRepository<Banco, Long>{}
