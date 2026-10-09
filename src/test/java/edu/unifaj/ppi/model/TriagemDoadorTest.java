package edu.unifaj.ppi.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;

class TriagemDoadorTest {

	private final Doador doador = new Doador("João", "joao@email.com", "hash", "12345678901", TipoSanguineo.O,
			FatorRh.POSITIVO);

	@Test
	void pesoEHemoglobinaNoMinimoAprovam() {
		TriagemDoador triagem = new TriagemDoador(doador, LocalDate.now(), 50.0, "120/80", 12.5);

		assertThat(triagem.isApto()).isTrue();
		assertThat(triagem.getMotivoInaptidao()).isNull();
	}

	@Test
	void pesoAbaixoDoMinimoReprova() {
		TriagemDoador triagem = new TriagemDoador(doador, LocalDate.now(), 49.9, "120/80", 14.0);

		assertThat(triagem.isApto()).isFalse();
		assertThat(triagem.getMotivoInaptidao()).contains("Peso");
	}

	@Test
	void hemoglobinaAbaixoDoMinimoReprova() {
		TriagemDoador triagem = new TriagemDoador(doador, LocalDate.now(), 70.0, "120/80", 11.0);

		assertThat(triagem.isApto()).isFalse();
		assertThat(triagem.getMotivoInaptidao()).contains("Hemoglobina");
	}

	@Test
	void regraEstaticaConfereComOConstrutor() {
		assertThat(TriagemDoador.isApto(60.0, 13.0)).isTrue();
		assertThat(TriagemDoador.isApto(45.0, 13.0)).isFalse();
	}

}
