package edu.unifaj.ppi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.unifaj.ppi.dto.request.RegistrarTriagemRequest;
import edu.unifaj.ppi.dto.response.TriagemResponse;
import edu.unifaj.ppi.service.TriagemService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doadores/{cpf}/triagens")
public class TriagemController {

	private final TriagemService triagemService;

	public TriagemController(TriagemService triagemService) {
		this.triagemService = triagemService;
	}

	@PostMapping
	public TriagemResponse registrar(@PathVariable String cpf, @Valid @RequestBody RegistrarTriagemRequest request) {
		return triagemService.registrar(cpf, request);
	}

	@GetMapping
	public List<TriagemResponse> listar(@PathVariable String cpf) {
		return triagemService.listar(cpf);
	}

	@GetMapping("/ultima")
	public TriagemResponse ultima(@PathVariable String cpf) {
		return triagemService.ultima(cpf);
	}

}
