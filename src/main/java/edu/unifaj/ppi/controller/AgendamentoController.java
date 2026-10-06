package edu.unifaj.ppi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.unifaj.ppi.dto.request.AgendarRequest;
import edu.unifaj.ppi.dto.response.AgendamentoResponse;
import edu.unifaj.ppi.dto.response.QuantidadesResponse;
import edu.unifaj.ppi.service.AgendamentoService;
import edu.unifaj.ppi.service.ConsultaAgendamentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

	private final AgendamentoService agendamentoService;
	private final ConsultaAgendamentoService consultaAgendamentoService;

	public AgendamentoController(AgendamentoService agendamentoService,
			ConsultaAgendamentoService consultaAgendamentoService) {
		this.agendamentoService = agendamentoService;
		this.consultaAgendamentoService = consultaAgendamentoService;
	}

	@GetMapping
	public List<AgendamentoResponse> listar(@RequestParam String cpf) {
		return consultaAgendamentoService.listarDoDoador(cpf);
	}

	@GetMapping("/para-registro")
	public List<AgendamentoResponse> listarParaRegistro(@RequestParam String cpf) {
		return consultaAgendamentoService.listarParaRegistro(cpf);
	}

	@GetMapping("/quantidades")
	public QuantidadesResponse quantidades(@RequestParam String cpf) {
		return consultaAgendamentoService.quantidadesPorAgendamento(cpf);
	}

	@PostMapping
	public AgendamentoResponse agendar(@RequestParam String cpf, @Valid @RequestBody AgendarRequest request) {
		return agendamentoService.agendar(cpf, request);
	}

	@PostMapping("/{id}/cancelar")
	public AgendamentoResponse cancelar(@PathVariable Long id, @RequestParam String cpf) {
		return agendamentoService.cancelar(cpf, id);
	}

}