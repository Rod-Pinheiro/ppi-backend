package edu.unifaj.ppi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Endereco de um hemocentro. No app era uma classe embutida que via junto na
 * copia de JSON de cada agendamento e de cada bolsa; aqui e uma tabela.
 */
@Entity
@Table(name = "endereco")
public class Endereco {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "logradouro", nullable = false)
	private String logradouro;

	@Column(name = "numero", nullable = false)
	private String numero;

	@Column(name = "complemento")
	private String complemento;

	@Column(name = "bairro", nullable = false)
	private String bairro;

	@Column(name = "cidade", nullable = false)
	private String cidade;

	@Column(name = "estado", nullable = false)
	private String estado;

	@Column(name = "cep", nullable = false)
	private String cep;

	protected Endereco() {
	}

	public Endereco(String logradouro, String numero, String bairro, String cidade, String estado, String cep) {
		this.logradouro = logradouro;
		this.numero = numero;
		this.bairro = bairro;
		this.cidade = cidade;
		this.estado = estado;
		this.cep = cep;
	}

	/**
	 * Montagem identica a do app, inclusive sem o CEP, que nunca foi exibido.
	 */
	public String getEnderecoCompleto() {
		String end = logradouro + ", " + numero;
		if (complemento != null && !complemento.isEmpty()) {
			end += " - " + complemento;
		}
		return end + ", " + bairro + ", " + cidade + "/" + estado;
	}

	public Long getId() {
		return id;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public String getEstado() {
		return estado;
	}

	public String getCep() {
		return cep;
	}

}