package edu.unifaj.ppi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Ajuste do limite minimo de um estoque. O saldo disponivel so muda por coleta
 * ou baixa; pela API o que se configura e quando o estoque vira critico.
 */
public record AjusteEstoqueRequest(
		@NotNull(message = "Informe a quantidade mínima")
		@Min(value = 0, message = "A quantidade mínima não pode ser negativa")
		Integer quantidadeMinima) {
}
