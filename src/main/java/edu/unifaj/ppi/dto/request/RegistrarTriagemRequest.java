package edu.unifaj.ppi.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dados da triagem clinica. A aptidao nao entra: ela e derivada de peso e
 * hemoglobina no proprio dominio, e nao algo que o cliente escolhe.
 */
public record RegistrarTriagemRequest(
		@NotNull(message = "Informe a data da triagem")
		LocalDate data,
		@NotNull(message = "Informe o peso")
		@DecimalMin(value = "1.0", message = "Peso inválido")
		Double pesoKg,
		@NotBlank(message = "Informe a pressão arterial")
		@Size(max = 20, message = "Pressão arterial inválida")
		String pressaoArterial,
		@NotNull(message = "Informe a hemoglobina")
		@DecimalMin(value = "1.0", message = "Hemoglobina inválida")
		Double hemoglobina,
		@Size(max = 500, message = "Observações muito longas")
		String observacoes) {
}
