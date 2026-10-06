package edu.unifaj.ppi.model.enums;

/**
 * Situacao do lote de bolsas. Uma coleta sempre nasce DISPONIVEL; as demais
 * transicoes existem no dominio mas nao sao alcancadas por nenhuma tela hoje.
 */
public enum StatusBolsa {

	// O nome da constante vai cru para o banco; o rotulo acentuado da tela
	// vive no descricao.
	DISPONIVEL("Disponível"),
	RESERVADA("Reservada"),
	UTILIZADA("Utilizada"),
	VENCIDA("Vencida");

	private final String descricao;

	StatusBolsa(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}