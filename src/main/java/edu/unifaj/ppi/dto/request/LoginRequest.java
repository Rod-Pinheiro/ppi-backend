package edu.unifaj.ppi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

		@NotBlank(message = "Preencha todos os campos")
		String email,

		@NotBlank(message = "Preencha todos os campos")
		String senha) {

}