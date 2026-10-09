package edu.unifaj.ppi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.unifaj.ppi.dto.response.NotificacaoResponse;
import edu.unifaj.ppi.service.NotificacaoService;

@RestController
@RequestMapping("/api/doadores/{cpf}/notificacoes")
public class NotificacaoController {

	private final NotificacaoService notificacaoService;

	public NotificacaoController(NotificacaoService notificacaoService) {
		this.notificacaoService = notificacaoService;
	}

	@GetMapping
	public List<NotificacaoResponse> listar(@PathVariable String cpf) {
		return notificacaoService.listar(cpf);
	}

	@GetMapping("/nao-lidas")
	public Map<String, Long> naoLidas(@PathVariable String cpf) {
		return Map.of("total", notificacaoService.contarNaoLidas(cpf));
	}

	@PostMapping("/{id}/ler")
	public NotificacaoResponse marcarLida(@PathVariable String cpf, @PathVariable Long id) {
		return notificacaoService.marcarLida(cpf, id);
	}

}
