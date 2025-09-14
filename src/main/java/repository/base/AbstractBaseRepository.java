package repository.base;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import model.common.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class AbstractBaseRepository<T extends BaseEntity, ID extends Serializable> implements BaseRepository<T, ID> {
    protected final EntityManager em;
    protected final Class<T> entityClassRef;
    private String entityName;

    public AbstractBaseRepository(EntityManager em, Class<T> entityClassRef) {
        this.em = em;
        this.entityClassRef = entityClassRef;
    }

    @Override
    public Class<T> getEntityClassRef() {
        return entityClassRef;
    }

    @Override
    public String getEntityName() {
        if (entityName == null) {
            entityName = em.getMetamodel().entity(entityClassRef).getName();
        }
        return entityName;
    }

    @Override
    public T save(T entity) {
        if (entity.getId() == null) {
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }

    @Override
    public List<T> findAll() {
        return em.createQuery("select e from " + getEntityName() + " e", entityClassRef)
                .getResultList();
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(entityClassRef, id));
    }

    @Override
    public boolean deleteById(ID id) {
        try {
            T ref = em.getReference(entityClassRef, id);
            em.remove(ref);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    @Override
    public long count() {
        return em.createQuery("select count(e) from " + getEntityName() + " e", Long.class)
                .getSingleResult();
    }
}
