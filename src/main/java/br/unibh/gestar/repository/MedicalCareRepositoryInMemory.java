package br.unibh.gestar.repository;

import br.unibh.gestar.domain.MedicalCare;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the medical care repository.
 * Stores medical care in a Map during execution, without database,
 * which keeps the scope lean. The order of insertion is preserved.
 */
public class MedicalCareRepositoryInMemory implements MedicalCareRepository {

    private final Map<String, MedicalCare> data = new LinkedHashMap<>();

    @Override
    public void save(MedicalCare medicalCare) {
        data.put(medicalCare.getId(), medicalCare);
    }

    @Override
    public Optional<MedicalCare> findById(String id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<MedicalCare> listAll() {
        return new ArrayList<>(data.values());
    }
}
