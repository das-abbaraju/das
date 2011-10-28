package com.picsauditing.actions.notes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.util.ReportFilterNote;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorNotes extends ContractorActionSupport {
	@Autowired
	private EmailQueueDAO emailDAO;

	private List<EmailQueue> emailList = null;
	private String returnType = SUCCESS;
	private ReportFilterNote filter = new ReportFilterNote();

	public String execute() throws Exception {
		findContractor();
		this.subHeading = getText("ReportActivityWatch.label.NotesAndEmails");

		if ("addFilter".equals(button)) {
			return "tasks";
		}

		if ("hasNext".equals(button)) {
			filter.setFirstResult(filter.getFirstResult() + filter.getLimit());
		}

		if ("hasPrevious".equals(button)) {
			filter.setFirstResult(filter.getFirstResult() - filter.getLimit());
		}

		return returnType;
	}

	public List<Note> getNotes() {
		return super.getNotes(getFilters(), filter.getFirstResult(), filter.getLimit());
	}

	public List<EmailQueue> getEmailList() {
		if (emailList == null)
			emailList = emailDAO.findByContractorId(account.getId(), permissions);

		return emailList;
	}

	public boolean isNext() {
		List<Note> noteList = getNotes(getFilters(), filter.getFirstResult() + filter.getLimit(), 100000);
		notes = null;
		if (noteList.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean isPrevious() {
		if (filter.getFirstResult() > 0)
			return true;
		return false;
	}

	private String getFilters() {
		String filterString = "";
		if (!Strings.isEmpty(filter.getKeyword())) {
			filterString += " AND (summary LIKE '%" + Utilities.escapeQuotes(filter.getKeyword()) + "%'"
					+ " OR body LIKE '%" + Utilities.escapeQuotes(filter.getKeyword()) + "%')";
		}
		if (filter.getUserID() != null && filter.getUserID().length > 0) {
			filterString += " AND createdBy.id IN (" + Strings.implode(filter.getUserID(), ",") + ")";
		}
		if (filter.getUserAccountID() != null && filter.getUserAccountID().length > 0) {
			filterString += " AND createdBy.account.id IN (" + Strings.implode(filter.getUserAccountID(), ",") + ")";
		}
		if (filter.getViewableBy() != null && filter.getViewableBy().length > 0) {
			filterString += " AND viewableBy.id IN (" + Strings.implode(filter.getViewableBy(), ",") + ")";
		}
		if (filter.getCategory() != null && filter.getCategory().length > 0) {
			filterString += " AND noteCategory IN (" + Strings.implodeForDB(filter.getCategory(), ",") + ")";
		}

		if (filter.getPriority() != null && filter.getPriority().length > 0) {
			Set<Integer> set = new HashSet<Integer>();
			for (LowMedHigh e : filter.getPriority())
				set.add(e.ordinal());

			filterString += " AND priority IN (" + Strings.implode(set, ",") + ")";
		}
		return filterString;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public ReportFilterNote getFilter() {
		return filter;
	}

	public int getCountRows() {
		List<Note> noteList = getNotes(getFilters(), 0, 100000);
		notes = null;
		return noteList.size();
	}
}
