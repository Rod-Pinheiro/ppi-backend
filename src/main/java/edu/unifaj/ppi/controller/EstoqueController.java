package edu.unifaj.ppi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.unifaj.ppi.dto.request.AjusteEstoqueRequest;
import edu.unifaj.ppi.dto.response.EstoqueResponse;
import edu.unifaj.ppi.service.EstoqueService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

	private final EstoqueService estoqueService;

	public EstoqueController(EstoqueService estoqueService) {
		this.estoqueService = estoqueService;
	}

	@GetMapping
	public List<EstoqueResponse> listar(@RequestParam(required = false) String hemocentroId) {
		return estoqueService.listar(hemocentroId);
	}

	@GetMapping("/criticos")
	public List<EstoqueResponse> criticos() {
		return estoqueService.listarCriticos();
	}

	@PutMapping("/{id}")
	public EstoqueResponse ajustarMinima(@PathVariable Long id, @Valid @RequestBody AjusteEstoqueRequest request) {
		return estoqueService.ajustarMinima(id, request.quantidadeMinima());
	}

}
