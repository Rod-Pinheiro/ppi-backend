package edu.unifaj.ppi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.unifaj.ppi.model.EstoqueSangue;
import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;

public interface EstoqueSangueRepository extends JpaRepository<EstoqueSangue, Long> {

	Optional<EstoqueSangue> findByHemocentroIdAndTipoSanguineoAndFatorRh(String hemocentroId,
			TipoSanguineo tipoSanguineo, FatorRh fatorRh);

	List<EstoqueSangue> findByHemocentroIdOrderByTipoSanguineoAscFatorRhAsc(String hemocentroId);

	List<EstoqueSangue> findAllByOrderByHemocentroIdAscTipoSanguineoAscFatorRhAsc();

}
