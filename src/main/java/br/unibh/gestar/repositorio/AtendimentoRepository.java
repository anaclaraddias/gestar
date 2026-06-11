package br.unibh.gestar.repositorio;

import br.unibh.gestar.dominio.Atendimento;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistencia de atendimentos (padrao Repository).
 * Abstrai o armazenamento, permitindo trocar a implementacao (memoria, banco)
 * sem afetar a regra de negocio. E o ponto-chave do DIP no projeto: o servico
 * de triagem dependera desta interface, nunca de uma implementacao concreta.
 */
public interface AtendimentoRepository {

    void salvar(Atendimento atendimento);

    Optional<Atendimento> buscarPorId(String id);

    List<Atendimento> listarTodos();
}
