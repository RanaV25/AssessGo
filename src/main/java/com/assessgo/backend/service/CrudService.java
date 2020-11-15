package com.assessgo.backend.service;

import com.assessgo.backend.entity.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;

public interface CrudService<T extends AbstractEntity> {

	JpaRepository<T, Long> getRepository();

	T save(T entity) throws Exception;

	T update(T entity) throws Exception;

	default void delete(T entity) {
		if (entity == null) {
			throw new EntityNotFoundException();
		}
		getRepository().delete(entity);
	}

	default void delete(long id) {
		delete(load(id));
	}

	default long count() {
		return getRepository().count();
	}

	default T load(long id) {
		T entity = getRepository().findById(id).orElse(null);
		if (entity == null) {
			throw new EntityNotFoundException();
		}
		return entity;
	}
}
