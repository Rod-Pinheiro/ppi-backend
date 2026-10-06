package edu.unifaj.ppi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.unifaj.ppi.dto.request.AtualizarPerfilRequest;
import edu.unifaj.ppi.dto.request.CadastroDoadorRequest;
import edu.unifaj.ppi.exception.ConflitoException;
import edu.unifaj.ppi.exception.CredenciaisInvalidasException;
import edu.unifaj.ppi.exception.RegraDeNegocioException;
import edu.unifaj.ppi.model.Doador;
import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;
import edu.unifaj.ppi.repository.BolsaSangueRepository;
import edu.unifaj.ppi.repository.DoadorRepository;
import edu.unifaj.ppi.validation.ConversorTipoSanguineo;
import edu.unifaj.ppi.validation.Validador;

@ExtendWith(MockitoExtension.class)
class DoadorServiceTest {

	private static final String CPF = "12345678901";

	@Mock
	private DoadorRepository doadorRepository;

	@Mock
	private BolsaSangueRepository bolsaSangueRepository;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private DoadorService doadorService;

	@BeforeEach
	void setUp() {
		doadorService = new DoadorService(doadorRepository, bolsaSangueRepository, passwordEncoder, new Validador(),
				new ConversorTipoSanguineo());
	}

	@Test
	void cadastroGuardaSenhaComHashEComCpfNormalizado() {
		when(doadorRepository.existsByEmailIgnoreCase("joao@email.com")).thenReturn(false);
		when(doadorRepository.existsByCpf(CPF)).thenReturn(false);
		when(doadorRepository.save(any(Doador.class)))
				.thenAnswer(invocacao -> invocacao.getArgument(0));

		doadorService.cadastrar(new CadastroDoadorRequest("João", "joao@email.com", "123456", "123.456.789-01", "O+"));

		var captor = ArgumentCaptor.forClass(Doador.class);
		verify(doadorRepository).save(captor.capture());
		Doador salvo = captor.getValue();

		assertThat(salvo.getCpf()).isEqualTo(CPF);
		assertThat(salvo.getSenha()).isNotEqualTo("123456");
		assertThat(passwordEncoder.matches("123456", salvo.getSenha())).isTrue();
		assertThat(salvo.getTipoCompleto()).isEqualTo("O+");
	}

	@Test
	void emailJaCadastradoRetornaConflito() {
		when(doadorRepository.existsByEmailIgnoreCase("joao@email.com")).thenReturn(true);

		assertThatThrownBy(
				() -> doadorService.cadastrar(new CadastroDoadorRequest("João", "joao@email.com", "123456", CPF, "O+")))
				.isInstanceOf(ConflitoException.class)
				.hasMessage("Este email já está cadastrado");
		verify(doadorRepository, never()).save(any());
	}

	@Test
	void cpfJaCadastradoRetornaConflito() {
		when(doadorRepository.existsByEmailIgnoreCase("joao@email.com")).thenReturn(false);
		when(doadorRepository.existsByCpf(CPF)).thenReturn(true);

		assertThatThrownBy(
				() -> doadorService.cadastrar(new CadastroDoadorRequest("João", "joao@email.com", "123456", CPF, "O+")))
				.isInstanceOf(ConflitoException.class)
				.hasMessage("Este CPF já está cadastrado");
	}

	@Test
	void senhaCurtaNoCadastroRecusada() {
		assertThatThrownBy(() -> doadorService
				.cadastrar(new CadastroDoadorRequest("João", "joao@email.com", "123", CPF, "O+")))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("Senha deve ter pelo menos 6 caracteres");
	}

	@Test
	void cpfInvalidoNoCadastroRecusado() {
		assertThatThrownBy(() -> doadorService
				.cadastrar(new CadastroDoadorRequest("João", "joao@email.com", "123456", "123", "O+")))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("CPF inválido");
	}

	@Test
	void tipoSanguineoInvalidoNoCadastroRecusado() {
		assertThatThrownBy(() -> doadorService
				.cadastrar(new CadastroDoadorRequest("João", "joao@email.com", "123456", CPF, "Z+")))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("Tipo sanguíneo inválido");
	}

