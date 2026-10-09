package edu.unifaj.ppi.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Janela de validade de um lote de sangue: 42 dias contados da coleta. Antes
 * essa conta vivia espalhada em {@code BolsaSangue.fromAgendamento}; aqui ela
 * tem um lugar so e pode ser testada isoladamente.
 */
public final class PeriodoValidade {

	/** Validade do sangue doado: sempre 42 dias. */
	public static final int DIAS = 42;

	private final LocalDate inicio;
	private final LocalDate fim;

	public PeriodoValidade(LocalDate inicio) {
		this.inicio = inicio;
		this.fim = inicio.plusDays(DIAS);
	}

	public boolean isVencido(LocalDate referencia) {
		return referencia.isAfter(fim);
	}

	public long diasRestantes(LocalDate referencia) {
		if (isVencido(referencia)) {
			return 0;
		}
		return ChronoUnit.DAYS.between(referencia, fim);
	}

	public LocalDate getInicio() {
		return inicio;
	}

	public LocalDate getFim() {
		return fim;
	}

}
