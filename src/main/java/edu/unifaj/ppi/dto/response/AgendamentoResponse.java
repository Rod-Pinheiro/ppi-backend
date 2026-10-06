package edu.unifaj.ppi.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import edu.unifaj.ppi.model.Agendamento;
import edu.unifaj.ppi.model.enums.StatusAgendamento;

/**
 * {@code quantidadeBolsas} e null quando o agendamento ainda nao tem coleta, que
 * e como o adapter do app decide se mostra a linha de bolsas.
 */
public record AgendamentoResponse(
		Long id,
		LocalDate data,
		LocalTime hora,
		StatusAgendamento status,
		String statusDescricao,
		HemocentroResponse hemocentro,
		Integer quantidadeBolsas,
		LocalDate dataConfirmacao,
		String observacoes) {

	public static AgendamentoResponse from(Agendamento agendamento, Integer quantidadeBolsas) {
		return new AgendamentoResponse(agendamento.getId(), agendamento.getData(), agendamento.getHora(),
				agendamento.getStatus(), agendamento.getStatus().getDescricao(),
				HemocentroResponse.from(agendamento.getHemocentro()), quantidadeBolsas,
				agendamento.getDataConfirmacao(), agendamento.getObservacoes());
	}

}