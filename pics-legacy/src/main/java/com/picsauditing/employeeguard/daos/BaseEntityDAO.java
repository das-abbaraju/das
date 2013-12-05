package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.BaseEntity;
import com.picsauditing.employeeguard.entities.ProjectRole;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public abstract class BaseEntityDAO<E extends BaseEntity> {

	@PersistenceContext
	protected EntityManager em;

	protected Class<E> type;

	public E find(Object id) {
		return em.find(type, id);
	}

	@Transactional(propagation = Propagation.NESTED)
	public E save(E entity) {
		entity = persistOrMerge(entity);
		em.flush();
		return entity;
	}

	protected E persistOrMerge(E entity) {
		if (entity.getId() == 0) {
			em.persist(entity);
		} else {
			entity = em.merge(entity);
		}

		return entity;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void delete(E entity) {
		em.remove(entity);
		em.flush();
	}

	public E detach(E entity) {
		em.detach(entity);
		return entity;
	}

	public E refresh(E entity) {
		em.refresh(entity);
		return entity;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void save(List<E> entities) {
		for (E entity : entities) {
			persistOrMerge(entity);
		}

		em.flush();
	}

    @Transactional(propagation = Propagation.NESTED)
    public void delete(List<E> entities) {
        for (E entity : entities) {
            em.remove(entity);
        }

        em.flush();
    }
}
