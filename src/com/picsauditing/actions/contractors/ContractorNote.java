package com.picsauditing.actions.contractors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.ReportFilterNote;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorNote extends ContractorActionSupport {
	private List<Note> openTasks = null;
	private List<Note> notes = null;
	private List<EmailQueue> emailList = null;
	private String returnType = SUCCESS;
	
	private ReportFilterNote filter = new ReportFilterNote();
	
	private NoteDAO noteDAO;
	private EmailQueueDAO emailDAO;

	public ContractorNote(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, NoteDAO noteDAO,
			EmailQueueDAO emailDAO) {
		super(accountDao, auditDao);
		this.noteDAO = noteDAO;
		this.emailDAO = emailDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		findContractor();

		if ("addFilter".equals(button)) {
			return "tasks";
		}
		
		this.subHeading = "Notes/Emails";

		return returnType;
	}

	public List<Note> getOpenTasks() {
		if (openTasks == null)
			openTasks = noteDAO.findWhere(contractor.getId(), "n.status = 1" + getFilters("tasks"), 25);
		
		return openTasks;
	}

	public List<Note> getNotes() {
		if (notes == null)
			notes = noteDAO.findWhere(contractor.getId(), "n.status = 2" + getFilters("notes"), 25);
		
		return notes;
	}
	
	public List<EmailQueue> getEmailList() {
		if (emailList == null)
			emailList = emailDAO.findByContractorId(contractor.getId());
		
		return emailList;
	}

	private String getFilters(String listType) {
		String filterString = "";
		if (!Strings.isEmpty(filter.getKeyword())) {
			filterString += " AND summary LIKE '%" + Utilities.escapeQuotes(filter.getKeyword()) + "%'";
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
			for(LowMedHigh e : filter.getPriority())
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
	
}
