package edu.unifaj.ppi.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Triagem clinica feita antes da doacao. E o registro que diz se o doador estava
 * apto naquele dia: peso, pressao e hemoglobina. Nao e um atributo do doador
 * porque muda a cada visita.
 */
@Entity
@Table(name = "triagem_doador")
public class TriagemDoador {

	public static final double PESO_MINIMO_KG = 50.0;
	public static final double HEMOGLOBINA_MINIMA = 12.5;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "doador_id", nullable = false)
	private Doador doador;

	@Column(name = "data", nullable = false)
	private LocalDate data;

	@Column(name = "peso_kg", nullable = false)
	private double pesoKg;

	@Column(name = "pressao_arterial", nullable = false)
	private String pressaoArterial;

	@Column(name = "hemoglobina", nullable = false)
	private double hemoglobina;

	@Column(name = "apto", nullable = false)
	private boolean apto;

	@Column(name = "observacoes")
	private String observacoes;

	protected TriagemDoador() {
	}

	public TriagemDoador(Doador doador, LocalDate data, double pesoKg, String pressaoArterial, double hemoglobina) {
		this.doador = doador;
		this.data = data;
		this.pesoKg = pesoKg;
		this.pressaoArterial = pressaoArterial;
		this.hemoglobina = hemoglobina;
		this.apto = pesoKg >= PESO_MINIMO_KG && hemoglobina >= HEMOGLOBINA_MINIMA;
	}

	/** Regra de aptidao, isolada para poder ser testada sem banco. */
	public static boolean isApto(double pesoKg, double hemoglobina) {
		return pesoKg >= PESO_MINIMO_KG && hemoglobina >= HEMOGLOBINA_MINIMA;
	}

	public String getMotivoInaptidao() {
		if (apto) {
			return null;
		}
		if (pesoKg < PESO_MINIMO_KG) {
			return "Peso abaixo de " + (int) PESO_MINIMO_KG + " kg";
		}
		return "Hemoglobina abaixo de " + HEMOGLOBINA_MINIMA;
	}

	public Long getId() {
		return id;
	}

	public Doador getDoador() {
		return doador;
	}

	public LocalDate getData() {
		return data;
	}

	public double getPesoKg() {
		return pesoKg;
	}

	public String getPressaoArterial() {
		return pressaoArterial;
	}

	public double getHemoglobina() {
		return hemoglobina;
	}

	public boolean isApto() {
		return apto;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

}
