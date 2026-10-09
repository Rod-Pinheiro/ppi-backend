package edu.unifaj.ppi.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PeriodoValidadeTest {

	private static final LocalDate COLETA = LocalDate.of(2026, 4, 13);

	@Test
	void validadeTermina42DiasDepoisDaColeta() {
		PeriodoValidade periodo = new PeriodoValidade(COLETA);

		assertThat(periodo.getInicio()).isEqualTo(COLETA);
		assertThat(periodo.getFim()).isEqualTo(LocalDate.of(2026, 5, 25));
	}

	@Test
	void noUltimoDiaAindaNaoEstaVencido() {
		PeriodoValidade periodo = new PeriodoValidade(COLETA);

		assertThat(periodo.isVencido(LocalDate.of(2026, 5, 25))).isFalse();
	}

	@Test
	void noDiaSeguinteEstaVencido() {
		PeriodoValidade periodo = new PeriodoValidade(COLETA);

		assertThat(periodo.isVencido(LocalDate.of(2026, 5, 26))).isTrue();
	}

	@Test
	void diasRestantesContamAteOFim() {
		PeriodoValidade periodo = new PeriodoValidade(COLETA);

		assertThat(periodo.diasRestantes(COLETA)).isEqualTo(42);
		assertThat(periodo.diasRestantes(LocalDate.of(2026, 5, 25))).isZero();
	}

	@Test
	void aposVencerNaoHaDiasRestantesNegativos() {
		PeriodoValidade periodo = new PeriodoValidade(COLETA);

		assertThat(periodo.diasRestantes(LocalDate.of(2026, 6, 1))).isZero();
	}

}
