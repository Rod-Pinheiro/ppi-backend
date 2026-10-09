package edu.unifaj.ppi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.unifaj.ppi.dto.request.RegistrarColetaRequest;
import edu.unifaj.ppi.dto.response.BolsaSangueResponse;
import edu.unifaj.ppi.exception.ConflitoException;
import edu.unifaj.ppi.exception.RecursoNaoEncontradoException;
import edu.unifaj.ppi.exception.RegraDeNegocioException;
import edu.unifaj.ppi.model.Agendamento;
import edu.unifaj.ppi.model.BolsaSangue;
import edu.unifaj.ppi.model.Doador;
import edu.unifaj.ppi.model.Endereco;
import edu.unifaj.ppi.model.Hemocentro;
import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.StatusAgendamento;
import edu.unifaj.ppi.model.enums.TipoSanguineo;
import edu.unifaj.ppi.repository.AgendamentoRepository;
import edu.unifaj.ppi.repository.BolsaSangueRepository;
import edu.unifaj.ppi.repository.DoadorRepository;
import edu.unifaj.ppi.validation.Validador;
import edu.unifaj.ppi.validation.ValidadorData;

@ExtendWith(MockitoExtension.class)
class ColetaServiceTest {

	private static final String CPF = "12345678901";
	private static final Long AGENDAMENTO_ID = 10L;

	@Mock
	private AgendamentoRepository agendamentoRepository;

	@Mock
	private BolsaSangueRepository bolsaSangueRepository;

	@Mock
	private DoadorRepository doadorRepository;

	@Mock
	private EstoqueService estoqueService;

	@Mock
	private NotificacaoService notificacaoService;

	private ColetaService coletaService;

	private final Validador validador = new Validador();
	private final ValidadorData validadorData = new ValidadorData();

	private Doador doador;
	private Agendamento agendamento;

	@BeforeEach
	void setUp() {
		coletaService = new ColetaService(agendamentoRepository, bolsaSangueRepository, doadorRepository,
				estoqueService, notificacaoService, validador, validadorData);

		doador = new Doador("João", "joao@email.com", "hash", CPF, TipoSanguineo.O, FatorRh.POSITIVO);
		agendamento = new Agendamento(doador, hemocentro(), LocalDate.now().minusDays(1), LocalTime.of(8, 30));

		when(doadorRepository.findByCpf(CPF)).thenReturn(Optional.of(doador));
	}

