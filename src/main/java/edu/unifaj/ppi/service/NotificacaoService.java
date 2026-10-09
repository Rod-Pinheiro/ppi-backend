package edu.unifaj.ppi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.unifaj.ppi.dto.response.NotificacaoResponse;
import edu.unifaj.ppi.exception.RecursoNaoEncontradoException;
import edu.unifaj.ppi.model.Doador;
import edu.unifaj.ppi.model.Notificacao;
import edu.unifaj.ppi.repository.DoadorRepository;
import edu.unifaj.ppi.repository.NotificacaoRepository;

@Service
public class NotificacaoService {

	private final NotificacaoRepository notificacaoRepository;
	private final DoadorRepository doadorRepository;

	public NotificacaoService(NotificacaoRepository notificacaoRepository, DoadorRepository doadorRepository) {
		this.notificacaoRepository = notificacaoRepository;
		this.doadorRepository = doadorRepository;
	}

	/** Usado internamente pelos outros servicos para avisar o doador. */
	@Transactional
	public void notificar(Doador doador, String titulo, String mensagem) {
		notificacaoRepository.save(new Notificacao(doador, titulo, mensagem));
	}

	@Transactional(readOnly = true)
	public List<NotificacaoResponse> listar(String cpf) {
		buscarDoador(cpf);
		return notificacaoRepository.findByDoadorCpfOrderByCriadaEmDesc(normalizar(cpf)).stream()
				.map(NotificacaoResponse::from).toList();
	}

	@Transactional(readOnly = true)
	public long contarNaoLidas(String cpf) {
		buscarDoador(cpf);
		return notificacaoRepository.countByDoadorCpfAndLidaFalse(normalizar(cpf));
	}

	@Transactional
	public NotificacaoResponse marcarLida(String cpf, Long id) {
		buscarDoador(cpf);
		Notificacao notificacao = notificacaoRepository.findByIdAndDoadorCpf(id, normalizar(cpf))
				.orElseThrow(() -> new RecursoNaoEncontradoException("Notificação não encontrada"));
		notificacao.marcarLida();
		return NotificacaoResponse.from(notificacaoRepository.save(notificacao));
	}

	private Doador buscarDoador(String cpf) {
		return doadorRepository.findByCpf(normalizar(cpf))
				.orElseThrow(() -> new RecursoNaoEncontradoException("Doador não encontrado"));
	}

	private String normalizar(String cpf) {
		return cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
	}

}
