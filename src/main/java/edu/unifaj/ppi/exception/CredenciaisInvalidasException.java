package edu.unifaj.ppi.exception;

/**
 * Login sem senha correspondente. Respondida com 401, com a mesma mensagem para
 * email inexistente e senha errada, para nao revelar quais emails existem.
 */
public class CredenciaisInvalidasException extends RuntimeException {

	public CredenciaisInvalidasException() {
		super("Email ou senha incorretos");
	}

}