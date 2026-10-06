package edu.unifaj.ppi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.unifaj.ppi.dto.request.AgendarRequest;
import edu.unifaj.ppi.dto.response.AgendamentoResponse;
import edu.unifaj.ppi.exception.ConflitoException;
import edu.unifaj.ppi.exception.RecursoNaoEncontradoException;
import edu.unifaj.ppi.exception.RegraDeNegocioException;
import edu.unifaj.ppi.model.Agendamento;
import edu.unifaj.ppi.model.Doador;
import edu.unifaj.ppi.model.Hemocentro;
import edu.unifaj.ppi.repository.AgendamentoRepository;
import edu.unifaj.ppi.repository.DoadorRepository;
import edu.unifaj.ppi.repository.HemocentroRepository;

@Service
public class AgendamentoService {

	private static final String MENSAGEM_DOADOR = "Erro ao criar agendamento. Faça login novamente.";
	private static final String MENSAGEM_AGENDAMENTO = "Agendamento não encontrado";

	private final AgendamentoRepository agendamentoRepository;
	private final DoadorRepository doadorRepository;
	private final HemocentroRepository hemocentroRepository;

	public AgendamentoService(AgendamentoRepository agendamentoRepository, DoadorRepository doadorRepository,
			HemocentroRepository hemocentroRepository) {
		this.agendamentoRepository = agendamentoRepository;
		this.doadorRepository = doadorRepository;
		this.hemocentroRepository = hemocentroRepository;
	}

	@Transactional
	public AgendamentoResponse agendar(String cpf, AgendarRequest request) {
		Doador doador = buscarDoador(cpf);
		Hemocentro hemocentro = hemocentroRepository.findById(request.hemocentroId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Hemocentro não encontrado"));

		Agendamento agendamento = new Agendamento(doador, hemocentro, request.data(), request.hora());
		return AgendamentoResponse.from(agendamentoRepository.save(agendamento), null);
	}

	/**
	 * No app o botao de cancelar so aparecia em agendamento PENDENTE, e essa era a
	 * unica barreira contra cancelar um agendamento ja realizado. Aqui a regra e
	 * do servidor: cancelamento so vale a partir de PENDENTE ou CONFIRMADO.
	 */
	@Transactional
	public AgendamentoResponse cancelar(String cpf, Long agendamentoId) {
		Agendamento agendamento = buscarAgendamentoDoDoador(cpf, agendamentoId);

		if (agendamento.isCancelado()) {
			throw new ConflitoException("Este agendamento já está cancelado");
		}
		if (agendamento.isRealizado()) {
			throw new ConflitoException("Agendamento realizado não pode ser cancelado");
		}

		agendamento.cancelar();
		return AgendamentoResponse.from(agendamentoRepository.save(agendamento), null);
	}

	/**
	 * Carrega o agendamento garantindo que ele pertence ao doador do CPF. O app
	 * confiava no id que a outra tela mandava no Intent; aqui a posse e conferida.
	 */
	@Transactional(readOnly = true)
	public Agendamento buscarAgendamentoDoDoador(String cpf, Long agendamentoId) {
		Doador doador = buscarDoador(cpf);
		Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException(MENSAGEM_AGENDAMENTO));
		if (!agendamento.getDoador().getCpf().equals(doador.getCpf())) {
			throw new RecursoNaoEncontradoException(MENSAGEM_AGENDAMENTO);
		}
		return agendamento;
	}

	private Doador buscarDoador(String cpf) {
		String normalizado = cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
		return doadorRepository.findByCpf(normalizado)
				.orElseThrow(() -> new RegraDeNegocioException(MENSAGEM_DOADOR));
	}

}