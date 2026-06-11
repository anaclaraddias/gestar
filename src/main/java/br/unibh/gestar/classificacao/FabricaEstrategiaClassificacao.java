package br.unibh.gestar.classificacao;

/**
 * Fabrica de estrategias de classificacao (padrao Factory Method).
 * Cria a estrategia adequada ao protocolo, sem acoplar o servico de triagem
 * as classes concretas.
 */
public class FabricaEstrategiaClassificacao {

    public static EstrategiaClassificacao criar(TipoProtocolo tipo) {
        return switch (tipo) {
            case MANCHESTER -> new ClassificacaoManchester();
            case SIMPLES -> new ClassificacaoSimples();
        };
    }
}
