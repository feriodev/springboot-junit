package com.pe.feriodev.code.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpStatus.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pe.feriodev.code.model.Cuenta;
import com.pe.feriodev.code.model.TransaccionRequest;
import com.pe.feriodev.code.service.CuentaService;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

	@Autowired
	private CuentaService cuentaService;

	@GetMapping("/{id}")
	public ResponseEntity<?> detalle(@PathVariable Long id) {		
		Cuenta cuenta = null;		
		try {
			cuenta = cuentaService.findById(id);
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}		
		return ResponseEntity.ok(cuenta);
	}
	
	@GetMapping("/")
	@ResponseStatus(OK)
	public List<Cuenta> listar() {
		return cuentaService.findAll();
	}

	@PostMapping("/transferir")
	public ResponseEntity<?> transferir(@RequestBody TransaccionRequest request){
		cuentaService.transferir(request.getCuentaOrigen(), request.getCuentaDestino(), 
				request.getMonto(), request.getBancoId());
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "Success");
		response.put("message", "Transferencia realizada con exito");
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/guardar")
	@ResponseStatus(CREATED)
	public Cuenta guardar(@RequestBody Cuenta cuenta){
		return cuentaService.save(cuenta);
	}
	
	@DeleteMapping("/eliminar/{id}")
	@ResponseStatus(NO_CONTENT)
	public void eliminar(@PathVariable Long id){
		cuentaService.delete(id);
	}
}
