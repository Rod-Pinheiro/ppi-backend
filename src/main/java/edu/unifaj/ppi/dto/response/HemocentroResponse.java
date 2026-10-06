package edu.unifaj.ppi.dto.response;

import java.time.LocalTime;

import edu.unifaj.ppi.model.Hemocentro;

public record HemocentroResponse(
		String id,
		String nome,
		String telefone,
		String email,
		LocalTime horarioAbertura,
		LocalTime horarioFechamento,
		boolean funcionamento24Horas,
		String horarioFuncionamento,
		EnderecoResponse endereco) {

	public static HemocentroResponse from(Hemocentro hemocentro) {
		return new HemocentroResponse(hemocentro.getId(), hemocentro.getNome(), hemocentro.getTelefone(),
				hemocentro.getEmail(), hemocentro.getHorarioAbertura(), hemocentro.getHorarioFechamento(),
				hemocentro.isFuncionamento24Horas(), hemocentro.getHorarioFuncionamento(),
				EnderecoResponse.from(hemocentro.getEndereco()));
	}

}