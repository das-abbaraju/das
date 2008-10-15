package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Note;

@Transactional
public class NoteDAO extends PicsDAO {

	public Note save(Note o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		Note row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public void remove(Note row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public Note find(int id) {
		return em.find(Note.class, id);
	}
}
