package edu.unifaj.ppi.dto.response;

import java.util.Map;

/**
 * Mapa de agendamento para quantidade de bolsas. No app era um
 * {@code Map<String, Integer>} montado a partir da lista de bolsas.
 */
public record QuantidadesResponse(Map<Long, Integer> porAgendamento) {

}