	@Test
	void loginComSenhaCorretaPassa() {
		Doador doador = doadorComSenha("123456");
		when(doadorRepository.findByEmailIgnoreCase("joao@email.com")).thenReturn(Optional.of(doador));

		assertThat(doadorService.login("joao@email.com", "123456").email()).isEqualTo("joao@email.com");
	}

	@Test
	void loginComSenhaErradaFalha() {
		when(doadorRepository.findByEmailIgnoreCase("joao@email.com")).thenReturn(Optional.of(doadorComSenha("123456")));

		assertThatThrownBy(() -> doadorService.login("joao@email.com", "errada"))
				.isInstanceOf(CredenciaisInvalidasException.class);
	}

	@Test
	void loginDeEmailInexistenteDaAMesmaMensagemDaSenhaErrada() {
		when(doadorRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> doadorService.login("ninguem@email.com", "123456"))
				.isInstanceOf(CredenciaisInvalidasException.class)
				.hasMessage("Email ou senha incorretos");
	}

	@Test
	void respostaDeLoginNaoExpoeSenha() {
		when(doadorRepository.findByEmailIgnoreCase("joao@email.com")).thenReturn(Optional.of(doadorComSenha("123456")));

		var resposta = doadorService.login("joao@email.com", "123456");

		// O record nao tem campo de senha, entao a checagem e sobre o contrato.
		assertThat(resposta.toString()).doesNotContain("hash").doesNotContain("$2a$");
	}

	@Test
	void doisCamposDeSenhaEmBrancoMantemASenhaAtual() {
		Doador doador = doadorComSenha("123456");
		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.of(doador));
		when(doadorRepository.save(any(Doador.class)))
				.thenAnswer(invocacao -> invocacao.getArgument(0));

		doadorService.atualizarPerfil(CPF, new AtualizarPerfilRequest("João Novo", "joao@email.com", null, null));

		assertThat(passwordEncoder.matches("123456", doador.getSenha())).isTrue();
		assertThat(doador.getNome()).isEqualTo("João Novo");
	}

	@Test
	void apenasUmCampoDeSenhaPreenchidoERecusado() {
		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.of(doadorComSenha("123456")));

		assertThatThrownBy(() -> doadorService
				.atualizarPerfil(CPF, new AtualizarPerfilRequest("João", "joao@email.com", "123456", null)))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("Preencha a senha atual e a nova senha");
	}

	@Test
	void senhaAtualErradaRecusaATroca() {
		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.of(doadorComSenha("123456")));

		assertThatThrownBy(() -> doadorService
				.atualizarPerfil(CPF, new AtualizarPerfilRequest("João", "joao@email.com", "errada", "654321")))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("Senha atual incorreta");
	}

	@Test
	void trocaDeSenhaValidaRedefineOHash() {
		Doador doador = doadorComSenha("123456");
		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.of(doador));
		when(doadorRepository.save(any(Doador.class)))
				.thenAnswer(invocacao -> invocacao.getArgument(0));

		doadorService.atualizarPerfil(CPF, new AtualizarPerfilRequest("João", "joao@email.com", "123456", "nova123"));

		assertThat(passwordEncoder.matches("nova123", doador.getSenha())).isTrue();
		assertThat(doador.getSenha()).isNotEqualTo("nova123");
	}

	@Test
	void editarPerfilNaoMexeNoCpfNemNoTipo() {
		Doador doador = doadorComSenha("123456");
		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.of(doador));
		when(doadorRepository.save(any(Doador.class)))
				.thenAnswer(invocacao -> invocacao.getArgument(0));

		doadorService.atualizarPerfil(CPF, new AtualizarPerfilRequest("Outro Nome", "novo@email.com", null, null));

		assertThat(doador.getCpf()).isEqualTo(CPF);
		assertThat(doador.getTipoSanguineo()).isEqualTo(TipoSanguineo.O);
		assertThat(doador.getFatorRh()).isEqualTo(FatorRh.POSITIVO);
	}

	private Doador doadorComSenha(String senha) {
		return new Doador("João", "joao@email.com", passwordEncoder.encode(senha), CPF, TipoSanguineo.O,
				FatorRh.POSITIVO);
	}

}