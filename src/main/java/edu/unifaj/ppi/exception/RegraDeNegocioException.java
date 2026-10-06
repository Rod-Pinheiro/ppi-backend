package edu.unifaj.ppi.exception;

/**
 * Regra de negocio violada. Respondida com 422: a sintaxe do request estava
 * correta, mas a operacao nao faz sentido no estado atual dos dados.
 */
public class RegraDeNegocioException extends RuntimeException {

	public RegraDeNegocioException(String mensagem) {
		super(mensagem);
	}

}