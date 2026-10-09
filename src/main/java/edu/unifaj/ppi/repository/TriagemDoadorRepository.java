package edu.unifaj.ppi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.unifaj.ppi.model.TriagemDoador;

public interface TriagemDoadorRepository extends JpaRepository<TriagemDoador, Long> {

	List<TriagemDoador> findByDoadorCpfOrderByDataDesc(String cpf);

	Optional<TriagemDoador> findFirstByDoadorCpfOrderByDataDesc(String cpf);

}
