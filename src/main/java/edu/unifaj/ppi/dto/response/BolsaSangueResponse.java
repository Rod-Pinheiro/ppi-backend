package edu.unifaj.ppi.dto.response;

import java.time.LocalDate;

import edu.unifaj.ppi.model.BolsaSangue;
import edu.unifaj.ppi.model.enums.StatusBolsa;

public record BolsaSangueResponse(
		Long id,
		String codigo,
		String tipoCompleto,
		LocalDate dataColeta,
		LocalDate dataValidade,
		int volumeMl,
		int volumeTotalMl,
		int quantidade,
		StatusBolsa status,
		String statusDescricao,
		HemocentroResponse hemocentroOrigem,
		Long agendamentoId) {

	public static BolsaSangueResponse from(BolsaSangue bolsa) {
		return new BolsaSangueResponse(bolsa.getId(), bolsa.getCodigo(), bolsa.getTipoCompleto(),
				bolsa.getDataColeta(), bolsa.getDataValidade(), bolsa.getVolumeMl(), bolsa.getVolumeTotalMl(),
				bolsa.getQuantidade(), bolsa.getStatus(), bolsa.getStatus().getDescricao(),
				HemocentroResponse.from(bolsa.getHemocentroOrigem()), bolsa.getAgendamento().getId());
	}

}