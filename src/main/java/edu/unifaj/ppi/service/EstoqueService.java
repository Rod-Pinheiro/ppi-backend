package edu.unifaj.ppi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.unifaj.ppi.dto.response.EstoqueResponse;
import edu.unifaj.ppi.exception.RecursoNaoEncontradoException;
import edu.unifaj.ppi.model.EstoqueSangue;
import edu.unifaj.ppi.model.Hemocentro;
import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;
import edu.unifaj.ppi.repository.EstoqueSangueRepository;

/**
 * Mantem o saldo de bolsas. A entrada e disparada pela coleta; a leitura e o que
 * a tela de estoque consome.
 */
@Service
public class EstoqueService {

	private final EstoqueSangueRepository estoqueRepository;

	public EstoqueService(EstoqueSangueRepository estoqueRepository) {
		this.estoqueRepository = estoqueRepository;
	}

	/**
	 * Soma as bolsas coletadas ao saldo do hemocentro. Cria a linha na primeira
	 * coleta daquela combinacao de tipo e hemocentro.
	 */
	@Transactional
	public void registrarEntrada(Hemocentro hemocentro, TipoSanguineo tipoSanguineo, FatorRh fatorRh, int bolsas) {
		EstoqueSangue estoque = estoqueRepository
				.findByHemocentroIdAndTipoSanguineoAndFatorRh(hemocentro.getId(), tipoSanguineo, fatorRh)
				.orElseGet(() -> new EstoqueSangue(hemocentro, tipoSanguineo, fatorRh, 0));
		estoque.adicionar(bolsas);
		estoqueRepository.save(estoque);
	}

	@Transactional(readOnly = true)
	public List<EstoqueResponse> listar(String hemocentroId) {
		List<EstoqueSangue> estoques = hemocentroId == null || hemocentroId.isBlank()
				? estoqueRepository.findAllByOrderByHemocentroIdAscTipoSanguineoAscFatorRhAsc()
				: estoqueRepository.findByHemocentroIdOrderByTipoSanguineoAscFatorRhAsc(hemocentroId);
		return estoques.stream().map(EstoqueResponse::from).toList();
	}

	@Transactional(readOnly = true)
	public List<EstoqueResponse> listarCriticos() {
		return estoqueRepository.findAllByOrderByHemocentroIdAscTipoSanguineoAscFatorRhAsc().stream()
				.filter(EstoqueSangue::isCritico)
				.map(EstoqueResponse::from)
				.toList();
	}

	@Transactional
	public EstoqueResponse ajustarMinima(Long id, int quantidadeMinima) {
		EstoqueSangue estoque = estoqueRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Estoque não encontrado"));
		estoque.setQuantidadeMinima(quantidadeMinima);
		return EstoqueResponse.from(estoqueRepository.save(estoque));
	}

}
