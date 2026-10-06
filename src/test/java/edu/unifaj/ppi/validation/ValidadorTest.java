package edu.unifaj.ppi.validation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import edu.unifaj.ppi.exception.RegraDeNegocioException;

class ValidadorTest {

	private final Validador validador = new Validador();

	@ParameterizedTest
	@ValueSource(strings = { "joao@email.com", "a.b+c@dominio.co.uk" })
	void aceitaEmailValido(String email) {
		assertThatCode(() -> validador.validarEmail(email)).doesNotThrowAnyException();
	}

	@ParameterizedTest
	@ValueSource(strings = { "sem-arroba", "joao@", "@dominio.com", "joao@dominio" })
	void rejeitaEmailInvalido(String email) {
		assertThatThrownBy(() -> validador.validarEmail(email))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("Email inválido");
	}

	@Test
	void senhaPrecisaDeSeisCaracteres() {
		assertThatCode(() -> validador.validarSenha("123456")).doesNotThrowAnyException();
		assertThatThrownBy(() -> validador.validarSenha("12345"))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("Senha deve ter pelo menos 6 caracteres");
	}

	@Test
	void cpfAceitaOnzeDigitosComOuSemMascara() {
		assertThatCode(() -> validador.validarCpf("12345678901")).doesNotThrowAnyException();
		assertThatCode(() -> validador.validarCpf("123.456.789-01")).doesNotThrowAnyException();
	}

	@Test
	void cpfDeComprimentoErradoEInvalido() {
		assertThatThrownBy(() -> validador.validarCpf("1234567890"))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("CPF inválido");
	}

	@Test
	void cpfSemVerificacaoDeDigitoComoNoApp() {
		// Regra herdada do ValidacaoUtils: conta digitos e nada mais.
		assertThatCode(() -> validador.validarCpf("00000000000")).doesNotThrowAnyException();
	}

	@Test
	void volumeAceita200A470() {
		assertThatCode(() -> validador.validarVolumeDoacao(200)).doesNotThrowAnyException();
		assertThatCode(() -> validador.validarVolumeDoacao(470)).doesNotThrowAnyException();
	}

	@Test
	void volumeForaDoIntervaloEInvalido() {
		assertThatThrownBy(() -> validador.validarVolumeDoacao(199))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("Volume deve estar entre 200ml e 470ml");
		assertThatThrownBy(() -> validador.validarVolumeDoacao(471))
				.isInstanceOf(RegraDeNegocioException.class);
	}

	@Test
	void quantidadeAceita1A6() {
		assertThatCode(() -> validador.validarQuantidadeBolsas(1)).doesNotThrowAnyException();
		assertThatCode(() -> validador.validarQuantidadeBolsas(6)).doesNotThrowAnyException();
		assertThatThrownBy(() -> validador.validarQuantidadeBolsas(0))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("A quantidade deve estar entre 1 e 6 bolsas");
		assertThatThrownBy(() -> validador.validarQuantidadeBolsas(7))
				.isInstanceOf(RegraDeNegocioException.class);
	}

	@Test
	void campoObrigatorioApontaAPosicao() {
		assertThatThrownBy(() -> validador.validarCamposObrigatorios("nome", "email", "  "))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("Campo 3 é obrigatório");
	}

}