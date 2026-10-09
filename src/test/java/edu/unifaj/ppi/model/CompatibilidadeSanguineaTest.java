package edu.unifaj.ppi.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import edu.unifaj.ppi.model.enums.TipoSanguineo;

class CompatibilidadeSanguineaTest {

	@Test
	void oEDoadorUniversal() {
		assertThat(CompatibilidadeSanguinea.isDoadorUniversal(TipoSanguineo.O)).isTrue();
		assertThat(CompatibilidadeSanguinea.podeDoarPara(TipoSanguineo.O, TipoSanguineo.A)).isTrue();
		assertThat(CompatibilidadeSanguinea.podeDoarPara(TipoSanguineo.O, TipoSanguineo.AB)).isTrue();
	}

	@Test
	void abEReceptorUniversal() {
		assertThat(CompatibilidadeSanguinea.isReceptorUniversal(TipoSanguineo.AB)).isTrue();
		assertThat(CompatibilidadeSanguinea.podeReceberDe(TipoSanguineo.AB, TipoSanguineo.O)).isTrue();
		assertThat(CompatibilidadeSanguinea.podeReceberDe(TipoSanguineo.AB, TipoSanguineo.A)).isTrue();
	}

	@Test
	void aNaoDoaParaB() {
		assertThat(CompatibilidadeSanguinea.podeDoarPara(TipoSanguineo.A, TipoSanguineo.B)).isFalse();
		assertThat(CompatibilidadeSanguinea.podeReceberDe(TipoSanguineo.A, TipoSanguineo.B)).isFalse();
	}

	@Test
	void abSoDoaParaAb() {
		assertThat(CompatibilidadeSanguinea.podeDoarPara(TipoSanguineo.AB, TipoSanguineo.A)).isFalse();
		assertThat(CompatibilidadeSanguinea.podeDoarPara(TipoSanguineo.AB, TipoSanguineo.AB)).isTrue();
	}

	@Test
	void tipoNuloNaoEDoadorNemReceptor() {
		assertThat(CompatibilidadeSanguinea.podeDoarPara(null, TipoSanguineo.A)).isFalse();
		assertThat(CompatibilidadeSanguinea.podeDoarPara(TipoSanguineo.A, null)).isFalse();
	}

}
