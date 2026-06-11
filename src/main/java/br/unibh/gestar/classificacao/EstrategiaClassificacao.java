package br.unibh.gestar.classificacao;

import br.unibh.gestar.dominio.Atendimento;
import br.unibh.gestar.dominio.NivelUrgencia;

/**
 * Contrato de classificacao de risco (padrao Strategy).
 * Permite trocar o protocolo de classificacao sem alterar a fila nem o servico.
 */
public interface EstrategiaClassificacao {

    NivelUrgencia classificar(Atendimento atendimento);
}
