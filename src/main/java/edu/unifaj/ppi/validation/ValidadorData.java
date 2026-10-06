package edu.unifaj.ppi.validation;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import edu.unifaj.ppi.exception.RegraDeNegocioException;

/**
 * Porta do DateUtils do app. A regra que importa para o negocio e o
 * {@link #isDataNoPassadoOuHoje(LocalDate)}: agendamento com data futura nao
 * pode gerar coleta.
 */
@Component
public class ValidadorData {

	/**
	 * Granularidade de dia, como no app: a hora do agendamento e ignorada. Um
	 * agendamento para hoje as 23h continua elegivel as 10h da manha.
	 */
	public boolean isDataNoPassadoOuHoje(LocalDate data) {
		return data != null && !data.isAfter(LocalDate.now());
	}

	public void exigirDataPassadaOuHoje(LocalDate data) {
		if (!isDataNoPassadoOuHoje(data)) {
			throw new RegraDeNegocioException("Este agendamento não está mais disponível para registro");
		}
	}

}