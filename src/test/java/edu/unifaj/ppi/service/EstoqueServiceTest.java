package edu.unifaj.ppi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.unifaj.ppi.exception.RecursoNaoEncontradoException;
import edu.unifaj.ppi.model.Endereco;
import edu.unifaj.ppi.model.EstoqueSangue;
import edu.unifaj.ppi.model.Hemocentro;
import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;
import edu.unifaj.ppi.repository.EstoqueSangueRepository;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

	@Mock
	private EstoqueSangueRepository estoqueRepository;

	private EstoqueService estoqueService;
	private Hemocentro hemocentro;

	@BeforeEach
	void setUp() {
		estoqueService = new EstoqueService(estoqueRepository);
		hemocentro = new Hemocentro("HEMO-001", "Central", "(11) 1", "h@h.com", LocalTime.of(7, 0),
				LocalTime.of(18, 0), false, 0,
				new Endereco("Av. x", "1", "Centro", "São Paulo", "SP", "00000-000"));
	}

	@Test
	void entradaCriaOEstoqueNaPrimeiraColeta() {
		when(estoqueRepository.findByHemocentroIdAndTipoSanguineoAndFatorRh("HEMO-001", TipoSanguineo.O,
				FatorRh.POSITIVO)).thenReturn(Optional.empty());
		when(estoqueRepository.save(any(EstoqueSangue.class))).thenAnswer(i -> i.getArgument(0));

		estoqueService.registrarEntrada(hemocentro, TipoSanguineo.O, FatorRh.POSITIVO, 3);

		ArgumentCaptor<EstoqueSangue> captor = ArgumentCaptor.forClass(EstoqueSangue.class);
		verify(estoqueRepository).save(captor.capture());
		assertThat(captor.getValue().getQuantidadeDisponivel()).isEqualTo(3);
		assertThat(captor.getValue().getHemocentro()).isSameAs(hemocentro);
	}

	@Test
	void entradaSomaAoEstoqueExistente() {
		EstoqueSangue existente = new EstoqueSangue(hemocentro, TipoSanguineo.O, FatorRh.POSITIVO, 0);
		existente.adicionar(2);
		when(estoqueRepository.findByHemocentroIdAndTipoSanguineoAndFatorRh("HEMO-001", TipoSanguineo.O,
				FatorRh.POSITIVO)).thenReturn(Optional.of(existente));
		when(estoqueRepository.save(any(EstoqueSangue.class))).thenAnswer(i -> i.getArgument(0));

		estoqueService.registrarEntrada(hemocentro, TipoSanguineo.O, FatorRh.POSITIVO, 4);

		assertThat(existente.getQuantidadeDisponivel()).isEqualTo(6);
	}

	@Test
	void ajustarMinimaDeEstoqueInexistenteFalha() {
		when(estoqueRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> estoqueService.ajustarMinima(99L, 5))
				.isInstanceOf(RecursoNaoEncontradoException.class);
		verify(estoqueRepository, never()).save(any());
	}

}
