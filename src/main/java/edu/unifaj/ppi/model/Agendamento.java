package edu.unifaj.ppi.model;

import java.time.LocalDate;
import java.time.LocalTime;

import edu.unifaj.ppi.model.enums.StatusAgendamento;

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
 * Agendamento de coleta. Nasce sempre PENDENTE e as transicoes ficam aqui
 * justamente para nenhum controller alterar {@code status} direto.
 */
@Entity
@Table(name = "agendamento")
public class Agendamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "doador_id", nullable = false)
	private Doador doador;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "hemocentro_id", nullable = false)
	private Hemocentro hemocentro;

	@Column(name = "data", nullable = false)
	private LocalDate data;

	@Column(name = "hora", nullable = false)
	private LocalTime hora;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private StatusAgendamento status;

	@Column(name = "data_confirmacao")
	private LocalDate dataConfirmacao;

	@Column(name = "observacoes")
	private String observacoes;

	protected Agendamento() {
	}

	public Agendamento(Doador doador, Hemocentro hemocentro, LocalDate data, LocalTime hora) {
		this.doador = doador;
		this.hemocentro = hemocentro;
		this.data = data;
		this.hora = hora;
		this.status = StatusAgendamento.PENDENTE;
	}

	public void confirmar() {
		this.status = StatusAgendamento.CONFIRMADO;
	}

	public void cancelar() {
		this.status = StatusAgendamento.CANCELADO;
	}

	public void marcarRealizado() {
		this.status = StatusAgendamento.REALIZADO;
	}

	public boolean isPendente() {
		return status == StatusAgendamento.PENDENTE;
	}

	public boolean isConfirmado() {
		return status == StatusAgendamento.CONFIRMADO;
	}

	public boolean isCancelado() {
		return status == StatusAgendamento.CANCELADO;
	}

	public boolean isRealizado() {
		return status == StatusAgendamento.REALIZADO;
	}

	public Long getId() {
		return id;
	}

	public Doador getDoador() {
		return doador;
	}

	public Hemocentro getHemocentro() {
		return hemocentro;
	}

	public LocalDate getData() {
		return data;
	}

	public LocalTime getHora() {
		return hora;
	}

	public StatusAgendamento getStatus() {
		return status;
	}

	public LocalDate getDataConfirmacao() {
		return dataConfirmacao;
	}

	public String getObservacoes() {
		return observacoes;
	}

}