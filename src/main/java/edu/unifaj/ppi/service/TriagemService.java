package edu.unifaj.ppi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.unifaj.ppi.dto.request.RegistrarTriagemRequest;
import edu.unifaj.ppi.dto.response.TriagemResponse;
import edu.unifaj.ppi.exception.RecursoNaoEncontradoException;
import edu.unifaj.ppi.model.Doador;
import edu.unifaj.ppi.model.TriagemDoador;
import edu.unifaj.ppi.repository.DoadorRepository;
import edu.unifaj.ppi.repository.TriagemDoadorRepository;

@Service
public class TriagemService {

	private final TriagemDoadorRepository triagemRepository;
	private final DoadorRepository doadorRepository;

	public TriagemService(TriagemDoadorRepository triagemRepository, DoadorRepository doadorRepository) {
		this.triagemRepository = triagemRepository;
		this.doadorRepository = doadorRepository;
	}

	@Transactional
	public TriagemResponse registrar(String cpf, RegistrarTriagemRequest request) {
		Doador doador = buscarDoador(cpf);
		TriagemDoador triagem = new TriagemDoador(doador, request.data(), request.pesoKg(), request.pressaoArterial(),
				request.hemoglobina());
		triagem.setObservacoes(request.observacoes());
		return TriagemResponse.from(triagemRepository.save(triagem));
	}

	@Transactional(readOnly = true)
	public List<TriagemResponse> listar(String cpf) {
		buscarDoador(cpf);
		return triagemRepository.findByDoadorCpfOrderByDataDesc(normalizar(cpf)).stream().map(TriagemResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public TriagemResponse ultima(String cpf) {
		buscarDoador(cpf);
		return triagemRepository.findFirstByDoadorCpfOrderByDataDesc(normalizar(cpf)).map(TriagemResponse::from)
				.orElse(null);
	}

	private Doador buscarDoador(String cpf) {
		return doadorRepository.findByCpf(normalizar(cpf))
				.orElseThrow(() -> new RecursoNaoEncontradoException("Doador não encontrado"));
	}

	private String normalizar(String cpf) {
		return cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
	}

}
