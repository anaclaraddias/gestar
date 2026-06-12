package br.unibh.gestar.alerta;

import br.unibh.gestar.domain.MedicalCare;

/**
 * Observador que exibe o alerta clinico no painel medico.
 * Nesta versao, escreve a notificacao no console.
 */
public class PainelMedico implements ObservadorAlerta {

    @Override
    public void notificar(MedicalCare atendimento) {
        System.out.println("[ALERTA CLINICO] Caso critico na fila: "
                + atendimento.getPaciente().getName()
                + " | Queixa: " + atendimento.getMainComplaint()
                + " | Nivel: " + atendimento.getUrgencyLevel());
    }
}
