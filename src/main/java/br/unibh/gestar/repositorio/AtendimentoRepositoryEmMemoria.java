package br.unibh.gestar.repositorio;

import br.unibh.gestar.dominio.Atendimento;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementacao em memoria do repositorio de atendimentos.
 * Guarda os atendimentos em um Map durante a execucao, sem banco de dados,
 * o que mantem o escopo enxuto. A ordem de insercao e preservada.
 */
public class AtendimentoRepositoryEmMemoria implements AtendimentoRepository {

    private final Map<String, Atendimento> dados = new LinkedHashMap<>();

    @Override
    public void salvar(Atendimento atendimento) {
        dados.put(atendimento.getId(), atendimento);
    }

    @Override
    public Optional<Atendimento> buscarPorId(String id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Atendimento> listarTodos() {
        return new ArrayList<>(dados.values());
    }
}
