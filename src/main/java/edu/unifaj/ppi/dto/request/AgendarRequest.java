package edu.unifaj.ppi.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record AgendarRequest(

		@NotNull(message = "Selecione uma data")
		LocalDate data,

		@NotNull(message = "Selecione uma hora")
		LocalTime hora,

		@NotNull(message = "Selecione um hemocentro")
		String hemocentroId) {

}