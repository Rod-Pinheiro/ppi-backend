package edu.unifaj.ppi.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduz as excecoes de dominio em ProblemDetail (RFC 7807), que ja vem
 * serializado pelo proprio Spring.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RegraDeNegocioException.class)
	public ProblemDetail handleRegraDeNegocio(RegraDeNegocioException excecao) {
		// UNPROCESSABLE_CONTENT: UNPROCESSABLE_ENTITY esta deprecado no Spring 7 e
		// emite warning na compilacao. O codigo HTTP continua sendo 422.
		return criar(HttpStatus.UNPROCESSABLE_CONTENT, excecao.getMessage());
	}

	@ExceptionHandler(ConflitoException.class)
	public ProblemDetail handleConflito(ConflitoException excecao) {
		return criar(HttpStatus.CONFLICT, excecao.getMessage());
	}

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ProblemDetail handleNaoEncontrado(RecursoNaoEncontradoException excecao) {
		return criar(HttpStatus.NOT_FOUND, excecao.getMessage());
	}

	@ExceptionHandler(CredenciaisInvalidasException.class)
	public ProblemDetail handleCredenciais(CredenciaisInvalidasException excecao) {
		return criar(HttpStatus.UNAUTHORIZED, excecao.getMessage());
	}

	/**
	 * Falha de @Valid no corpo do request. As mensagens do Bean Validation chegam
	 * em ingles e com o nome do campo na frente, entao a gente troca pelo texto
	 * que o app exibia.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleValidacao(MethodArgumentNotValidException excecao) {
		String detalhe = excecao.getBindingResult().getFieldErrors().stream()
				.map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
				.collect(Collectors.joining("; "));
		return criar(HttpStatus.BAD_REQUEST, detalhe.isEmpty() ? "Dados inválidos" : detalhe);
	}

	/**
	 * JSON malformado ou tipo incompativel no corpo, como uma data que nao
	 * existe ou um texto onde o modelo espera numero.
	 */
	@ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
	public ProblemDetail handleCorpoInvalido(org.springframework.http.converter.HttpMessageNotReadableException excecao) {
		return criar(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido");
	}

	private ProblemDetail criar(HttpStatus status, String detalhe) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detalhe);
		problem.setTitle(status.getReasonPhrase());
		return problem;
	}

}