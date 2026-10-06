package edu.unifaj.ppi.model;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Catalogo de hemocentros. O id e o codigo de negocio "HEMO-001" e nao um
 * surrogate gerado, porque ele ja aparecia no app e no agendamento do doador.
 */
@Entity
@Table(name = "hemocentro")
public class Hemocentro {

	@Id
	@Column(name = "id", length = 20)
	private String id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "telefone", nullable = false)
	private String telefone;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "horario_abertura", nullable = false)
	private LocalTime horarioAbertura;

	@Column(name = "horario_fechamento", nullable = false)
	private LocalTime horarioFechamento;

	@Column(name = "funcionamento_24_horas", nullable = false)
	private boolean funcionamento24Horas;

	@Column(name = "capacidade_coleta", nullable = false)
	private double capacidadeColeta;

	// EAGER de proposito: o endereco e sempre exibido junto do hemocentro e o
	// catalogo e pequeno, entao buscar junto e mais barato que um segundo select.
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "endereco_id", nullable = false)
	private Endereco endereco;

	protected Hemocentro() {
	}

	// O id e chave natural do catalogo ("HEMO-001"), nao um surrogate, entao
	// existe um construtor que o recebe. Serve ao seed e aos testes.
	public Hemocentro(String id, String nome, String telefone, String email, LocalTime horarioAbertura,
			LocalTime horarioFechamento, boolean funcionamento24Horas, double capacidadeColeta, Endereco endereco) {
		this.id = id;
		this.nome = nome;
		this.telefone = telefone;
		this.email = email;
		this.horarioAbertura = horarioAbertura;
		this.horarioFechamento = horarioFechamento;
		this.funcionamento24Horas = funcionamento24Horas;
		this.capacidadeColeta = capacidadeColeta;
		this.endereco = endereco;
	}

	public String getHorarioFuncionamento() {
		if (funcionamento24Horas) {
			return "24 horas";
		}
		return horarioAbertura + " - " + horarioFechamento;
	}

	public String getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public String getEmail() {
		return email;
	}

	public LocalTime getHorarioAbertura() {
		return horarioAbertura;
	}

	public LocalTime getHorarioFechamento() {
		return horarioFechamento;
	}

	public boolean isFuncionamento24Horas() {
		return funcionamento24Horas;
	}

	public double getCapacidadeColeta() {
		return capacidadeColeta;
	}

	public Endereco getEndereco() {
		return endereco;
	}

}