package edu.unifaj.ppi.model;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.StatusBolsa;
import edu.unifaj.ppi.model.enums.TipoSanguineo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Lote de bolsas coletado em um agendamento.
 *
 * <p>A bolsa nasce espelhando o agendamento — data, hemocentro e validade — para
 * que o registro nunca divirja do que foi agendado. O tipo sanguíneo, esse sim,
 * vem do doador.
 */
@Entity
@Table(name = "bolsa_sangue")
public class BolsaSangue {

	/** Regra de validade do sangue doado: sempre 42 dias apos a coleta. */
	public static final int DIAS_VALIDADE = 42;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "codigo", nullable = false)
	private String codigo;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_sanguineo", nullable = false)
	private TipoSanguineo tipoSanguineo;

	@Enumerated(EnumType.STRING)
	@Column(name = "fator_rh", nullable = false)
	private FatorRh fatorRh;

	@Column(name = "data_coleta", nullable = false)
	private LocalDate dataColeta;

	@Column(name = "data_validade", nullable = false)
	private LocalDate dataValidade;

	/** Volume por bolsa, nao o total da sessao. */
	@Column(name = "volume_ml", nullable = false)
	private int volumeMl;

	/** Quantas bolsas sairam na sessao. */
	@Column(name = "quantidade", nullable = false)
	private int quantidade;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private StatusBolsa status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "hemocentro_origem_id", nullable = false)
	private Hemocentro hemocentroOrigem;

	// OneToOne porque o UNIQUE em agendamento_id garante um lote por agendamento;
	// ManyToOne deixaria o modelo permitir dois sem o banco impedir.
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "agendamento_id", nullable = false)
	private Agendamento agendamento;

	@Column(name = "receptor_cpf")
	private String receptorCpf;

	@Column(name = "data_destinacao")
	private LocalDate dataDestinacao;

	protected BolsaSangue() {
	}

	public BolsaSangue(String codigo, TipoSanguineo tipoSanguineo, FatorRh fatorRh, LocalDate dataColeta,
			int volumeMl, int quantidade) {
		this.codigo = codigo;
		this.tipoSanguineo = tipoSanguineo;
		this.fatorRh = fatorRh;
		this.dataColeta = dataColeta;
		this.quantidade = quantidade;
		this.volumeMl = volumeMl;
	}

	/**
	 * Fabrica de porta unica para bolsa vinda de agendamento: e o que garante que
	 * data, local e validade sejam sempre os do agendamento.
	 */
	public static BolsaSangue fromAgendamento(Agendamento agendamento, TipoSanguineo tipoSanguineo,
			FatorRh fatorRh, int volumeMl, int quantidade) {
		LocalDate dataColeta = agendamento.getData();
		BolsaSangue bolsa = new BolsaSangue(gerarCodigo(), tipoSanguineo, fatorRh, dataColeta, volumeMl, quantidade);
		bolsa.setHemocentroOrigem(agendamento.getHemocentro());
		bolsa.setAgendamento(agendamento);
		bolsa.setDataValidade(dataColeta.plusDays(DIAS_VALIDADE));
		bolsa.setStatus(StatusBolsa.DISPONIVEL);
		return bolsa;
	}

	public static String gerarCodigo() {
		return "BS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
	}

	public int getVolumeTotalMl() {
		return volumeMl * Math.max(quantidade, 1);
	}

	public String getTipoCompleto() {
		if (tipoSanguineo == null || fatorRh == null) {
			return null;
		}
		return tipoSanguineo.getValor() + fatorRh.getValor();
	}

	public boolean isDisponivel() {
		return status == StatusBolsa.DISPONIVEL;
	}

	public Long getId() {
		return id;
	}

	public String getCodigo() {
		return codigo;
	}

	public TipoSanguineo getTipoSanguineo() {
		return tipoSanguineo;
	}

	public FatorRh getFatorRh() {
		return fatorRh;
	}

	public LocalDate getDataColeta() {
		return dataColeta;
	}

	public void setDataColeta(LocalDate dataColeta) {
		this.dataColeta = dataColeta;
	}

	public LocalDate getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(LocalDate dataValidade) {
		this.dataValidade = dataValidade;
	}

	public int getVolumeMl() {
		return volumeMl;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public StatusBolsa getStatus() {
		return status;
	}

	public void setStatus(StatusBolsa status) {
		this.status = status;
	}

	public Hemocentro getHemocentroOrigem() {
		return hemocentroOrigem;
	}

	public void setHemocentroOrigem(Hemocentro hemocentroOrigem) {
		this.hemocentroOrigem = hemocentroOrigem;
	}

	public Agendamento getAgendamento() {
		return agendamento;
	}

	public void setAgendamento(Agendamento agendamento) {
		this.agendamento = agendamento;
	}

	public String getReceptorCpf() {
		return receptorCpf;
	}

	public LocalDate getDataDestinacao() {
		return dataDestinacao;
	}

}