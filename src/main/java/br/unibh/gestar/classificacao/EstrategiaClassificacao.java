package br.unibh.gestar.classificacao;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.UrgencyLevel;

/**
 * Contrato de classificacao de risco (padrao Strategy).
 * Permite trocar o protocolo de classificacao sem alterar a fila nem o servico.
 */
public interface EstrategiaClassificacao {

    UrgencyLevel classificar(MedicalCare atendimento);
}
