package it.ddalpra.acme.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import it.ddalpra.acme.domain.esternalservice.ExternalService;

public interface ExternalServiceRepositoryPort {
    List<ExternalService> findAll();
    Optional<ExternalService> findByName(String name);
    Optional<ExternalService> findById(Long id);
    void save(ExternalService service);
    void delete(Long id);
}