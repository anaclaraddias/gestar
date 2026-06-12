package br.unibh.gestar.servico;

import br.unibh.gestar.alerta.NotificadorClinico;
import br.unibh.gestar.classificacao.EstrategiaClassificacao;
import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.PriorityCategory;
import br.unibh.gestar.domain.UrgencyLevel;
import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.domain.VitalSigns;
import br.unibh.gestar.domain.MedicalCareStatus;
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
    public MedicalCare realizarTriagem(Patient paciente, VitalSigns sinais,
                                       String queixa, PriorityCategory categoria) {
        MedicalCare atendimento = new MedicalCare(paciente, queixa, categoria);
        atendimento.advanceStatus(MedicalCareStatus.IN_TRIAGE);
        atendimento.setVitalSigns(sinais);

        UrgencyLevel urgencyLevel = estrategia.classificar(atendimento);
        atendimento.setClassification(urgencyLevel);

        repositorio.salvar(atendimento);

        atendimento.advanceStatus(MedicalCareStatus.IN_QUEUE);
        fila.adicionar(atendimento);

        if (atendimento.isCritical()) {
            notificador.dispararAlerta(atendimento);
        }
        return atendimento;
    }

    /**
     * Registra um paciente nao atendido pela unidade: guarda os dados, os sinais
     * e o encaminhamento (RN06), sem enfileirar.
     */
    public MedicalCare encaminhar(Patient paciente, VitalSigns sinais, String queixa,
                                  PriorityCategory categoria, String referralReason, String destinationUnit) {
        MedicalCare atendimento = new MedicalCare(paciente, queixa, categoria);
        atendimento.setVitalSigns(sinais);
        atendimento.markReferred(referralReason, destinationUnit);
        repositorio.salvar(atendimento);
        return atendimento;
    }

    /**
     * Reavalia um atendimento com novos sinais. Aplica a regra monotonica
     * (RN05): a urgencia so aumenta, nunca diminui. Se passar a critico,
     * dispara o alerta. A reordenacao na fila apos a mudanca de nivel fica
     * como evolucao futura; aqui o nivel e atualizado e o alerta disparado.
     */
    public MedicalCare reclassificar(MedicalCare atendimento, VitalSigns novosSinais) {
        atendimento.setVitalSigns(novosSinais);
        UrgencyLevel newUrgencyLevel = estrategia.classificar(atendimento);
        atendimento.reclassify(newUrgencyLevel);
        repositorio.salvar(atendimento);
        if (atendimento.isCritical()) {
            notificador.dispararAlerta(atendimento);
        }
        return atendimento;
    }

    /**
     * Chama o proximo paciente da fila para atendimento, ou null se a fila
     * estiver vazia.
     */
    public MedicalCare chamarProximo() {
        MedicalCare atendimento = fila.proximo();
        if (atendimento != null) {
            atendimento.advanceStatus(MedicalCareStatus.IN_MEDICAL_CARE);
            repositorio.salvar(atendimento);
        }
        return atendimento;
    }

    /**
     * Finaliza um atendimento.
     */
    public void finalizar(MedicalCare atendimento) {
        atendimento.advanceStatus(MedicalCareStatus.FINISHED);
        repositorio.salvar(atendimento);
    }
}
