package br.unibh.gestar.dominio;

/**
 * Estados do ciclo de vida de um atendimento.
 */
public enum StatusAtendimento {

    AGUARDANDO_TRIAGEM,
    EM_TRIAGEM,
    NA_FILA,
    EM_ATENDIMENTO,
    FINALIZADO,
    ENCAMINHADO
}
