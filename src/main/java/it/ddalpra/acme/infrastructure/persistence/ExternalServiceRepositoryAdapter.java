package it.ddalpra.acme.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import it.ddalpra.acme.domain.esternalservice.ExternalService;
import it.ddalpra.acme.interfaces.persistence.ExternalServiceRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class ExternalServiceRepositoryAdapter implements ExternalServiceRepositoryPort {

    @Inject
    EntityManager em;

    @Override
    public List<ExternalService> findAll() {
        return em.createQuery("SELECT s FROM ExternalService s", ExternalService.class).getResultList();
    }

    @Override
    public Optional<ExternalService> findByName(String name) {
        return em.createQuery("SELECT s FROM ExternalService s WHERE s.name = :name", ExternalService.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<ExternalService> findById(Long id) {
        return Optional.ofNullable(em.find(ExternalService.class, id));
    }

    @Override
    public void save(ExternalService service) {
        em.persist(service);
    }

    @Override
    public void delete(Long id) {
        Optional<ExternalService> service = findById(id);
        service.ifPresent(em::remove);
    }
}