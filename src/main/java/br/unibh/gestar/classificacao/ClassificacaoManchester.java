package br.unibh.gestar.classificacao;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.UrgencyLevel;
import br.unibh.gestar.domain.VitalSigns;

/**
 * Classificacao baseada no Protocolo de Manchester (versao simplificada).
 * Combina dois fatores e usa o mais urgente entre eles:
 *  1) discriminadores por sinais vitais;
 *  2) piso de urgencia por queixa (RN08): certas queixas impoem urgencia
 *     minima independentemente dos sinais. Ex.: dor toracica e no minimo urgente.
 */
public class ClassificacaoManchester implements EstrategiaClassificacao {

    @Override
    public UrgencyLevel classificar(MedicalCare atendimento) {
        VitalSigns sv = atendimento.getVitalSigns();
        if (sv == null) {
            throw new IllegalStateException("Sinais vitais nao informados para a classificacao.");
        }
        UrgencyLevel porSinais = classificarPorSinais(sv);
        UrgencyLevel pisoQueixa = pisoPorQueixa(atendimento.getMainComplaint());
        return maisUrgente(porSinais, pisoQueixa);
    }

    private UrgencyLevel classificarPorSinais(VitalSigns sv) {
        if (sv.getOxygenSaturation() < 85
                || sv.getHeartRate() > 150 || sv.getHeartRate() < 40
                || sv.getRespiratoryRate() > 35 || sv.getRespiratoryRate() < 8
                || sv.getSystolicPressure() > 220 || sv.getSystolicPressure() < 70
                || sv.getTemperature() > 41.0) {
            return UrgencyLevel.RED;
        }
        if (sv.getOxygenSaturation() < 91
                || sv.getHeartRate() > 120
                || sv.getRespiratoryRate() > 24
                || sv.getSystolicPressure() > 200
                || sv.getTemperature() >= 39.5
                || sv.getPainScale() >= 8) {
            return UrgencyLevel.ORANGE;
        }
        if (sv.getOxygenSaturation() < 95
                || sv.getHeartRate() > 100
                || sv.getTemperature() >= 38.0
                || sv.getPainScale() >= 4) {
            return UrgencyLevel.YELLOW;
        }
        return UrgencyLevel.GREEN;
    }

    /**
     * RN08: piso de urgencia por queixa. Dor toracica e sempre, no minimo, urgente.
     */
    private UrgencyLevel pisoPorQueixa(String queixa) {
        if (queixa == null) {
            return UrgencyLevel.GREEN;
        }
        String q = queixa.toLowerCase();
        if (q.contains("torac") || q.contains("torác") || q.contains("peito")) {
            return UrgencyLevel.YELLOW;
        }
        return UrgencyLevel.GREEN;
    }

    /**
     * Retorna o nivel mais urgente entre dois (menor prioridade = mais urgente).
     */
    private UrgencyLevel maisUrgente(UrgencyLevel a, UrgencyLevel b) {
        return a.getPriority() <= b.getPriority() ? a : b;
    }
}
