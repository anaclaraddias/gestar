package br.unibh.gestar.alerta;

import br.unibh.gestar.dominio.Atendimento;

/**
 * Observador que exibe o alerta clinico no painel medico.
 * Nesta versao, escreve a notificacao no console.
 */
public class PainelMedico implements ObservadorAlerta {

    @Override
    public void notificar(Atendimento atendimento) {
        System.out.println("[ALERTA CLINICO] Caso critico na fila: "
                + atendimento.getPaciente().getNome()
                + " | Queixa: " + atendimento.getQueixaPrincipal()
                + " | Nivel: " + atendimento.getNivel());
    }
}
