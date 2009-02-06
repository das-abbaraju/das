package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Note;
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
	public List<Note> findWhere(int id, String where, int limit) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE account.id = "+ id + " AND " + where;
		Query query = em.createQuery("FROM Note n " + where + " ORDER BY creationDate DESC");
		if (limit > 0)
			query.setMaxResults(limit);
		return query.getResultList();
	}

	/**
	 * Get a list of notes for the supplied contractorID/operatorID
	 * Restrict the list to only those visible to the user
	 * @param accountID
	 * @param permissions
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Note> getNotes(int accountID, Permissions permissions, String where) {
		if (Strings.isEmpty(where))
			where = "1";
		
		String permWhere;
		// Show the user's private notes
		permWhere = "(createdBy.id = :userID AND viewableBy.id = " + Account.PRIVATE + ")";
		
		// Show the note available to all users
		permWhere += " OR (viewableBy.id = " + Account.EVERYONE + ")";
		
		// Show all non-private notes to Admins
		if (permissions.hasPermission(OpPerms.AllOperators))
			permWhere += " OR (viewableBy.id > 2)";
		
		// Show intra-company notes users
		if (permissions.isOperator() || permissions.isCorporate())
			permWhere += " OR (viewableBy.id IN (" + Strings.implode(permissions.getVisibleAccounts(), ",") + ")";
		
		Query query = em.createQuery("FROM Note WHERE account.id = :accountID " +
				"AND status = " + NoteStatus.Closed.ordinal() + 
				" AND (" + where + ") " +
				" AND (" + permWhere + ") " +
				" ORDER BY priority DESC, creationDate");
		query.setParameter("accountID", accountID);
		query.setParameter("userID", permissions.getUserId());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Note> getTasksForUser(int userID) {
		Query query = em.createQuery("FROM Note WHERE createdBy.id = ? AND status = ? " +
				"ORDER BY priority DESC, creationDate");
		query.setParameter(1, userID);
		query.setParameter(2, NoteStatus.Open.ordinal());
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Note> getTasksForAccount(int userID, int accountID) {
		Query query = em.createQuery("FROM Note WHERE createdBy.id = ? AND account.id = ? AND status = ? " +
				"ORDER BY priority DESC, creationDate");
		query.setParameter(1, userID);
		query.setParameter(2, NoteStatus.Open.ordinal());
		query.setParameter(3, accountID);
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
