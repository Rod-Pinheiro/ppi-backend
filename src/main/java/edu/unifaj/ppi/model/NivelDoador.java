package edu.unifaj.ppi.model;

/**
 * Regra do nivel do doador, sem Spring e sem Android: e a mesma classe pura que
 * o app mantinha para poder testar o teto sem emulador, so que agora o teste
 * roda na suite do backend.
 *
 * <p>Um registro e um lote de bolsa, isto e, um agendamento que ja teve coleta
 * registrada. A quantidade de bolsas da sessao nao entra na conta: seis bolsas
 * no mesmo dia continuam sendo um registro so.
 */
public class NivelDoador {

	public static final int LIMITE = 3;

	private final int registros;

	public NivelDoador(int registros) {
		this.registros = registros;
	}

	public int getRegistros() {
		return registros;
	}

	/**
	 * @return 0, 33, 67 ou 100; nunca passa de 100
	 */
	public int percentual() {
		if (registros <= 0) {
			return 0;
		}
		if (isMaximo()) {
			return 100;
		}
		return Math.round(registros * 100f / LIMITE);
	}

	public boolean isMaximo() {
		return registros >= LIMITE;
	}

}