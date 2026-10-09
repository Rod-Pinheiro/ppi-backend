package edu.unifaj.ppi.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Base de todo ser humano do dominio. No app essa ideia existia como
 * {@code User}, com {@code Doador} herdando dela; aqui ela volta como
 * {@code @MappedSuperclass} para que a heranca apareca no modelo sem criar uma
 * segunda tabela: nome e email continuam nas colunas de {@code doador}.
 */
@MappedSuperclass
public abstract class Pessoa {

	@Column(name = "nome", nullable = false)
	protected String nome;

	@Column(name = "email", nullable = false)
	protected String email;

	protected Pessoa() {
	}

	protected Pessoa(String nome, String email) {
		this.nome = nome;
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
