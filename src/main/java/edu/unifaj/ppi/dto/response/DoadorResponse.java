package edu.unifaj.ppi.dto.response;

import edu.unifaj.ppi.model.Doador;

/**
 * Nunca inclui a senha, nem mesmo o hash.
 */
public record DoadorResponse(
		Long id,
		String nome,
		String email,
		String cpf,
		String tipoSanguineo,
		String fatorRh,
		String tipoCompleto) {

	public static DoadorResponse from(Doador doador) {
		return new DoadorResponse(doador.getId(), doador.getNome(), doador.getEmail(), doador.getCpf(),
				doador.getTipoSanguineo().getValor(), doador.getFatorRh().getValor(), doador.getTipoCompleto());
	}

}