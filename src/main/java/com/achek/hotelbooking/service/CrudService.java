package com.achek.hotelbooking.service;

import com.achek.hotelbooking.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Shared CRUD logic. Each concrete service adds its own business rules. */
@Transactional(readOnly = true)
public abstract class CrudService<T> {

    protected final JpaRepository<T, Long> repository;
    protected final String entityName;

    protected CrudService(JpaRepository<T, Long> repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public T findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(entityName, id));
    }

    @Transactional
    public T create(T entity) {
        setId(entity, null);
        return repository.save(entity);
    }

    @Transactional
    public T update(Long id, T source) {
        T existing = findById(id);
        copyFields(source, existing);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    /** Copies the updatable fields of source into target (never the id nor the inverse collections). */
    protected abstract void copyFields(T source, T target);

    /** Makes sure a POST can never overwrite an existing row by sending an id. */
    protected abstract void setId(T entity, Long id);
}
