package br.unibh.gestar.dominio;

/**
 * Categoria de prioridade definida na retirada da senha.
 * Usada como desempate dentro da mesma cor de urgencia.
 * Quanto maior o peso, maior a prioridade.
 */
public enum CategoriaPrioridade {

    PRIORIDADE_MAXIMA(3),
    PREFERENCIAL(2),
    NORMAL(1);

    private final int peso;

    CategoriaPrioridade(int peso) {
        this.peso = peso;
    }

    public int getPeso() {
        return peso;
    }

    /**
     * Categoria sugerida apenas pela idade (prioridade ao idoso).
     * Idoso 80+ tem prioridade maxima; idoso 60+ e preferencial.
     * Nao considera PCD, que e definida na retirada da senha.
     */
    public static CategoriaPrioridade paraIdade(int idade) {
        if (idade >= 80) {
            return PRIORIDADE_MAXIMA;
        }
        if (idade >= 60) {
            return PREFERENCIAL;
        }
        return NORMAL;
    }
}
