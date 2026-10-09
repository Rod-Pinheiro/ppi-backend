package edu.unifaj.ppi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.unifaj.ppi.dto.request.RegistrarTriagemRequest;
import edu.unifaj.ppi.dto.response.TriagemResponse;
import edu.unifaj.ppi.exception.RecursoNaoEncontradoException;
import edu.unifaj.ppi.model.Doador;
import edu.unifaj.ppi.model.TriagemDoador;
import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;
import edu.unifaj.ppi.repository.DoadorRepository;
import edu.unifaj.ppi.repository.TriagemDoadorRepository;

@ExtendWith(MockitoExtension.class)
class TriagemServiceTest {

	private static final String CPF = "12345678901";

	@Mock
	private TriagemDoadorRepository triagemRepository;

	@Mock
	private DoadorRepository doadorRepository;

	private TriagemService triagemService;

	@BeforeEach
	void setUp() {
		triagemService = new TriagemService(triagemRepository, doadorRepository);
	}

	@Test
	void registraTriagemComAptidaoCalculada() {
		Doador doador = new Doador("João", "joao@email.com", "hash", CPF, TipoSanguineo.O, FatorRh.POSITIVO);
		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.of(doador));
		when(triagemRepository.save(any(TriagemDoador.class))).thenAnswer(i -> i.getArgument(0));

		TriagemResponse resposta = triagemService.registrar(CPF,
				new RegistrarTriagemRequest(LocalDate.now(), 70.0, "120/80", 14.0, "ok"));

		assertThat(resposta.apto()).isTrue();
		assertThat(resposta.observacoes()).isEqualTo("ok");
	}

	@Test
	void registraTriagemInaptaPorPeso() {
		Doador doador = new Doador("João", "joao@email.com", "hash", CPF, TipoSanguineo.O, FatorRh.POSITIVO);
		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.of(doador));
		when(triagemRepository.save(any(TriagemDoador.class))).thenAnswer(i -> i.getArgument(0));

		TriagemResponse resposta = triagemService.registrar(CPF,
				new RegistrarTriagemRequest(LocalDate.now(), 45.0, "120/80", 14.0, null));

		assertThat(resposta.apto()).isFalse();
		assertThat(resposta.motivoInaptidao()).contains("Peso");
	}

	@Test
	void doadorInexistenteFalha() {
		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> triagemService.registrar(CPF,
				new RegistrarTriagemRequest(LocalDate.now(), 70.0, "120/80", 14.0, null)))
				.isInstanceOf(RecursoNaoEncontradoException.class);
	}

}
