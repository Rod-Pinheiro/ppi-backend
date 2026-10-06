package edu.unifaj.ppi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.unifaj.ppi.model.Hemocentro;

public interface HemocentroRepository extends JpaRepository<Hemocentro, String> {

	List<Hemocentro> findAllByOrderByNomeAsc();

}