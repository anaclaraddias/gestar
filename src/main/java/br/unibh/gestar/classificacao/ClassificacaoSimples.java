package br.unibh.gestar.classificacao;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.UrgencyLevel;
import br.unibh.gestar.domain.VitalSigns;

/**
 * Classificacao simplificada, baseada apenas em saturacao e escala de dor.
 * Serve como alternativa ao Manchester e demonstra que a estrategia pode ser
 * trocada sem qualquer impacto na fila ou no servico (OCP e LSP).
 */
public class ClassificacaoSimples implements EstrategiaClassificacao {

    @Override
    public UrgencyLevel classificar(MedicalCare atendimento) {
        VitalSigns sv = atendimento.getVitalSigns();
        if (sv == null) {
            throw new IllegalStateException("Sinais vitais nao informados para a classificacao.");
        }
        if (sv.getOxygenSaturation() < 90 || sv.getPainScale() >= 8) {
            return UrgencyLevel.ORANGE;
        }
        if (sv.getPainScale() >= 5) {
            return UrgencyLevel.YELLOW;
        }
        return UrgencyLevel.GREEN;
    }
}
