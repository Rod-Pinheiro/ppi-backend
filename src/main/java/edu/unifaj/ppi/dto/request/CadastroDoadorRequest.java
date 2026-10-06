package edu.unifaj.ppi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A validacao de CPF fica fora do Bean Validation de proposito: a regra e "apenas
 * 11 digitos" e o Validador do dominio ja faz isso, com a mensagem em portugues
 * que o app exibia.
 */
public record CadastroDoadorRequest(

		@NotBlank(message = "Campo 1 é obrigatório")
		String nome,

		@NotBlank(message = "Campo 2 é obrigatório")
		@Email(message = "Email inválido")
		String email,

		@NotBlank(message = "Campo 3 é obrigatório")
		@Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
		String senha,

		@NotBlank(message = "Campo 4 é obrigatório")
		String cpf,

		@NotBlank(message = "Campo 5 é obrigatório")
		String tipoSanguineo) {

}