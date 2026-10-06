package edu.unifaj.ppi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.unifaj.ppi.model.BolsaSangue;

public interface BolsaSangueRepository extends JpaRepository<BolsaSangue, Long> {

	// OrNull porque existe no maximo uma bolsa por agendamento: o UNIQUE em
	// agendamento_id garante isso, entao um find ja basta.
	Optional<BolsaSangue> findByAgendamentoId(Long agendamentoId);

	boolean existsByAgendamentoId(Long agendamentoId);

	// Filtra pelo agendamento, e nao pelo doador guardado na bolsa: a bolsa nao
	// tem CPF proprio, ela pertence ao agendamento que a originou.
	List<BolsaSangue> findByAgendamentoDoadorCpfOrderByDataColetaDesc(String cpf);

	long countByAgendamentoDoadorCpf(String cpf);

}