package edu.unifaj.ppi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.unifaj.ppi.dto.response.HemocentroResponse;
import edu.unifaj.ppi.model.Hemocentro;
import edu.unifaj.ppi.repository.HemocentroRepository;

@Service
public class HemocentroService {

	private final HemocentroRepository hemocentroRepository;

	public HemocentroService(HemocentroRepository hemocentroRepository) {
		this.hemocentroRepository = hemocentroRepository;
	}

	@Transactional(readOnly = true)
	public List<HemocentroResponse> listar() {
		return hemocentroRepository.findAllByOrderByNomeAsc().stream().map(HemocentroResponse::from).toList();
	}

}