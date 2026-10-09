package edu.unifaj.ppi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.unifaj.ppi.model.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

	List<Notificacao> findByDoadorCpfOrderByCriadaEmDesc(String cpf);

	Optional<Notificacao> findByIdAndDoadorCpf(Long id, String cpf);

	long countByDoadorCpfAndLidaFalse(String cpf);

}
