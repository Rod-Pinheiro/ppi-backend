package edu.unifaj.ppi.dto.response;

import edu.unifaj.ppi.model.Endereco;

public record EnderecoResponse(
		String logradouro,
		String numero,
		String complemento,
		String bairro,
		String cidade,
		String estado,
		String cep,
		String completo) {

	public static EnderecoResponse from(Endereco endereco) {
		return new EnderecoResponse(endereco.getLogradouro(), endereco.getNumero(), endereco.getComplemento(),
				endereco.getBairro(), endereco.getCidade(), endereco.getEstado(), endereco.getCep(),
				endereco.getEnderecoCompleto());
	}

}