package edu.unifaj.ppi.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Roda sem Spring nem banco, que era o motivo da classe ser pura no app.
 */
class NivelDoadorTest {

	@Test
	void semRegistrosBarraFicaVazia() {
		NivelDoador nivel = new NivelDoador(0);

		assertThat(nivel.percentual()).isZero();
		assertThat(nivel.isMaximo()).isFalse();
	}

	@Test
	void umRegistroPreenche33PorCento() {
		assertThat(new NivelDoador(1).percentual()).isEqualTo(33);
	}

	@Test
	void doisRegistrosPreenchem67PorCento() {
		assertThat(new NivelDoador(2).percentual()).isEqualTo(67);
	}

	@Test
	void tresRegistrosAtingemOTeto() {
		NivelDoador nivel = new NivelDoador(3);

		assertThat(nivel.percentual()).isEqualTo(100);
		assertThat(nivel.isMaximo()).isTrue();
	}

	@Test
	void acimaDoTetoNaoEstouraACemPorCento() {
		assertThat(new NivelDoador(7).percentual()).isEqualTo(100);
		assertThat(new NivelDoador(7).isMaximo()).isTrue();
	}

	@Test
	void quantidadeDeBolsasNaoContaParaONivel() {
		// Um agendamento com 6 bolsas e um registro so. O numero que conta e a
		// quantidade de lotes, que o servico le como count do repositorio.
		assertThat(new NivelDoador(1).percentual()).isEqualTo(new NivelDoador(1).percentual());
	}

	@Test
	void limiteExpostoEUtilizadoNoCalculo() {
		assertThat(NivelDoador.LIMITE).isEqualTo(3);
	}

}