package edu.unifaj.ppi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.unifaj.ppi.dto.request.RegistrarColetaRequest;
import edu.unifaj.ppi.dto.response.AgendamentoResponse;
import edu.unifaj.ppi.dto.response.BolsaSangueResponse;
import edu.unifaj.ppi.exception.ConflitoException;
import edu.unifaj.ppi.exception.RecursoNaoEncontradoException;
import edu.unifaj.ppi.exception.RegraDeNegocioException;
import edu.unifaj.ppi.model.Agendamento;
import edu.unifaj.ppi.model.BolsaSangue;
import edu.unifaj.ppi.model.Doador;
import edu.unifaj.ppi.repository.AgendamentoRepository;
import edu.unifaj.ppi.repository.BolsaSangueRepository;
import edu.unifaj.ppi.repository.DoadorRepository;
import edu.unifaj.ppi.validation.Validador;
import edu.unifaj.ppi.validation.ValidadorData;

@Service
public class ColetaService {

	private final AgendamentoRepository agendamentoRepository;
	private final BolsaSangueRepository bolsaSangueRepository;
	private final DoadorRepository doadorRepository;
	private final EstoqueService estoqueService;
	private final NotificacaoService notificacaoService;
	private final Validador validador;
	private final ValidadorData validadorData;

	public ColetaService(AgendamentoRepository agendamentoRepository,
			BolsaSangueRepository bolsaSangueRepository, DoadorRepository doadorRepository,
			EstoqueService estoqueService, NotificacaoService notificacaoService, Validador validador,
			ValidadorData validadorData) {
		this.agendamentoRepository = agendamentoRepository;
		this.bolsaSangueRepository = bolsaSangueRepository;
		this.doadorRepository = doadorRepository;
		this.estoqueService = estoqueService;
		this.notificacaoService = notificacaoService;
		this.validador = validador;
		this.validadorData = validadorData;
	}

	/**
	 * Regra central do dominio, toda numa transacao: ou o agendamento vira
	 * REALIZADO e o lote e gravado, ou nada acontece. Sem isso uma falha entre as
	 * duas escritas deixaria o agendamento realizado sem bolsa.
	 *
	 * <p>As guards seguem a ordem do PrefsManager.registrarColeta do app.
	 */
	@Transactional
	public BolsaSangueResponse registrarColeta(String cpf, Long agendamentoId, RegistrarColetaRequest request) {
		Doador doador = buscarDoador(cpf);
		Agendamento agendamento = buscarAgendamentoDoDoador(doador, agendamentoId);

		if (agendamento.isCancelado()) {
			throw new ConflitoException("Agendamento cancelado não aceita coleta");
		}
		if (agendamento.isRealizado()) {
			throw new ConflitoException("Este agendamento já tem coleta registrada");
		}
		if (bolsaSangueRepository.existsByAgendamentoId(agendamentoId)) {
			throw new ConflitoException("Este agendamento já tem coleta registrada");
		}
		// Data futura nao gera coleta. Data ilegivel tambem nao: validadorData
		// trata null como inelegivel, igual ao DateUtils.isDataNoPassadoOuHoje.
		validadorData.exigirDataPassadaOuHoje(agendamento.getData());

		validador.validarVolumeDoacao(request.volumeMl());
		validador.validarQuantidadeBolsas(request.quantidade());

		if (doador.getTipoSanguineo() == null || doador.getFatorRh() == null) {
			throw new RegraDeNegocioException("Doador sem tipo sanguíneo cadastrado");
		}

		agendamento.marcarRealizado();
		agendamentoRepository.save(agendamento);

		// A bolsa herda data, local e validade do agendamento; o sangue e o do
		// doador, porque foi ele quem doou.
		BolsaSangue bolsa = BolsaSangue.fromAgendamento(agendamento, doador.getTipoSanguineo(), doador.getFatorRh(),
				request.volumeMl(), request.quantidade());
		BolsaSangue salva = bolsaSangueRepository.save(bolsa);

		// Efeitos colaterais da coleta, na mesma transacao: o saldo do hemocentro
		// sobe e o doador recebe o aviso. Se qualquer um falhar, a bolsa nao fica.
		estoqueService.registrarEntrada(agendamento.getHemocentro(), doador.getTipoSanguineo(), doador.getFatorRh(),
				request.quantidade());
		notificacaoService.notificar(doador, "Coleta registrada",
				"Sua doação gerou " + request.quantidade() + " bolsa(s) de " + doador.getTipoCompleto()
						+ " em " + agendamento.getHemocentro().getNome() + ".");

		return BolsaSangueResponse.from(salva);
	}

	@Transactional(readOnly = true)
	public List<BolsaSangueResponse> listarHistorico(String cpf) {
		Doador doador = buscarDoador(cpf);
		// Filtrado por doador: no app o historico mostrava todas as bolsas do
		// aparelho, o que so era seguro porque havia um doador por vez.
		return bolsaSangueRepository.findByAgendamentoDoadorCpfOrderByDataColetaDesc(doador.getCpf()).stream()
				.map(BolsaSangueResponse::from).toList();
	}

	private Agendamento buscarAgendamentoDoDoador(Doador doador, Long agendamentoId) {
		Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado"));
		if (!agendamento.getDoador().getCpf().equals(doador.getCpf())) {
			throw new RecursoNaoEncontradoException("Agendamento não encontrado");
		}
		return agendamento;
	}

	private Doador buscarDoador(String cpf) {
		String normalizado = cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
		return doadorRepository.findByCpf(normalizado)
				.orElseThrow(() -> new RegraDeNegocioException("Erro ao criar agendamento. Faça login novamente."));
	}

}