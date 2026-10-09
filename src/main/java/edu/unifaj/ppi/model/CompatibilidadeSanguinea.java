package edu.unifaj.ppi.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import edu.unifaj.ppi.model.enums.TipoSanguineo;

/**
 * Regra de compatibilidade entre tipos sanguineos. E uma classe pura, sem
 * framework, pelo mesmo motivo de {@link NivelDoador}: precisa rodar sem
 * Spring e sem banco, tanto nos testes quanto na tela de estoque.
 *
 * <p>O<br>
 * A pode doar para A e AB; B para B e AB; AB so para AB; O e doador universal.
 */
public final class CompatibilidadeSanguinea {

	private static final Map<TipoSanguineo, Set<TipoSanguineo>> RECEPTORES_POR_DOADOR = Map.of(
			TipoSanguineo.O, EnumSet.of(TipoSanguineo.A, TipoSanguineo.B, TipoSanguineo.AB, TipoSanguineo.O),
			TipoSanguineo.A, EnumSet.of(TipoSanguineo.A, TipoSanguineo.AB),
			TipoSanguineo.B, EnumSet.of(TipoSanguineo.B, TipoSanguineo.AB),
			TipoSanguineo.AB, EnumSet.of(TipoSanguineo.AB));

	private CompatibilidadeSanguinea() {
	}

	public static boolean podeDoarPara(TipoSanguineo doador, TipoSanguineo receptor) {
		if (doador == null || receptor == null) {
			return false;
		}
		return RECEPTORES_POR_DOADOR.get(doador).contains(receptor);
	}

	public static boolean podeReceberDe(TipoSanguineo receptor, TipoSanguineo doador) {
		return podeDoarPara(doador, receptor);
	}

	public static boolean isDoadorUniversal(TipoSanguineo tipo) {
		return tipo == TipoSanguineo.O;
	}

	public static boolean isReceptorUniversal(TipoSanguineo tipo) {
		return tipo == TipoSanguineo.AB;
	}

}
