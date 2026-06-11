package br.unibh.gestar.dominio;

/**
 * Niveis de urgencia do Protocolo de Manchester usados na unidade.
 * A unidade nao utiliza a cor Azul.
 * Quanto menor a prioridade, mais urgente e o caso (Vermelho = 1).
 */
public enum NivelUrgencia {

    VERMELHO("Emergencia", 1, 0),
    LARANJA("Muito urgente", 2, 10),
    AMARELO("Urgente", 3, 60),
    VERDE("Pouco urgente", 4, 120);

    private final String descricao;
    private final int prioridade;
    private final int tempoAlvoMinutos;

    NivelUrgencia(String descricao, int prioridade, int tempoAlvoMinutos) {
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.tempoAlvoMinutos = tempoAlvoMinutos;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public int getTempoAlvoMinutos() {
        return tempoAlvoMinutos;
    }
}
