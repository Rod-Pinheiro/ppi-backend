package edu.unifaj.ppi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.unifaj.ppi.dto.request.RegistrarColetaRequest;
import edu.unifaj.ppi.dto.response.BolsaSangueResponse;
import edu.unifaj.ppi.service.ColetaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bolsas-sangue")
public class ColetaController {

	private final ColetaService coletaService;

	public ColetaController(ColetaService coletaService) {
		this.coletaService = coletaService;
	}

	@GetMapping
	public List<BolsaSangueResponse> historico(@RequestParam String cpf) {
		return coletaService.listarHistorico(cpf);
	}

	@PostMapping("/agendamentos/{agendamentoId}/coletas")
	public BolsaSangueResponse registrar(@PathVariable Long agendamentoId, @RequestParam String cpf,
			@Valid @RequestBody RegistrarColetaRequest request) {
		return coletaService.registrarColeta(cpf, agendamentoId, request);
	}

}