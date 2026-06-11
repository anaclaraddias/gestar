package br.unibh.gestar.alerta;

import br.unibh.gestar.dominio.Atendimento;

import java.util.ArrayList;
import java.util.List;

/**
 * Sujeito do padrao Observer: mantem os observadores registrados e dispara
 * o alerta clinico para todos quando ocorre um caso critico (RN07).
 */
public class NotificadorClinico {

    private final List<ObservadorAlerta> observadores = new ArrayList<>();

    public void registrar(ObservadorAlerta observador) {
        observadores.add(observador);
    }

    public void dispararAlerta(Atendimento atendimento) {
        for (ObservadorAlerta observador : observadores) {
            observador.notificar(atendimento);
        }
    }
}
