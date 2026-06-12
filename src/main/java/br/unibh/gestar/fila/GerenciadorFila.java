package br.unibh.gestar.fila;

import java.util.Comparator;
import java.util.PriorityQueue;

import br.unibh.gestar.domain.MedicalCare;

/**
 * Fila priorizada de atendimentos. A ordem segue tres criterios, nesta sequencia:
 *  1) cor de urgencia, do mais urgente para o menos (RN01: Vermelho primeiro);
 *  2) categoria de prioridade da senha (RN03: idoso 80+ antes de PCD/idoso 60+,
 *     que vem antes do normal);
 *  3) ordem de chegada, mais antigo primeiro.
 * E o coracao do sistema e o principal alvo dos testes.
 */
public class GerenciadorFila {

    private static final Comparator<MedicalCare> ORDEM_PRIORIDADE =
            Comparator
                    .comparingInt((MedicalCare a) -> a.getUrgencyLevel().getPriority())
                    .thenComparing(Comparator.comparingInt(
                            (MedicalCare a) -> a.getPriorityCategory().getWeight()).reversed())
                    .thenComparing(MedicalCare::getArrivalDateTime);

    private final PriorityQueue<MedicalCare> fila = new PriorityQueue<>(ORDEM_PRIORIDADE);

    /**
     * Adiciona um atendimento ja classificado a fila.
     */
    public void adicionar(MedicalCare atendimento) {
        if (atendimento.getUrgencyLevel() == null) {
            throw new IllegalStateException("Atendimento sem classificacao nao pode entrar na fila.");
        }
        fila.add(atendimento);
    }

    /**
     * Remove e retorna o proximo atendimento (o mais prioritario), ou null se vazia.
     */
    public MedicalCare proximo() {
        return fila.poll();
    }

    /**
     * Retorna o proximo sem remover, ou null se a fila estiver vazia.
     */
    public MedicalCare espiar() {
        return fila.peek();
    }

    public int tamanho() {
        return fila.size();
    }

    public boolean estaVazia() {
        return fila.isEmpty();
    }
}
