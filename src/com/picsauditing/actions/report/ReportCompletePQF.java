package com.picsauditing.actions.report;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.mail.EmailAuditBean;
import com.picsauditing.mail.EmailTemplates;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.search.SelectFilter;

public class ReportCompletePQF extends ReportContractorAudits {
	private Date followUpDate = null;
	private String[] sendMail = null;
	protected ContractorAuditDAO contractorAuditDAO;
	protected EmailAuditBean emailAuditBean;

	public static final String DEFAULT_PERCENT = "-%Complete-";
	protected String percentComplete1;
	protected String percentComplete2;
	protected boolean filterPercentComplete = true;
	protected Map<Integer, Date> scheduledDate;

	public ReportCompletePQF(ContractorAuditDAO contractorAuditDAO, EmailAuditBean emailAuditBean) {
		sql = new SelectContractorAudit();
		this.contractorAuditDAO = contractorAuditDAO;
		this.emailAuditBean = emailAuditBean;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		sql.addWhere("ca.auditStatus = 'Pending'");
		sql.addWhere("ca.auditTypeID = 1");
		sql.addWhere("a.active = 'Y'");
		sql.addOrderBy("ca.percentComplete DESC");

		if ("SendEmail".equals(button)) {
			if (sendMail.length > 0 && scheduledDate != null) {
				for (int i = 0; i < sendMail.length; i++) {
					ContractorAudit conAudit = contractorAuditDAO.find(Integer.parseInt(sendMail[i]));

					Date newDate = scheduledDate.get(Integer.parseInt(sendMail[i]));
					if (newDate == null || !newDate.after(new Date())) {
						Calendar followUpCal = Calendar.getInstance();
						followUpCal.add(Calendar.DAY_OF_MONTH, 7);
						conAudit.setScheduledDate(followUpCal.getTime());
					} else {
						conAudit.setScheduledDate(newDate);
					}
					try {
						emailAuditBean.setPermissions(permissions);
						emailAuditBean.sendMessage(EmailTemplates.pendingPqf, conAudit);
					} catch (Exception e) {
						e.printStackTrace();
					}
					contractorAuditDAO.save(conAudit);
				}
			}
		}
		toggleFilters();
		return super.execute();
	}

	protected void toggleFilters() {
		filterVisible = false;
		filterTrade = false;
		filterAuditType = false;
		filterAuditStatus = false;
		filterCompletedDate = false;
		filterClosedDate = false;
		filterExpiredDate = false;
	}

	public Date getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}

	public String[] getSendMail() {
		return sendMail;
	}

	public void setSendMail(String[] sendMail) {
		this.sendMail = sendMail;
	}

	public boolean isFilterPercentComplete() {
		return filterPercentComplete;
	}

	public Map<Integer, Date> getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Map<Integer, Date> scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public String getPercentComplete1() {
		return percentComplete1;
	}

	public void setPercentComplete1(String percentComplete1) {
		report.addFilter(new SelectFilter("percentComplete1", "ca.percentComplete >= '?'", percentComplete1));
		this.percentComplete1 = percentComplete1;
	}

	public String getPercentComplete2() {
		return percentComplete2;
	}

	public void setPercentComplete2(String percentComplete2) {
		report.addFilter(new SelectFilter("percentComplete2", "ca.percentComplete < '?'", percentComplete2));
		this.percentComplete2 = percentComplete2;
	}

}
