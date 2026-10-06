package edu.unifaj.ppi.validation;

import org.springframework.stereotype.Component;

import edu.unifaj.ppi.exception.RegraDeNegocioException;
import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;

/**
 * Interpreta o tipo sanguíneo no formato "O+" que o app usava, separado nos dois
 * enums que vao para o banco.
 */
@Component
public class ConversorTipoSanguineo {

	/**
	 * O sinal do fator Rh e o ultimo caractere e o tipo e o que vem antes. O app
	 * fazia {@code substring(0, 1)} e {@code substring(1)}, o que so funciona
	 * para tipos de uma letra: em "AB+" o segundo corte devolvia "B+" e o
	 * cadastro era recusado como tipo invalido. Aqui "AB+" e "AB-" tambem
	 * funcionam.
	 */
	public TipoSanguineo paraTipo(String tipoCompleto) {
		if (!temFormatoCompleto(tipoCompleto)) {
			return null;
		}
		return TipoSanguineo.fromValor(tipoCompleto.substring(0, tipoCompleto.length() - 1));
	}

	public FatorRh paraFatorRh(String tipoCompleto) {
		if (!temFormatoCompleto(tipoCompleto)) {
			return null;
		}
		return FatorRh.fromValor(tipoCompleto.substring(tipoCompleto.length() - 1));
	}

	private boolean temFormatoCompleto(String tipoCompleto) {
		return tipoCompleto != null && tipoCompleto.trim().length() >= 2;
	}

	/**
	 * @return true se o valor no formato "O+" foi entendido
	 */
	public boolean validaTipoSanguineo(String tipoCompleto) {
		return paraTipo(tipoCompleto) != null && paraFatorRh(tipoCompleto) != null;
	}

	public void validarTipoSanguineo(String tipoCompleto) {
		if (!validaTipoSanguineo(tipoCompleto)) {
			throw new RegraDeNegocioException("Tipo sanguíneo inválido");
		}
	}

}