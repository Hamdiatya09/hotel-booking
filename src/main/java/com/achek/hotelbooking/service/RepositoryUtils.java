package com.achek.hotelbooking.service;

import com.achek.hotelbooking.exception.BusinessException;
import com.achek.hotelbooking.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public final class RepositoryUtils {

    private RepositoryUtils() {
    }

    /** Loads a referenced entity (e.g. the hotel of a room) or fails with a clear error. */
    public static <T> T require(JpaRepository<T, Long> repository, Long id, String entityName) {
        if (id == null) {
            throw new BusinessException(entityName + " id is required");
        }
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(entityName, id));
    }
}
