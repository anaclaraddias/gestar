package br.unibh.gestar.classificacao;

import br.unibh.gestar.dominio.Atendimento;
import br.unibh.gestar.dominio.NivelUrgencia;
import br.unibh.gestar.dominio.SinaisVitais;

/**
 * Classificacao baseada no Protocolo de Manchester (versao simplificada).
 * Combina dois fatores e usa o mais urgente entre eles:
 *  1) discriminadores por sinais vitais;
 *  2) piso de urgencia por queixa (RN08): certas queixas impoem urgencia
 *     minima independentemente dos sinais. Ex.: dor toracica e no minimo urgente.
 */
public class ClassificacaoManchester implements EstrategiaClassificacao {

    @Override
    public NivelUrgencia classificar(Atendimento atendimento) {
        SinaisVitais sv = atendimento.getSinaisVitais();
        if (sv == null) {
            throw new IllegalStateException("Sinais vitais nao informados para a classificacao.");
        }
        NivelUrgencia porSinais = classificarPorSinais(sv);
        NivelUrgencia pisoQueixa = pisoPorQueixa(atendimento.getQueixaPrincipal());
        return maisUrgente(porSinais, pisoQueixa);
    }

    private NivelUrgencia classificarPorSinais(SinaisVitais sv) {
        if (sv.getSaturacao() < 85
                || sv.getFrequenciaCardiaca() > 150 || sv.getFrequenciaCardiaca() < 40
                || sv.getFrequenciaRespiratoria() > 35 || sv.getFrequenciaRespiratoria() < 8
                || sv.getPressaoSistolica() > 220 || sv.getPressaoSistolica() < 70
                || sv.getTemperatura() > 41.0) {
            return NivelUrgencia.VERMELHO;
        }
        if (sv.getSaturacao() < 91
                || sv.getFrequenciaCardiaca() > 120
                || sv.getFrequenciaRespiratoria() > 24
                || sv.getPressaoSistolica() > 200
                || sv.getTemperatura() >= 39.5
                || sv.getEscalaDor() >= 8) {
            return NivelUrgencia.LARANJA;
        }
        if (sv.getSaturacao() < 95
                || sv.getFrequenciaCardiaca() > 100
                || sv.getTemperatura() >= 38.0
                || sv.getEscalaDor() >= 4) {
            return NivelUrgencia.AMARELO;
        }
        return NivelUrgencia.VERDE;
    }

    /**
     * RN08: piso de urgencia por queixa. Dor toracica e sempre, no minimo, urgente.
     */
    private NivelUrgencia pisoPorQueixa(String queixa) {
        if (queixa == null) {
            return NivelUrgencia.VERDE;
        }
        String q = queixa.toLowerCase();
        if (q.contains("torac") || q.contains("torác") || q.contains("peito")) {
            return NivelUrgencia.AMARELO;
        }
        return NivelUrgencia.VERDE;
    }

    /**
     * Retorna o nivel mais urgente entre dois (menor prioridade = mais urgente).
     */
    private NivelUrgencia maisUrgente(NivelUrgencia a, NivelUrgencia b) {
        return a.getPrioridade() <= b.getPrioridade() ? a : b;
    }
}
