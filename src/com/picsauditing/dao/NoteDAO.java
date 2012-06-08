package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.notes.ActivityBean;
import com.picsauditing.actions.notes.ActivityBeanAudit;
import com.picsauditing.actions.notes.ActivityBeanNote;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class NoteDAO extends PicsDAO {
	private final Logger logger = LoggerFactory.getLogger(NoteDAO.class);
	@Transactional(propagation = Propagation.NESTED)
	public Note save(Note o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		Note row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(Note row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public Note find(int id) {
		return em.find(Note.class, id);
	}

	public List<Note> findWhere(int id, String where, int limit) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE n.account.id = " + id + " AND " + where;
		String queryText = "FROM Note n " + where + " ORDER BY n.creationDate DESC";
		Query query = em.createQuery(queryText);
		if (limit > 0)
			query.setMaxResults(limit);
		try {
			List<Note> list = query.getResultList();
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new ArrayList<Note>();
	}

	/**
	 * Get a list of notes for the supplied contractorID/operatorID Restrict the
	 * list to only those visible to the user
	 * 
	 * @param accountID
	 * @param permissions
	 * @return
	 */
	public List<Note> getNotes(int accountID, Permissions permissions, String where, int firstResult, int limit) {
		// Make sure the where string is a valid SQL expression
		if (Strings.isEmpty(where))
			where = "1";

		String permWhere;
		// Show the user's private notes
		permWhere = "(createdBy.id = :userID AND viewableBy.id = " + Account.PRIVATE + ")";

		// Show the note available to all users
		permWhere += " OR (viewableBy.id = " + Account.EVERYONE + ")";

		// Show all non-private notes to Admins
		if (permissions.hasPermission(OpPerms.AllOperators))
			permWhere += " OR (viewableBy.id > " + Account.PRIVATE + ")";

		if (permissions.isContractor())
			permWhere += " OR (viewableBy.id > " + Account.PRIVATE + ") AND canContractorView = 1";

		if (permissions.isOnlyAuditor())
			permWhere += " OR (createdBy.id = :userID AND viewableBy.id = " + Account.PicsID + ")";

		// Show intra-company notes users
		if (permissions.isOperator() || permissions.isCorporate())
			permWhere += " OR (viewableBy.id IN (" + Strings.implode(permissions.getVisibleAccounts(), ",") + "))";

		Query query = em.createQuery("FROM Note WHERE account.id = :accountID  AND (" + where + ") AND (" + permWhere
				+ ") AND (canContractorView = 1 OR account.id <> :permAccountID) ORDER BY creationDate DESC");
		query.setParameter("accountID", accountID);
		query.setParameter("userID", permissions.getUserId());
		query.setParameter("permAccountID", permissions.getAccountId());
		query.setFirstResult(firstResult);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	private List<ContractorAuditOperatorWorkflow> getCaowsInDateRange(int accountID, Permissions permissions,
			String whereForWorkflow, Date earliestDate, Date latestDate) {

		String permWhere = "";
		if (permissions.isOperator() || permissions.isCorporate())
			permWhere += "AND (w.cao.operator.id IN (" + Strings.implode(permissions.getVisibleAccounts(), ",") + "))";

		Query query = em
				.createQuery("FROM ContractorAuditOperatorWorkflow w JOIN FETCH w.cao c JOIN FETCH c.audit a WHERE a.contractorAccount.id = :accountID "
						+ whereForWorkflow
						+ " AND w.updateDate > :earliestDate AND w.updateDate <= :latestDate "
						+ permWhere + " ");
		query.setParameter("accountID", accountID);
		query.setParameter("earliestDate", earliestDate);
		query.setParameter("latestDate", latestDate);
		return query.getResultList();
	}

	public List<Note> getTasksForUser(int userID) {
		Query query = em.createQuery("FROM Note WHERE createdBy.id = ? AND status = ? "
				+ "ORDER BY priority DESC, creationDate");
		query.setParameter(1, userID);
		query.setParameter(2, NoteStatus.Open);
		query.setMaxResults(10);
		return query.getResultList();
	}

	public List<Note> getTasksForAccount(int userID, int accountID) {
		Query query = em.createQuery("FROM Note WHERE createdBy.id = ? AND account.id = ? AND status = ? "
				+ "ORDER BY priority DESC, creationDate");
		query.setParameter(1, userID);
		query.setParameter(2, NoteStatus.Open);
		query.setParameter(3, accountID);
		return query.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void addPicsNote(Account account, User user, String summary) {
		Note note = new Note(account, user, summary);
		save(note);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void addPicsAdminNote(Account account, User user, String summary) {
		Note note = new Note(account, user, summary);
		save(note);
	}

	/**
	 * Gathers all notes and related activity (audit changes, i.e. CAOW
	 * workflow) Currently, this works by getting a "page" worth of notes (50
	 * records by default) and then feathering in the CAOW records around it.
	 * So, the user is actually going to see more than 50 records.
	 */
	public List<ActivityBean> getActivity(int accountID, Permissions permissions, String where,
			String whereForWorkflow, NoteCategory[] categoryFilter, int firstResult, int limit) {
		List<ActivityBean> beans = new ArrayList<ActivityBean>();

		// Get a page worth of Notes (plus one to peek ahead)
		List<Note> notes = getNotes(accountID, permissions, where, firstResult, limit + 1);
		// Determine what date range those 50+1 notes represent
		Date latestDate = (firstResult > 1) ? notes.get(0).getCreationDate() : DateBean.getEndOfTime();
		Date earliestDate = extractEarliestDate(notes, limit);
		// FYI: the 51st note, if there was one, is now gone
		// Convert the remaining notes to activity beans
		accumulateNotes(beans, notes, limit);

		// Get all CAOW records that fall within that date range
		List<ContractorAuditOperatorWorkflow> caows = getCaowsInDateRange(accountID, permissions, whereForWorkflow,
				earliestDate, latestDate);
		// Convert the caow records to activity beans
		accumulateWorkflowChanges(beans, caows, categoryFilter);

		// Feather in the caows
		sortByDateDesc(beans);

		return beans;
	}

	/**
	 * Returns a list of Note objects wrapped as an ActivityBean object because
	 * the new standard for display on the notes page is ActivityBean.
	 */
	public List<ActivityBean> getActivity(int accountID, Permissions permissions, String where, int firstResult,
			int limit) {
		List<Note> notes = getNotes(accountID, permissions, where, firstResult, limit);
		ArrayList<ActivityBean> activity = new ArrayList<ActivityBean>();
		accumulateNotes(activity, notes, limit);
		return activity;
	}

	private Date extractEarliestDate(List<Note> notes, int limit) {
		Date earliestDate;
		// if the limit is 50, this will get the 51st note
		Note earliestNote = notes.size() > limit ? notes.get(limit) : null;
		if (earliestNote == null) {
			// There is no 51st note
			earliestDate = DateBean.getStartOfPicsTime();
		} else {
			earliestDate = earliestNote.getCreationDate();
			// We're done with the 51st note
			notes.remove(limit);
		}
		return earliestDate;
	}

	private void accumulateNotes(List<ActivityBean> beans, List<Note> notes, int limit) {
		ActivityBean bean;
		for (Note note : notes) {
			bean = new ActivityBeanNote();
			bean.setId(note.getId());
			bean.setAccount(note.getAccount());
			bean.setAttachment(note.getAttachment());
			bean.setBody(note.getBody());
			bean.setEmployee(note.getEmployee());
			bean.setNoteCategory(note.getNoteCategory());
			bean.setPriority(note.getPriority());
			bean.setSummary(note.getSummary());

			bean.setCreatedBy(note.getCreatedBy());
			bean.setCreationDate(note.getCreationDate());
			bean.setUpdateDate(note.getUpdateDate());
			bean.setUpdatedBy(note.getUpdatedBy());
			// Always sort notes by their creation date, even if modified later
			bean.setSortDate(note.getCreationDate());

			beans.add(bean);
		}
	}

	private void accumulateWorkflowChanges(List<ActivityBean> beans, List<ContractorAuditOperatorWorkflow> caows,
			NoteCategory[] filterCategory) {
		ActivityBean bean;
		for (ContractorAuditOperatorWorkflow caow : caows) {
			bean = new ActivityBeanAudit();

			ContractorAudit audit = caow.getCao().getAudit();
			bean.setAuditId(audit.getId());
			bean.setAuditType(audit.getAuditType());
			bean.setAuditFor(audit.getAuditFor() == null ? "" : audit.getAuditFor());
			bean.setOperator(caow.getCao().getOperator());
			bean.setStatus(caow.getStatus());
			bean.setPreviousStatus(caow.getPreviousStatus());
			bean.setBody(caow.getMappedNote());

			bean.setCreatedBy(caow.getCreatedBy());
			bean.setCreationDate(caow.getCreationDate());
			bean.setUpdatedBy(caow.getUpdatedBy());
			bean.setUpdateDate(caow.getUpdateDate());
			// There really should not be any CAOW records where the creation
			// date and the update date are different (because corrections
			// should be in the form of always adding a reversing entry). But
			// just in case, we'll sort by the update date (unless it's null).
			bean.setSortDate(caow.getUpdateDate() == null ? caow.getCreationDate() : caow.getUpdateDate());

			if (bean.inNoteCategory(filterCategory)) {
				beans.add(bean);
			}
		}
	}

	private void sortByDateDesc(List<ActivityBean> beans) {
		Collections.sort(beans, new Comparator<ActivityBean>() {
			public int compare(ActivityBean bean1, ActivityBean bean2) {
				if (bean1.getSortDate() != null && bean2.getSortDate() != null)
					// We want to go descending, so we're comparing bean1 to
					// bean2, as
					// opposed to the other way around
					return bean2.getSortDate().compareTo(bean1.getSortDate());
				return 0;
			}
		});
	}

}
