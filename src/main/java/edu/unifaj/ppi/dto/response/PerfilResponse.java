package edu.unifaj.ppi.dto.response;

import edu.unifaj.ppi.model.NivelDoador;

/**
 * Inclui o nivel do doador para que o cartazinho do app nao precise contar os
 * registros sozinho: a regra e do backend.
 */
public record PerfilResponse(
		DoadorResponse doador,
		int registros,
		int limite,
		int percentual,
		boolean nivelMaximo) {

	public static PerfilResponse from(DoadorResponse doador, int registros) {
		NivelDoador nivel = new NivelDoador(registros);
		return new PerfilResponse(doador, nivel.getRegistros(), NivelDoador.LIMITE, nivel.percentual(),
				nivel.isMaximo());
	}

}