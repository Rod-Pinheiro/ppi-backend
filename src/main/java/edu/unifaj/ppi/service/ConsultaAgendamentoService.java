package edu.unifaj.ppi.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.unifaj.ppi.dto.response.AgendamentoResponse;
import edu.unifaj.ppi.dto.response.QuantidadesResponse;
import edu.unifaj.ppi.model.Agendamento;
import edu.unifaj.ppi.model.BolsaSangue;
import edu.unifaj.ppi.model.Doador;
import edu.unifaj.ppi.model.enums.StatusAgendamento;
import edu.unifaj.ppi.repository.AgendamentoRepository;
import edu.unifaj.ppi.repository.BolsaSangueRepository;
import edu.unifaj.ppi.repository.DoadorRepository;
import edu.unifaj.ppi.exception.RecursoNaoEncontradoException;

@Service
public class ConsultaAgendamentoService {

	private final AgendamentoRepository agendamentoRepository;
	private final BolsaSangueRepository bolsaSangueRepository;
	private final DoadorRepository doadorRepository;

	public ConsultaAgendamentoService(AgendamentoRepository agendamentoRepository,
			BolsaSangueRepository bolsaSangueRepository, DoadorRepository doadorRepository) {
		this.agendamentoRepository = agendamentoRepository;
		this.bolsaSangueRepository = bolsaSangueRepository;
		this.doadorRepository = doadorRepository;
	}

	@Transactional(readOnly = true)
	public List<AgendamentoResponse> listarDoDoador(String cpf) {
		return comQuantidades(
				agendamentoRepository.findByDoadorCpfOrderByDataAscHoraAsc(normalizar(cpf)));
	}

	/**
	 * Agendamentos que a agente pode registrar coleta: nao cancelado, nao
	 * realizado, sem bolsa registrada e com data de hoje ou anterior, do mais
	 * antigo para o mais novo.
	 *
	 * <p>A bolsa existente e a fonte da verdade e nao o status REALIZADO, que e
	 * so o reflexo na tela.
	 */
	@Transactional(readOnly = true)
	public List<AgendamentoResponse> listarParaRegistro(String cpf) {
		List<Agendamento> candidatos = agendamentoRepository
				.findByDoadorCpfAndStatusNotInAndDataLessThanEqualOrderByDataAscHoraAsc(normalizar(cpf),
						List.of(StatusAgendamento.CANCELADO, StatusAgendamento.REALIZADO), LocalDate.now());

		List<Agendamento> elegiveis = candidatos.stream()
				.filter(agendamento -> !bolsaSangueRepository.existsByAgendamentoId(agendamento.getId()))
				.toList();

		return comQuantidades(elegiveis);
	}

	@Transactional(readOnly = true)
	public QuantidadesResponse quantidadesPorAgendamento(String cpf) {
		Map<Long, Integer> porAgendamento = new HashMap<>();
		for (BolsaSangue bolsa : bolsaSangueRepository
				.findByAgendamentoDoadorCpfOrderByDataColetaDesc(normalizar(cpf))) {
			porAgendamento.merge(bolsa.getAgendamento().getId(), bolsa.getQuantidade(), Integer::sum);
		}
		return new QuantidadesResponse(porAgendamento);
	}

	/**
	 * A tela de lista usava a quantidade por agendamento para decidir se mostrava
	 * a linha "3 bolsas registradas".
	 */
	private List<AgendamentoResponse> comQuantidades(List<Agendamento> agendamentos) {
		Map<Long, Integer> quantidades = new HashMap<>();
		for (Agendamento agendamento : agendamentos) {
			bolsaSangueRepository.findByAgendamentoId(agendamento.getId())
					.ifPresent(bolsa -> quantidades.put(agendamento.getId(), bolsa.getQuantidade()));
		}
		return agendamentos.stream()
				.map(agendamento -> AgendamentoResponse.from(agendamento, quantidades.get(agendamento.getId())))
				.toList();
	}

	private String normalizar(String cpf) {
		String digitos = cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
		if (!doadorRepository.existsByCpf(digitos)) {
			throw new RecursoNaoEncontradoException("Doador não encontrado");
		}
		return digitos;
	}

}