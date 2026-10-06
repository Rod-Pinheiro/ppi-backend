package edu.unifaj.ppi.exception;

/**
 * Conflito com o estado atual: email ou CPF ja cadastrado, coleta ja registrada
 * para o agendamento, ou cancelamento de algo que nao esta pendente. Respondida
 * com 409.
 */
public class ConflitoException extends RuntimeException {

	public ConflitoException(String mensagem) {
		super(mensagem);
	}

}