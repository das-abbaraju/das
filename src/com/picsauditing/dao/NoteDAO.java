package com.picsauditing.dao;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

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
			where = "WHERE n.account.id = "+ id + " AND " + where;
		Query query = em.createQuery("SELECT n FROM Note n " + where + " ORDER BY n.creationDate DESC");
		return query.getResultList();
	}
	
	public void addPicsNote(Account account, User user, String summary) {
		Note note = new Note();
		note.setAccount(account);
		note.setViewableBy(new Account());
		note.getViewableBy().setId(Account.PicsID);
		note.setAuditColumns(user);
		note.setSummary(summary);
		save(note);
	}

	public void addPicsAdminNote(Account account, User user, String summary) {
		Note note = new Note();
		note.setAccount(account);
		note.setViewableBy(new Account());
		note.getViewableBy().setId(Account.PicsID);
		note.setAuditColumns(user);
		note.setSummary(summary);
		save(note);
	}
}
