package edu.unifaj.ppi.exception;

/**
 * Recurso inexistente: hemocentro, agendamento ou doador nao encontrado.
 * Respondida com 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

	public RecursoNaoEncontradoException(String mensagem) {
		super(mensagem);
	}

}