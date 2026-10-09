package edu.unifaj.ppi.dto.response;

import java.time.LocalDateTime;

import edu.unifaj.ppi.model.Notificacao;

public record NotificacaoResponse(
		Long id,
		String titulo,
		String mensagem,
		LocalDateTime criadaEm,
		boolean lida) {
	public static NotificacaoResponse from(Notificacao notificacao) {
		return new NotificacaoResponse(notificacao.getId(), notificacao.getTitulo(), notificacao.getMensagem(),
				notificacao.getCriadaEm(), notificacao.isLida());
	}
}
