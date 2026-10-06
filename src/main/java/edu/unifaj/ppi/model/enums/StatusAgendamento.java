package edu.unifaj.ppi.model.enums;

/**
 * Ciclo de vida do agendamento. As transicoes sao impostas pelo
 * AgendamentoService, nao pelos controllers: cancelar so vale de PENDENTE e
 * marcarRealizado so vale quando a coleta efetivamente acontece.
 */
public enum StatusAgendamento {

	PENDENTE("Pendente"),
	CONFIRMADO("Confirmado"),
	CANCELADO("Cancelado"),
	REALIZADO("Realizado");

	private final String descricao;

	StatusAgendamento(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}