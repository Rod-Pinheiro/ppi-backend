package edu.unifaj.ppi.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.StatusBolsa;
import edu.unifaj.ppi.model.enums.TipoSanguineo;

class BolsaSangueTest {

	private final Doador doador = new Doador("João", "joao@email.com", "hash", "12345678901", TipoSanguineo.O,
			FatorRh.POSITIVO);

	private final Hemocentro hemocentro = hemocentro("HEMO-001");

	@Test
	void bolsaHerdaDataLocalEValidadeDoAgendamento() {
		LocalDate data = LocalDate.now().minusDays(1);
		Agendamento agendamento = new Agendamento(doador, hemocentro, data, LocalTime.of(8, 30));

		BolsaSangue bolsa = BolsaSangue.fromAgendamento(agendamento, TipoSanguineo.O, FatorRh.POSITIVO, 450, 3);

		assertThat(bolsa.getDataColeta()).isEqualTo(data);
		assertThat(bolsa.getHemocentroOrigem()).isSameAs(hemocentro);
		assertThat(bolsa.getAgendamento()).isSameAs(agendamento);
	}

	@Test
	void validadeEDaColetaMais42Dias() {
		LocalDate coleta = LocalDate.of(2026, 4, 13);
		Agendamento agendamento = new Agendamento(doador, hemocentro, coleta, LocalTime.of(8, 0));

		BolsaSangue bolsa = BolsaSangue.fromAgendamento(agendamento, TipoSanguineo.O, FatorRh.POSITIVO, 450, 1);

		assertThat(bolsa.getDataValidade()).isEqualTo(LocalDate.of(2026, 5, 25));
		assertThat(BolsaSangue.DIAS_VALIDADE).isEqualTo(42);
	}

	@Test
	void bolsaNasceDisponivel() {
		Agendamento agendamento = new Agendamento(doador, hemocentro, LocalDate.now(), LocalTime.of(9, 0));

		BolsaSangue bolsa = BolsaSangue.fromAgendamento(agendamento, TipoSanguineo.O, FatorRh.POSITIVO, 450, 1);

		assertThat(bolsa.getStatus()).isEqualTo(StatusBolsa.DISPONIVEL);
		assertThat(bolsa.isDisponivel()).isTrue();
	}

	@Test
	void codigoTemPrefixoBSEOitoCaracteres() {
		String codigo = BolsaSangue.gerarCodigo();

		assertThat(codigo).startsWith("BS-").hasSize(11);
		assertThat(codigo.substring(3)).matches("[0-9A-F]{8}");
	}

	@Test
	void volumeTotalEVolumePorBolsaVezesQuantidade() {
		BolsaSangue bolsa = BolsaSangue.fromAgendamento(
				new Agendamento(doador, hemocentro, LocalDate.now(), LocalTime.of(9, 0)), TipoSanguineo.O,
				FatorRh.POSITIVO, 450, 6);

		assertThat(bolsa.getVolumeMl()).isEqualTo(450);
		assertThat(bolsa.getQuantidade()).isEqualTo(6);
		assertThat(bolsa.getVolumeTotalMl()).isEqualTo(2700);
	}

	@Test
	void tipoCompletoMontaComOSimboloDoFatorRh() {
		BolsaSangue bolsa = BolsaSangue.fromAgendamento(
				new Agendamento(doador, hemocentro, LocalDate.now(), LocalTime.of(9, 0)), TipoSanguineo.O,
				FatorRh.POSITIVO, 450, 1);

		assertThat(bolsa.getTipoCompleto()).isEqualTo("O+");
	}

	@Test
	void agendamentoNascePendenteEEAceitaTransicoes() {
		Agendamento agendamento = new Agendamento(doador, hemocentro, LocalDate.now(), LocalTime.of(9, 0));

		assertThat(agendamento.isPendente()).isTrue();

		agendamento.confirmar();
		assertThat(agendamento.isConfirmado()).isTrue();

		agendamento.marcarRealizado();
		assertThat(agendamento.isRealizado()).isTrue();
	}

	@Test
	void cancelamentoMudaParaCancelado() {
		Agendamento agendamento = new Agendamento(doador, hemocentro, LocalDate.now(), LocalTime.of(9, 0));

		agendamento.cancelar();

		assertThat(agendamento.isCancelado()).isTrue();
		assertThat(agendamento.isPendente()).isFalse();
	}

	@Test
	void tipoCompletoDoDoadorMontaIgualAoDaBolsa() {
		assertThat(doador.getTipoCompleto()).isEqualTo("O+");
	}

	private Hemocentro hemocentro(String id) {
		Endereco endereco = new Endereco("Av. Dr. Enéas", "155", "Cerqueira César", "São Paulo", "SP",
				"05403-000");
		return new Hemocentro(id, "Hemocentro Central", "(11) 3069-6000", "hemosp@saude.sp.gov.br",
				LocalTime.of(7, 0), LocalTime.of(18, 0), false, 0, endereco);
	}

}