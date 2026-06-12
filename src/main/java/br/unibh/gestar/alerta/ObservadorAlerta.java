package br.unibh.gestar.alerta;

import br.unibh.gestar.domain.MedicalCare;

/**
 * Observador de alertas clinicos (padrao Observer).
 * As implementacoes reagem quando um caso critico e sinalizado.
 */
public interface ObservadorAlerta {

    void notificar(MedicalCare atendimento);
}
