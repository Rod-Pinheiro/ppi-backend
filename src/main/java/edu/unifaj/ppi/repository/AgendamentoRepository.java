package edu.unifaj.ppi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.unifaj.ppi.model.Agendamento;
import edu.unifaj.ppi.model.enums.StatusAgendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

	List<Agendamento> findByDoadorCpfOrderByDataAscHoraAsc(String cpf);

	List<Agendamento> findByDoadorCpfAndStatusOrderByDataAscHoraAsc(String cpf, StatusAgendamento status);

	/**
	 * Base da consulta de elegibilidade para registro de coleta: agendamento que
	 * ainda nao virou coleta e cuja data ja passou.
	 *
	 * <p>Os agendamentos com bolsa sao removidos em memoria pelo servico, porque
	 * o filtro depende da tabela de bolsa e nao ha como expressar "sem bolsa"
	 * direto no JPQL sem um subselect correlacionado.
	 */
	List<Agendamento> findByDoadorCpfAndStatusNotInAndDataLessThanEqualOrderByDataAscHoraAsc(String cpf,
			List<StatusAgendamento> status, LocalDate hoje);

}