package edu.unifaj.ppi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.unifaj.ppi.model.Doador;

public interface DoadorRepository extends JpaRepository<Doador, Long> {

	Optional<Doador> findByEmailIgnoreCase(String email);

	Optional<Doador> findByCpf(String cpf);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByCpf(String cpf);

	// Usado ao editar o perfil: o proprio doador nao pode ser considerado
	// conflito quando mantem o mesmo email.
	boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

}