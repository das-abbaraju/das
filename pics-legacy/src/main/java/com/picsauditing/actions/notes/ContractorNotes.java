package com.picsauditing.actions.notes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

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

	public List<ActivityBean> getActivity() {
		List<ActivityBean> activity = noteDao.getActivity(id, permissions, "status IN (1,2)" + getFilters(),
				getFiltersForWorkflow(), filter.getCategory(), filter.getFirstResult(), filter.getLimit());
		for (ActivityBean bean : activity) {
			if (bean.needsComplexSummaryWithTranlations()) {
				bean.setSummary(composeComplexSummary(bean));
			}
		}
		return activity;
	}

	private String composeComplexSummary(ActivityBean bean) {
		// Currently, only ActivityBeanAudit requires a complex summary, so
		// there's no need to test for which bean type this is
		return "<a href='Audit.action?auditID=" + bean.getAuditId() + "'>"
				+ getText(bean.getAuditType().getI18nKey("name")) + " " + bean.getAuditFor()
				+ "</a> &nbsp; &nbsp; <span class='previous-status'>" + getText(bean.getPreviousStatus().getI18nKey())
				+ "</span> &rarr; " + getText(bean.getStatus().getI18nKey()) + " &nbsp; &nbsp; "
				+ bean.getOperator().getName();
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
		return atLeastOneMoreNote(getFilters(), filter.getFirstResult(), filter.getLimit());
	}

	public boolean isPrevious() {
		return (filter.getFirstResult() > 0);
	}

	private String getFilters() {
		String filterString = "";
		if (!Strings.isEmpty(filter.getKeyword())) {
			filterString += " AND (summary LIKE '%" + Strings.escapeQuotesAndSlashes(filter.getKeyword()) + "%'"
					+ " OR body LIKE '%" + Strings.escapeQuotesAndSlashes(filter.getKeyword()) + "%')";
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

	private String getFiltersForWorkflow() {
		String filterString = "";
		if (filter.getPriority() != null && filter.getPriority().length > 0) {
			// CAOW records have no prioority at all, so if the user filters on
			// priorty, then force the query to bring back nothing
			filterString += " AND 1=2";
		}
		if (!Strings.isEmpty(filter.getKeyword())) {
			filterString += " AND (w.cao.audit.auditFor LIKE '%" + Strings.escapeQuotesAndSlashes(filter.getKeyword()) + "%'"
					+ " OR w.notes LIKE '%" + Strings.escapeQuotesAndSlashes(filter.getKeyword()) + "%'"
					+ " OR w.cao.operator.name LIKE '%" + Strings.escapeQuotesAndSlashes(filter.getKeyword()) + "%')";
		}
		if (filter.getUserID() != null && filter.getUserID().length > 0) {
			filterString += " AND w.createdBy.id IN (" + Strings.implode(filter.getUserID(), ",") + ")";
		}
		if (filter.getUserAccountID() != null && filter.getUserAccountID().length > 0) {
			filterString += " AND w.createdBy.account.id IN (" + Strings.implode(filter.getUserAccountID(), ",") + ")";
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
		return getNotes().size();
	}
}
