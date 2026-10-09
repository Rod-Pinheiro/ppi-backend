package edu.unifaj.ppi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.unifaj.ppi.dto.response.NotificacaoResponse;
import edu.unifaj.ppi.exception.RecursoNaoEncontradoException;
import edu.unifaj.ppi.model.Doador;
import edu.unifaj.ppi.model.Notificacao;
import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;
import edu.unifaj.ppi.repository.DoadorRepository;
import edu.unifaj.ppi.repository.NotificacaoRepository;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

	private static final String CPF = "12345678901";

	@Mock
	private NotificacaoRepository notificacaoRepository;

	@Mock
	private DoadorRepository doadorRepository;

	private NotificacaoService notificacaoService;
	private Doador doador;

	@BeforeEach
	void setUp() {
		notificacaoService = new NotificacaoService(notificacaoRepository, doadorRepository);
		doador = new Doador("João", "joao@email.com", "hash", CPF, TipoSanguineo.O, FatorRh.POSITIVO);
	}

	@Test
	void notificarCriaNotificacaoNaoLida() {
		when(notificacaoRepository.save(any(Notificacao.class))).thenAnswer(i -> i.getArgument(0));

		notificacaoService.notificar(doador, "Olá", "Mensagem");

		ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
		verify(notificacaoRepository).save(captor.capture());
		assertThat(captor.getValue().isLida()).isFalse();
		assertThat(captor.getValue().getTitulo()).isEqualTo("Olá");
	}

	@Test
	void marcarLidaDeNotificacaoInexistenteFalha() {
		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.of(doador));
		when(notificacaoRepository.findByIdAndDoadorCpf(1L, CPF)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> notificacaoService.marcarLida(CPF, 1L))
				.isInstanceOf(RecursoNaoEncontradoException.class);
	}

	@Test
	void marcarLidaAlteraOEstado() {
		Notificacao notificacao = new Notificacao(doador, "Olá", "Mensagem");
		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.of(doador));
		when(notificacaoRepository.findByIdAndDoadorCpf(1L, CPF)).thenReturn(Optional.of(notificacao));
		when(notificacaoRepository.save(any(Notificacao.class))).thenAnswer(i -> i.getArgument(0));

		NotificacaoResponse resposta = notificacaoService.marcarLida(CPF, 1L);

		assertThat(resposta.lida()).isTrue();
	}

	@Test
	void contarNaoLidasDelegaAoRepositorio() {
		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.of(doador));
		when(notificacaoRepository.countByDoadorCpfAndLidaFalse(CPF)).thenReturn(2L);

		assertThat(notificacaoService.contarNaoLidas(CPF)).isEqualTo(2L);
	}

}
