package br.unibh.gestar.alerta;

import br.unibh.gestar.dominio.Atendimento;

/**
 * Observador de alertas clinicos (padrao Observer).
 * As implementacoes reagem quando um caso critico e sinalizado.
 */
public interface ObservadorAlerta {

    void notificar(Atendimento atendimento);
}
