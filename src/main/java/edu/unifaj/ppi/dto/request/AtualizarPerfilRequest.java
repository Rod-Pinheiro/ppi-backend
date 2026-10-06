package edu.unifaj.ppi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CPF e tipo sanguíneo nao entram aqui de proposito: sao imutaveis e a tela de
 * edicao do app os exibia como somente-leitura.
 */
public record AtualizarPerfilRequest(

		@NotBlank(message = "Informe seu nome")
		String nome,

		@NotBlank(message = "Informe seu email")
		@Email(message = "Email inválido")
		String email,

		@Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres")
		String senhaAtual,

		@Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres")
		String novaSenha) {

}