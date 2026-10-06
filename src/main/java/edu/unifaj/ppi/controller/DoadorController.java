package edu.unifaj.ppi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.unifaj.ppi.dto.request.AtualizarPerfilRequest;
import edu.unifaj.ppi.dto.request.CadastroDoadorRequest;
import edu.unifaj.ppi.dto.request.LoginRequest;
import edu.unifaj.ppi.dto.response.DoadorResponse;
import edu.unifaj.ppi.dto.response.PerfilResponse;
import edu.unifaj.ppi.service.DoadorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doadores")
public class DoadorController {

	private final DoadorService doadorService;

	public DoadorController(DoadorService doadorService) {
		this.doadorService = doadorService;
	}

	@PostMapping
	public ResponseEntity<DoadorResponse> cadastrar(@Valid @RequestBody CadastroDoadorRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(doadorService.cadastrar(request));
	}

	@PostMapping("/login")
	public DoadorResponse login(@Valid @RequestBody LoginRequest request) {
		return doadorService.login(request.email(), request.senha());
	}

	@GetMapping("/{cpf}")
	public PerfilResponse buscarPerfil(@PathVariable String cpf) {
		return doadorService.buscarPerfil(cpf);
	}

	@PutMapping("/{cpf}")
	public PerfilResponse atualizarPerfil(@PathVariable String cpf,
			@Valid @RequestBody AtualizarPerfilRequest request) {
		return doadorService.atualizarPerfil(cpf, request);
	}

}