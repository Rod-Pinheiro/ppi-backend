package edu.unifaj.ppi.model;

import edu.unifaj.ppi.model.enums.FatorRh;
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
import jakarta.persistence.Table;

/**
 * Saldo de bolsas por hemocentro e por tipo sanguineo. Cada coleta registrada
 * soma as bolsas coletadas aqui; a tela de estoque le esse saldo e destaco o que
 * esta no limite minimo.
 *
 * <p>O par (hemocentro, tipo, fator) e unico: um estoque por combinacao, e nao
 * um registro por entrada, senao o saldo ficaria espalhado e dificil de somar.
 */
@Entity
@Table(name = "estoque_sangue")
public class EstoqueSangue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "hemocentro_id", nullable = false)
	private Hemocentro hemocentro;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_sanguineo", nullable = false)
	private TipoSanguineo tipoSanguineo;

	@Enumerated(EnumType.STRING)
	@Column(name = "fator_rh", nullable = false)
	private FatorRh fatorRh;

	@Column(name = "quantidade_disponivel", nullable = false)
	private int quantidadeDisponivel;

	@Column(name = "quantidade_minima", nullable = false)
	private int quantidadeMinima;

	protected EstoqueSangue() {
	}

	public EstoqueSangue(Hemocentro hemocentro, TipoSanguineo tipoSanguineo, FatorRh fatorRh, int quantidadeMinima) {
		this.hemocentro = hemocentro;
		this.tipoSanguineo = tipoSanguineo;
		this.fatorRh = fatorRh;
		this.quantidadeMinima = quantidadeMinima;
		this.quantidadeDisponivel = 0;
	}

	/** Entrada de bolsas: toda coleta registrada chama este metodo. */
	public void adicionar(int bolsas) {
		if (bolsas < 0) {
			throw new IllegalArgumentException("Bolsas não pode ser negativo");
		}
		this.quantidadeDisponivel += bolsas;
	}

	/** Saida de bolsas; nunca deixa o saldo negativo. */
	public void baixar(int bolsas) {
		if (bolsas < 0 || bolsas > quantidadeDisponivel) {
			throw new IllegalArgumentException("Baixa maior que o saldo disponível");
		}
		this.quantidadeDisponivel -= bolsas;
	}

	public boolean isCritico() {
		return quantidadeDisponivel <= quantidadeMinima;
	}

	public boolean isDisponivel() {
		return quantidadeDisponivel > 0;
	}

	public String getTipoCompleto() {
		if (tipoSanguineo == null || fatorRh == null) {
			return null;
		}
		return tipoSanguineo.getValor() + fatorRh.getValor();
	}

	public Long getId() {
		return id;
	}

	public Hemocentro getHemocentro() {
		return hemocentro;
	}

	public TipoSanguineo getTipoSanguineo() {
		return tipoSanguineo;
	}

	public FatorRh getFatorRh() {
		return fatorRh;
	}

	public int getQuantidadeDisponivel() {
		return quantidadeDisponivel;
	}

	public int getQuantidadeMinima() {
		return quantidadeMinima;
	}

	public void setQuantidadeMinima(int quantidadeMinima) {
		this.quantidadeMinima = quantidadeMinima;
	}

}
