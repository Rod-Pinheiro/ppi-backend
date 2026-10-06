package edu.unifaj.ppi.model.enums;

/**
 * Fator Rh. O {@code valor} e o simbolo usado na tela ("O+"), mas quem vai para o
 * banco e o {@code name()} da constante.
 */
public enum FatorRh {

	POSITIVO("+"),
	NEGATIVO("-");

	private final String valor;

	FatorRh(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	/**
	 * Casa exata com "+" ou "-", ao contrario do {@link TipoSanguineo} que ignora
	 * a caixa: aqui os dois valores sao simbolos.
	 *
	 * @return null quando o valor nao corresponde a nenhum fator
	 */
	public static FatorRh fromValor(String valor) {
		if (valor == null) {
			return null;
		}
		for (FatorRh fator : values()) {
			if (fator.valor.equals(valor.trim())) {
				return fator;
			}
		}
		return null;
	}

}