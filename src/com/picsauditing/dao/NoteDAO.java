package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.User;

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
	
	@SuppressWarnings("unchecked")
	public List<Note> findWhere(int id, String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE account.id = "+ id + " AND " + where;
		Query query = em.createQuery("FROM Note n " + where + " ORDER BY creationDate DESC");
		return query.getResultList();
	}
	
	public void addPicsNote(Account account, User user, String summary) {
		Note note = new Note(account, user, summary);
		save(note);
	}

	public void addPicsAdminNote(Account account, User user, String summary) {
		Note note = new Note(account, user, summary);
		save(note);
	}
}
