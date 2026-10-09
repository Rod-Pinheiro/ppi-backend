package edu.unifaj.ppi.dto.response;

import edu.unifaj.ppi.model.EstoqueSangue;
import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;

public record EstoqueResponse(
		Long id,
		HemocentroResponse hemocentro,
		TipoSanguineo tipoSanguineo,
		FatorRh fatorRh,
		String tipoCompleto,
		int quantidadeDisponivel,
		int quantidadeMinima,
		boolean critico,
		boolean disponivel) {
	public static EstoqueResponse from(EstoqueSangue estoque) {
		return new EstoqueResponse(estoque.getId(), HemocentroResponse.from(estoque.getHemocentro()),
				estoque.getTipoSanguineo(), estoque.getFatorRh(), estoque.getTipoCompleto(),
				estoque.getQuantidadeDisponivel(), estoque.getQuantidadeMinima(), estoque.isCritico(),
				estoque.isDisponivel());
	}
}
