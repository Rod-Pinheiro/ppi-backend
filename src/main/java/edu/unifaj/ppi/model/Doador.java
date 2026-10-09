package edu.unifaj.ppi.model;

import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Doador e a unificacao de User e Doador do app. {@code cpf} e a chave logica do
 * dominio — e por ele que a API identifica o doador enquanto nao houver token —
 * e por isso e imutavel depois do cadastro.
 *
 * <p>{@code senha} guarda o hash BCrypt, nunca a senha digitada.
 */
@Entity
@Table(name = "doador")
public class Doador extends Pessoa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "senha", nullable = false)
	private String senha;

	@Column(name = "cpf", nullable = false)
	private String cpf;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_sanguineo", nullable = false)
	private TipoSanguineo tipoSanguineo;

	@Enumerated(EnumType.STRING)
	@Column(name = "fator_rh", nullable = false)
	private FatorRh fatorRh;

	protected Doador() {
	}

	public Doador(String nome, String email, String senha, String cpf, TipoSanguineo tipoSanguineo,
			FatorRh fatorRh) {
		super(nome, email);
		this.senha = senha;
		this.cpf = cpf;
		this.tipoSanguineo = tipoSanguineo;
		this.fatorRh = fatorRh;
	}

	/**
	 * @return "O+", ou null se o doador estiver sem tipo completo cadastrado
	 */
	public String getTipoCompleto() {
		if (tipoSanguineo == null || fatorRh == null) {
			return null;
		}
		return tipoSanguineo.getValor() + fatorRh.getValor();
	}

	public Long getId() {
		return id;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCpf() {
		return cpf;
	}

	public TipoSanguineo getTipoSanguineo() {
		return tipoSanguineo;
	}

	public FatorRh getFatorRh() {
		return fatorRh;
	}

}