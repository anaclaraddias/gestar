package br.unibh.gestar.dominio;

/**
 * Valores aferidos na triagem, usados pela classificacao de risco.
 * Imutavel: representa a afericao de um momento.
 */
public class SinaisVitais {

    private final int pressaoSistolica;
    private final int pressaoDiastolica;
    private final int frequenciaCardiaca;
    private final int frequenciaRespiratoria;
    private final double temperatura;
    private final int saturacao;
    private final int escalaDor;

    public SinaisVitais(int pressaoSistolica, int pressaoDiastolica,
                        int frequenciaCardiaca, int frequenciaRespiratoria,
                        double temperatura, int saturacao, int escalaDor) {
        this.pressaoSistolica = pressaoSistolica;
        this.pressaoDiastolica = pressaoDiastolica;
        this.frequenciaCardiaca = frequenciaCardiaca;
        this.frequenciaRespiratoria = frequenciaRespiratoria;
        this.temperatura = temperatura;
        this.saturacao = saturacao;
        this.escalaDor = escalaDor;
    }

    public int getPressaoSistolica() {
        return pressaoSistolica;
    }

    public int getPressaoDiastolica() {
        return pressaoDiastolica;
    }

    public int getFrequenciaCardiaca() {
        return frequenciaCardiaca;
    }

    public int getFrequenciaRespiratoria() {
        return frequenciaRespiratoria;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public int getSaturacao() {
        return saturacao;
    }

    public int getEscalaDor() {
        return escalaDor;
    }
}
