package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Webcam;

@Transactional
@SuppressWarnings("unchecked")
public class WebcamDAO extends PicsDAO {
	public Webcam save(Webcam o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		Webcam row = find(id);
		remove(row);
	}

	public void remove(Webcam row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public Webcam find(int id) {
		return em.find(Webcam.class, id);
	}

	public List<Webcam> findActiveUnused() {
		Query q = em.createQuery("FROM Webcam WHERE active = true AND contractor IS NULL ORDER BY id, make, model");
		return q.getResultList();
	}

	public List<Webcam> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("FROM Webcam  " + where + " ORDER BY active DESC, make, model");
		return query.getResultList();
	}

}
