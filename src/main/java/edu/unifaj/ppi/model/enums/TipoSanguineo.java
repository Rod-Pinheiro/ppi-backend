package edu.unifaj.ppi.model.enums;

/**
 * Tipo sanguíneo. O valor persistido e o {@code name()} da constante, porque a
 * coluna e varchar com CHECK e o Hibernate mapeia com EnumType.STRING.
 */
public enum TipoSanguineo {

	A("A"),
	B("B"),
	AB("AB"),
	O("O");

	private final String valor;

	TipoSanguineo(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	/**
	 * Aceita qualquer caixa, como o spinner do app, que mandava "o+" ou "O+".
	 *
	 * @return null quando o valor nao corresponde a nenhum tipo
	 */
	public static TipoSanguineo fromValor(String valor) {
		if (valor == null) {
			return null;
		}
		for (TipoSanguineo tipo : values()) {
			if (tipo.valor.equalsIgnoreCase(valor.trim())) {
				return tipo;
			}
		}
		return null;
	}

}