	@Test
	void registraColetaEmAgendamentoElegivel() {
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento));
		when(bolsaSangueRepository.existsByAgendamentoId(AGENDAMENTO_ID)).thenReturn(false);
		when(bolsaSangueRepository.save(any(BolsaSangue.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

		BolsaSangueResponse resposta = coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(450, 3));

		assertThat(resposta.codigo()).startsWith("BS-");
		assertThat(resposta.volumeMl()).isEqualTo(450);
		assertThat(resposta.quantidade()).isEqualTo(3);
		assertThat(resposta.volumeTotalMl()).isEqualTo(1350);
		assertThat(resposta.tipoCompleto()).isEqualTo("O+");
		assertThat(agendamento.getStatus()).isEqualTo(StatusAgendamento.REALIZADO);
	}

	@Test
	void bolsaRecebeValidadeDe42DiasAposAColeta() {
		LocalDate coleta = LocalDate.of(2026, 4, 13);
		Agendamento agendamentoEmDataFixa = new Agendamento(doador, hemocentro(), coleta, LocalTime.of(8, 0));
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamentoEmDataFixa));
		when(bolsaSangueRepository.existsByAgendamentoId(AGENDAMENTO_ID)).thenReturn(false);
		when(bolsaSangueRepository.save(any(BolsaSangue.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

		BolsaSangueResponse resposta = coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(450, 1));

		assertThat(resposta.dataColeta()).isEqualTo(coleta);
		assertThat(resposta.dataValidade()).isEqualTo(LocalDate.of(2026, 5, 25));
	}

	@Test
	void agendamentoInexistenteRetornaNaoEncontrado() {
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(450, 1)))
				.isInstanceOf(RecursoNaoEncontradoException.class);
		verify(bolsaSangueRepository, never()).save(any());
	}

	@Test
	void agendamentoDeOutroDoadorNaoEVisivel() {
		Doador outro = new Doador("Maria", "maria@email.com", "hash", "99999999999", TipoSanguineo.A, FatorRh.NEGATIVO);
		Agendamento alheio = new Agendamento(outro, hemocentro(), LocalDate.now().minusDays(1), LocalTime.of(9, 0));
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(alheio));

		assertThatThrownBy(() -> coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(450, 1)))
				.isInstanceOf(RecursoNaoEncontradoException.class);
		verify(bolsaSangueRepository, never()).save(any());
	}

	@Test
	void agendamentoCanceladoNaoAceitaColeta() {
		agendamento.cancelar();
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento));

		assertThatThrownBy(() -> coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(450, 1)))
				.isInstanceOf(ConflitoException.class)
				.hasMessage("Agendamento cancelado não aceita coleta");
		verify(bolsaSangueRepository, never()).save(any());
	}

	@Test
	void agendamentoJaRealizadoNaoAceitaColeta() {
		agendamento.marcarRealizado();
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento));

		assertThatThrownBy(() -> coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(450, 1)))
				.isInstanceOf(ConflitoException.class);
		verify(bolsaSangueRepository, never()).save(any());
	}

	@Test
	void agendamentoComBolsaRegistradaNaoAceitaSegundaColeta() {
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento));
		when(bolsaSangueRepository.existsByAgendamentoId(AGENDAMENTO_ID)).thenReturn(true);

		assertThatThrownBy(() -> coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(450, 1)))
				.isInstanceOf(ConflitoException.class)
				.hasMessage("Este agendamento já tem coleta registrada");
		verify(bolsaSangueRepository, never()).save(any());
	}

	@Test
	void agendamentoFuturoNaoGeraColeta() {
		Agendamento futuro = new Agendamento(doador, hemocentro(), LocalDate.now().plusDays(2), LocalTime.of(9, 0));
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(futuro));
		when(bolsaSangueRepository.existsByAgendamentoId(AGENDAMENTO_ID)).thenReturn(false);

		assertThatThrownBy(() -> coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(450, 1)))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("Este agendamento não está mais disponível para registro");
		verify(bolsaSangueRepository, never()).save(any());
	}

	@Test
	void agendamentoDeHojeAceitaColeta() {
		Agendamento hoje = new Agendamento(doador, hemocentro(), LocalDate.now(), LocalTime.of(23, 0));
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(hoje));
		when(bolsaSangueRepository.existsByAgendamentoId(AGENDAMENTO_ID)).thenReturn(false);
		when(bolsaSangueRepository.save(any(BolsaSangue.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

		assertThat(coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(450, 1))).isNotNull();
	}

	@Test
	void volumeForaDoIntervaloERecusado() {
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento));
		when(bolsaSangueRepository.existsByAgendamentoId(AGENDAMENTO_ID)).thenReturn(false);

		assertThatThrownBy(() -> coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(100, 1)))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessage("Volume deve estar entre 200ml e 470ml");
		verify(bolsaSangueRepository, never()).save(any());
	}

	@Test
	void quantidadeForaDoIntervaloERecusada() {
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento));
		when(bolsaSangueRepository.existsByAgendamentoId(AGENDAMENTO_ID)).thenReturn(false);

		assertThatThrownBy(() -> coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(450, 9)))
				.isInstanceOf(RegraDeNegocioException.class);
		verify(bolsaSangueRepository, never()).save(any());
	}

	@Test
	void tipoSanguineoVemDoDoadorENaoDoRequest() {
		ArgumentCaptor<BolsaSangue> captor = ArgumentCaptor.forClass(BolsaSangue.class);
		when(agendamentoRepository.findById(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento));
		when(bolsaSangueRepository.existsByAgendamentoId(AGENDAMENTO_ID)).thenReturn(false);
		when(bolsaSangueRepository.save(captor.capture())).thenAnswer(invocacao -> captor.getValue());

		coletaService.registrarColeta(CPF, AGENDAMENTO_ID, requisicao(450, 2));

		assertThat(captor.getValue().getTipoCompleto()).isEqualTo("O+");
		assertThat(captor.getValue().getTipoSanguineo()).isEqualTo(TipoSanguineo.O);
		assertThat(captor.getValue().getFatorRh()).isEqualTo(FatorRh.POSITIVO);
	}

	private RegistrarColetaRequest requisicao(int volumeMl, int quantidade) {
		return new RegistrarColetaRequest(volumeMl, quantidade);
	}

	private Hemocentro hemocentro() {
		Endereco endereco = new Endereco("Av. Dr. Enéas", "155", "Cerqueira César", "São Paulo", "SP",
				"05403-000");
		return new Hemocentro("HEMO-001", "Hemocentro Central", "(11) 3069-6000", "hemosp@saude.sp.gov.br",
				LocalTime.of(7, 0), LocalTime.of(18, 0), false, 0, endereco);
	}

}