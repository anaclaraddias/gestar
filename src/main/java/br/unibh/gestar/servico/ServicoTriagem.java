package br.unibh.gestar.servico;

import br.unibh.gestar.alerta.NotificadorClinico;
import br.unibh.gestar.classificacao.EstrategiaClassificacao;
import br.unibh.gestar.dominio.Atendimento;
import br.unibh.gestar.dominio.CategoriaPrioridade;
import br.unibh.gestar.dominio.NivelUrgencia;
import br.unibh.gestar.dominio.Paciente;
import br.unibh.gestar.dominio.SinaisVitais;
import br.unibh.gestar.dominio.StatusAtendimento;
import br.unibh.gestar.fila.GerenciadorFila;
import br.unibh.gestar.repositorio.AtendimentoRepository;

/**
 * Orquestra o fluxo de triagem: cria o atendimento, classifica o risco,
 * persiste, enfileira e dispara alertas. Depende apenas de abstracoes
 * (a estrategia de classificacao e o repositorio), o que materializa o DIP.
 */
public class ServicoTriagem {

    private final EstrategiaClassificacao estrategia;
    private final AtendimentoRepository repositorio;
    private final GerenciadorFila fila;
    private final NotificadorClinico notificador;

    public ServicoTriagem(EstrategiaClassificacao estrategia,
                          AtendimentoRepository repositorio,
                          GerenciadorFila fila,
                          NotificadorClinico notificador) {
        this.estrategia = estrategia;
        this.repositorio = repositorio;
        this.fila = fila;
        this.notificador = notificador;
    }

    /**
     * Triagem de um paciente elegivel: classifica, persiste, enfileira e,
     * se for caso critico (Vermelho), dispara o alerta clinico (RN07).
     */
    public Atendimento realizarTriagem(Paciente paciente, SinaisVitais sinais,
                                       String queixa, CategoriaPrioridade categoria) {
        Atendimento atendimento = new Atendimento(paciente, queixa, categoria);
        atendimento.avancarStatus(StatusAtendimento.EM_TRIAGEM);
        atendimento.setSinaisVitais(sinais);

        NivelUrgencia nivel = estrategia.classificar(atendimento);
        atendimento.definirClassificacao(nivel);

        repositorio.salvar(atendimento);

        atendimento.avancarStatus(StatusAtendimento.NA_FILA);
        fila.adicionar(atendimento);

        if (atendimento.ehCritico()) {
            notificador.dispararAlerta(atendimento);
        }
        return atendimento;
    }

    /**
     * Registra um paciente nao atendido pela unidade: guarda os dados, os sinais
     * e o encaminhamento (RN06), sem enfileirar.
     */
    public Atendimento encaminhar(Paciente paciente, SinaisVitais sinais, String queixa,
                                  CategoriaPrioridade categoria, String motivo, String unidadeDestino) {
        Atendimento atendimento = new Atendimento(paciente, queixa, categoria);
        atendimento.setSinaisVitais(sinais);
        atendimento.marcarEncaminhado(motivo, unidadeDestino);
        repositorio.salvar(atendimento);
        return atendimento;
    }

    /**
     * Reavalia um atendimento com novos sinais. Aplica a regra monotonica
     * (RN05): a urgencia so aumenta, nunca diminui. Se passar a critico,
     * dispara o alerta. A reordenacao na fila apos a mudanca de nivel fica
     * como evolucao futura; aqui o nivel e atualizado e o alerta disparado.
     */
    public Atendimento reclassificar(Atendimento atendimento, SinaisVitais novosSinais) {
        atendimento.setSinaisVitais(novosSinais);
        NivelUrgencia novoNivel = estrategia.classificar(atendimento);
        atendimento.reclassificar(novoNivel);
        repositorio.salvar(atendimento);
        if (atendimento.ehCritico()) {
            notificador.dispararAlerta(atendimento);
        }
        return atendimento;
    }

    /**
     * Chama o proximo paciente da fila para atendimento, ou null se a fila
     * estiver vazia.
     */
    public Atendimento chamarProximo() {
        Atendimento atendimento = fila.proximo();
        if (atendimento != null) {
            atendimento.avancarStatus(StatusAtendimento.EM_ATENDIMENTO);
            repositorio.salvar(atendimento);
        }
        return atendimento;
    }

    /**
     * Finaliza um atendimento.
     */
    public void finalizar(Atendimento atendimento) {
        atendimento.avancarStatus(StatusAtendimento.FINALIZADO);
        repositorio.salvar(atendimento);
    }
}
