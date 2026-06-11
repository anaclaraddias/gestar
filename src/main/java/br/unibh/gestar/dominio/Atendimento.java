package br.unibh.gestar.dominio;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa um atendimento no pronto atendimento: o paciente, sua queixa,
 * a classificacao de risco, a categoria de prioridade e o estado atual.
 */
public class Atendimento {

    private final String id;
    private final Paciente paciente;
    private final String queixaPrincipal;
    private final CategoriaPrioridade categoriaPrioridade;
    private final LocalDateTime dataHoraChegada;

    private SinaisVitais sinaisVitais;
    private NivelUrgencia nivel;
    private StatusAtendimento status;

    private String motivoEncaminhamento;
    private String unidadeDestino;

    public Atendimento(Paciente paciente, String queixaPrincipal,
                       CategoriaPrioridade categoriaPrioridade) {
        this.id = "ATD-" + UUID.randomUUID().toString().substring(0, 8);
        this.paciente = paciente;
        this.queixaPrincipal = queixaPrincipal;
        this.categoriaPrioridade = categoriaPrioridade;
        this.dataHoraChegada = LocalDateTime.now();
        this.status = StatusAtendimento.AGUARDANDO_TRIAGEM;
    }

    /**
     * Primeira classificacao de risco do atendimento.
     */
    public void definirClassificacao(NivelUrgencia nivel) {
        this.nivel = nivel;
    }

    /**
     * Reavalia a classificacao. Regra RN05: a urgencia so pode aumentar,
     * nunca diminuir. Se a nova classificacao for menos urgente, mantem a atual.
     */
    public void reclassificar(NivelUrgencia novoNivel) {
        if (this.nivel == null || novoNivel.getPrioridade() < this.nivel.getPrioridade()) {
            this.nivel = novoNivel;
        }
    }

    /**
     * Marca o atendimento como encaminhado a outra unidade, guardando o
     * registro (motivo e unidade de destino) por rastreabilidade.
     */
    public void marcarEncaminhado(String motivo, String unidadeDestino) {
        this.motivoEncaminhamento = motivo;
        this.unidadeDestino = unidadeDestino;
        this.status = StatusAtendimento.ENCAMINHADO;
    }

    public void avancarStatus(StatusAtendimento novoStatus) {
        this.status = novoStatus;
    }

    public boolean ehCritico() {
        return this.nivel == NivelUrgencia.VERMELHO;
    }

    public void setSinaisVitais(SinaisVitais sinaisVitais) {
        this.sinaisVitais = sinaisVitais;
    }

    public String getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public String getQueixaPrincipal() {
        return queixaPrincipal;
    }

    public CategoriaPrioridade getCategoriaPrioridade() {
        return categoriaPrioridade;
    }

    public LocalDateTime getDataHoraChegada() {
        return dataHoraChegada;
    }

    public SinaisVitais getSinaisVitais() {
        return sinaisVitais;
    }

    public NivelUrgencia getNivel() {
        return nivel;
    }

    public StatusAtendimento getStatus() {
        return status;
    }

    public String getMotivoEncaminhamento() {
        return motivoEncaminhamento;
    }

    public String getUnidadeDestino() {
        return unidadeDestino;
    }
}
