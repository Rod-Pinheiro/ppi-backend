package edu.unifaj.ppi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.unifaj.ppi.dto.response.HemocentroResponse;
import edu.unifaj.ppi.service.HemocentroService;

@RestController
@RequestMapping("/api/hemocentros")
public class HemocentroController {

	private final HemocentroService hemocentroService;

	public HemocentroController(HemocentroService hemocentroService) {
		this.hemocentroService = hemocentroService;
	}

	@GetMapping
	public List<HemocentroResponse> listar() {
		return hemocentroService.listar();
	}

}