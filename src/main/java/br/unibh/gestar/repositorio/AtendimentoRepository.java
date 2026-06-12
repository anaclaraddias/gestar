package br.unibh.gestar.repositorio;

import java.util.List;
import java.util.Optional;

import br.unibh.gestar.domain.MedicalCare;

/**
 * Contrato de persistencia de atendimentos (padrao Repository).
 * Abstrai o armazenamento, permitindo trocar a implementacao (memoria, banco)
 * sem afetar a regra de negocio. E o ponto-chave do DIP no projeto: o servico
 * de triagem dependera desta interface, nunca de uma implementacao concreta.
 */
public interface AtendimentoRepository {

    void salvar(MedicalCare atendimento);

    Optional<MedicalCare> buscarPorId(String id);

    List<MedicalCare> listarTodos();
}
