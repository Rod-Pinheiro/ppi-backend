package edu.unifaj.ppi.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import edu.unifaj.ppi.exception.RegraDeNegocioException;
import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;

class ConversorTipoSanguineoTest {

	private final ConversorTipoSanguineo conversor = new ConversorTipoSanguineo();

	@Test
	void separaTipoEFatorRh() {
		assertThat(conversor.paraTipo("O+")).isEqualTo(TipoSanguineo.O);
		assertThat(conversor.paraFatorRh("O+")).isEqualTo(FatorRh.POSITIVO);
	}

	@Test
	void aceitaCaixaBaixaComoOSpinnerDoApp() {
		assertThat(conversor.validaTipoSanguineo("ab-")).isTrue();
		assertThat(conversor.paraTipo("ab-")).isEqualTo(TipoSanguineo.AB);
		assertThat(conversor.paraFatorRh("ab-")).isEqualTo(FatorRh.NEGATIVO);
	}

	@Test
	void cobreOsOitoTiposValidos() {
		for (String tipo : new String[] { "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-" }) {
			assertThat(conversor.validaTipoSanguineo(tipo)).as(tipo).isTrue();
		}
	}

	@Test
	void rejeitaValorInvalidoOuCurto() {
		assertThat(conversor.validaTipoSanguineo("Z+")).isFalse();
		assertThat(conversor.validaTipoSanguineo("O")).isFalse();
		assertThat(conversor.validaTipoSanguineo("")).isFalse();
		assertThat(conversor.validaTipoSanguineo(null)).isFalse();
	}

	@Test
	void dataFuturaNaoElegivelEHojeElegivel() {
		ValidadorData validadorData = new ValidadorData();

		assertThat(validadorData.isDataNoPassadoOuHoje(LocalDate.now().plusDays(1))).isFalse();
		assertThat(validadorData.isDataNoPassadoOuHoje(LocalDate.now())).isTrue();
		assertThat(validadorData.isDataNoPassadoOuHoje(LocalDate.now().minusYears(1))).isTrue();
	}

	@Test
	void dataIlegivelFalhaFechado() {
		// Fail-closed como no DateUtils do app: data que nao da para ler nao
		// habilita registro de coleta.
		ValidadorData validadorData = new ValidadorData();

		assertThat(validadorData.isDataNoPassadoOuHoje(null)).isFalse();
		assertThatThrownBy(() -> validadorData.exigirDataPassadaOuHoje(null))
				.isInstanceOf(RegraDeNegocioException.class);
	}

}