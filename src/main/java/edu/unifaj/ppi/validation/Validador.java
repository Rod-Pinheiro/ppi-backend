package edu.unifaj.ppi.validation;

import org.springframework.stereotype.Component;

import edu.unifaj.ppi.exception.RegraDeNegocioException;

/**
 * Porta das regras do ValidacaoUtils do app. As expressoes regulares e os
 * intervalos sao os mesmos, incluindo o CPF so com 11 digitos e sem conferida
 * de digito verificador.
 */
@Component
public class Validador {

	private static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
	private static final int TAMANHO_CPF = 11;
	private static final int TAMANHO_MINIMO_SENHA = 6;
	private static final int VOLUME_MINIMO_ML = 200;
	private static final int VOLUME_MAXIMO_ML = 470;
	private static final int QUANTIDADE_MINIMA_BOLSAS = 1;
	private static final int QUANTIDADE_MAXIMA_BOLSAS = 6;

	public void validarEmail(String email) {
		if (email == null || email.isEmpty() || !email.matches(REGEX_EMAIL)) {
			throw new RegraDeNegocioException("Email inválido");
		}
	}

	public void validarSenha(String senha) {
		if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
			throw new RegraDeNegocioException("Senha deve ter pelo menos 6 caracteres");
		}
	}

	/**
	 * Igual ao app: conta os digitos e nao valida os dois verificadores. Manter
	 * como esta para nao divergir do que o formulario do app ja aceitou.
	 */
	public void validarCpf(String cpf) {
		if (cpf == null || cpf.replaceAll("[^0-9]", "").length() != TAMANHO_CPF) {
			throw new RegraDeNegocioException("CPF inválido");
		}
	}

	public void validarVolumeDoacao(int volumeMl) {
		if (volumeMl < VOLUME_MINIMO_ML || volumeMl > VOLUME_MAXIMO_ML) {
			throw new RegraDeNegocioException("Volume deve estar entre 200ml e 470ml");
		}
	}

	public void validarQuantidadeBolsas(int quantidade) {
		if (quantidade < QUANTIDADE_MINIMA_BOLSAS || quantidade > QUANTIDADE_MAXIMA_BOLSAS) {
			throw new RegraDeNegocioException("A quantidade deve estar entre 1 e 6 bolsas");
		}
	}

	/**
	 * Mesma ordem de verificacao da tela de cadastro do app, para que a mensagem
	 * de erro seja sempre a primeira regra violada.
	 */
	public void validarCamposObrigatorios(String... campos) {
		for (int i = 0; i < campos.length; i++) {
			if (campos[i] == null || campos[i].trim().isEmpty()) {
				throw new RegraDeNegocioException("Campo " + (i + 1) + " é obrigatório");
			}
		}
	}

}