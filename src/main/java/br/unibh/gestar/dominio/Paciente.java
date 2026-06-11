package br.unibh.gestar.dominio;

import java.time.LocalDate;
import java.time.Period;

/**
 * Representa um paciente que busca atendimento.
 */
public class Paciente {

    private final String id;
    private final String nome;
    private final LocalDate dataNascimento;

    public Paciente(String id, String nome, LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
    }

    /**
     * Idade do paciente em anos completos.
     */
    public int getIdade() {
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
}
