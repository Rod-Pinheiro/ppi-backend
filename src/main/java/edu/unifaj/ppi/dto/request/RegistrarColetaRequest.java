package edu.unifaj.ppi.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * O tipo sanguíneo nao e campo deste request: o sangue e o do doador logado, e nao
 * algo que a agente escolha. Se mandarem mesmo assim, o campo e simplesmente
 * ignorado.
 */
public record RegistrarColetaRequest(

		@NotNull(message = "Informe o volume por bolsa")
		@Min(value = 200, message = "Volume deve estar entre 200ml e 470ml")
		@Max(value = 470, message = "Volume deve estar entre 200ml e 470ml")
		Integer volumeMl,

		@NotNull(message = "Informe a quantidade de bolsas")
		@Min(value = 1, message = "A quantidade deve estar entre 1 e 6 bolsas")
		@Max(value = 6, message = "A quantidade deve estar entre 1 e 6 bolsas")
		Integer quantidade) {

}