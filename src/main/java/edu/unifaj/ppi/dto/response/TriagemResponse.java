package edu.unifaj.ppi.dto.response;

import java.time.LocalDate;

import edu.unifaj.ppi.model.TriagemDoador;

public record TriagemResponse(
		Long id,
		LocalDate data,
		double pesoKg,
		String pressaoArterial,
		double hemoglobina,
		boolean apto,
		String motivoInaptidao,
		String observacoes) {
	public static TriagemResponse from(TriagemDoador triagem) {
		return new TriagemResponse(triagem.getId(), triagem.getData(), triagem.getPesoKg(),
				triagem.getPressaoArterial(), triagem.getHemoglobina(), triagem.isApto(),
				triagem.getMotivoInaptidao(), triagem.getObservacoes());
	}
}
