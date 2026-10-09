package edu.unifaj.ppi.model;

import java.time.LocalDateTime;

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
 * Aviso enviado ao doador: resultado da triagem, estoque critico no hemocentro
 * onde ele costuma doar, lembrete do nivel. O doador marca como lida, e nao
 * apaga, para que o historico de avisos continue auditavel.
 */
@Entity
@Table(name = "notificacao")
public class Notificacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "doador_id", nullable = false)
	private Doador doador;

	@Column(name = "titulo", nullable = false)
	private String titulo;

	@Column(name = "mensagem", nullable = false)
	private String mensagem;

	@Column(name = "criada_em", nullable = false)
	private LocalDateTime criadaEm;

	@Column(name = "lida", nullable = false)
	private boolean lida;

	protected Notificacao() {
	}

	public Notificacao(Doador doador, String titulo, String mensagem) {
		this.doador = doador;
		this.titulo = titulo;
		this.mensagem = mensagem;
		this.criadaEm = LocalDateTime.now();
		this.lida = false;
	}

	public void marcarLida() {
		this.lida = true;
	}

	public Long getId() {
		return id;
	}

	public Doador getDoador() {
		return doador;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public LocalDateTime getCriadaEm() {
		return criadaEm;
	}

	public boolean isLida() {
		return lida;
	}

}
