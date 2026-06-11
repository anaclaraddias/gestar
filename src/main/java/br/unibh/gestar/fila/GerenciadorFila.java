package br.unibh.gestar.fila;

import br.unibh.gestar.dominio.Atendimento;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Fila priorizada de atendimentos. A ordem segue tres criterios, nesta sequencia:
 *  1) cor de urgencia, do mais urgente para o menos (RN01: Vermelho primeiro);
 *  2) categoria de prioridade da senha (RN03: idoso 80+ antes de PCD/idoso 60+,
 *     que vem antes do normal);
 *  3) ordem de chegada, mais antigo primeiro.
 * E o coracao do sistema e o principal alvo dos testes.
 */
public class GerenciadorFila {

    private static final Comparator<Atendimento> ORDEM_PRIORIDADE =
            Comparator
                    .comparingInt((Atendimento a) -> a.getNivel().getPrioridade())
                    .thenComparing(Comparator.comparingInt(
                            (Atendimento a) -> a.getCategoriaPrioridade().getPeso()).reversed())
                    .thenComparing(Atendimento::getDataHoraChegada);

    private final PriorityQueue<Atendimento> fila = new PriorityQueue<>(ORDEM_PRIORIDADE);

    /**
     * Adiciona um atendimento ja classificado a fila.
     */
    public void adicionar(Atendimento atendimento) {
        if (atendimento.getNivel() == null) {
            throw new IllegalStateException("Atendimento sem classificacao nao pode entrar na fila.");
        }
        fila.add(atendimento);
    }

    /**
     * Remove e retorna o proximo atendimento (o mais prioritario), ou null se vazia.
     */
    public Atendimento proximo() {
        return fila.poll();
    }

    /**
     * Retorna o proximo sem remover, ou null se a fila estiver vazia.
     */
    public Atendimento espiar() {
        return fila.peek();
    }

    public int tamanho() {
        return fila.size();
    }

    public boolean estaVazia() {
        return fila.isEmpty();
    }
}
