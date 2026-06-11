package br.unibh.gestar.classificacao;

import br.unibh.gestar.dominio.Atendimento;
import br.unibh.gestar.dominio.NivelUrgencia;
import br.unibh.gestar.dominio.SinaisVitais;

/**
 * Classificacao simplificada, baseada apenas em saturacao e escala de dor.
 * Serve como alternativa ao Manchester e demonstra que a estrategia pode ser
 * trocada sem qualquer impacto na fila ou no servico (OCP e LSP).
 */
public class ClassificacaoSimples implements EstrategiaClassificacao {

    @Override
    public NivelUrgencia classificar(Atendimento atendimento) {
        SinaisVitais sv = atendimento.getSinaisVitais();
        if (sv == null) {
            throw new IllegalStateException("Sinais vitais nao informados para a classificacao.");
        }
        if (sv.getSaturacao() < 90 || sv.getEscalaDor() >= 8) {
            return NivelUrgencia.LARANJA;
        }
        if (sv.getEscalaDor() >= 5) {
            return NivelUrgencia.AMARELO;
        }
        return NivelUrgencia.VERDE;
    }
}
