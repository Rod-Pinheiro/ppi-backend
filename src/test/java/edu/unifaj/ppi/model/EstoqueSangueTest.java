package edu.unifaj.ppi.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;

class EstoqueSangueTest {

	private final Hemocentro hemocentro = new Hemocentro("HEMO-001", "Central", "(11) 1", "h@h.com",
			LocalTime.of(7, 0), LocalTime.of(18, 0), false, 0,
			new Endereco("Av. x", "1", "Centro", "São Paulo", "SP", "00000-000"));

	@Test
	void novoEstoqueComecaZerado() {
		EstoqueSangue estoque = new EstoqueSangue(hemocentro, TipoSanguineo.O, FatorRh.POSITIVO, 5);

		assertThat(estoque.getQuantidadeDisponivel()).isZero();
		assertThat(estoque.isDisponivel()).isFalse();
	}

	@Test
	void adicionarSomaBolsasAoSaldo() {
		EstoqueSangue estoque = new EstoqueSangue(hemocentro, TipoSanguineo.O, FatorRh.POSITIVO, 5);

		estoque.adicionar(3);
		estoque.adicionar(2);

		assertThat(estoque.getQuantidadeDisponivel()).isEqualTo(5);
		assertThat(estoque.isDisponivel()).isTrue();
	}

	@Test
	void estoqueNoLimiteMinimoECritico() {
		EstoqueSangue estoque = new EstoqueSangue(hemocentro, TipoSanguineo.A, FatorRh.NEGATIVO, 5);
		estoque.adicionar(5);

		assertThat(estoque.isCritico()).isTrue();
	}

	@Test
	void acimaDoMinimoNaoECritico() {
		EstoqueSangue estoque = new EstoqueSangue(hemocentro, TipoSanguineo.A, FatorRh.NEGATIVO, 5);
		estoque.adicionar(6);

		assertThat(estoque.isCritico()).isFalse();
	}

	@Test
	void baixarNuncaDeixaSaldoNegativo() {
		EstoqueSangue estoque = new EstoqueSangue(hemocentro, TipoSanguineo.B, FatorRh.POSITIVO, 0);
		estoque.adicionar(2);

		assertThatThrownBy(() -> estoque.baixar(3)).isInstanceOf(IllegalArgumentException.class);
		assertThat(estoque.getQuantidadeDisponivel()).isEqualTo(2);
	}

	@Test
	void tipoCompletoUsaSimboloDoFator() {
		EstoqueSangue estoque = new EstoqueSangue(hemocentro, TipoSanguineo.O, FatorRh.NEGATIVO, 0);

		assertThat(estoque.getTipoCompleto()).isEqualTo("O-");
	}

}
