package com.picsauditing.actions.report;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.search.SelectFilter;

public class ReportCompletePQF extends ReportContractorAudits {
	private Date followUpDate = null;
	private String[] sendMail = null;
	protected ContractorAuditDAO contractorAuditDAO;
	protected EmailBuilder emailBuilder;

	public static final String DEFAULT_PERCENT = "-%Complete-";
	protected String percentComplete1;
	protected String percentComplete2;
	protected boolean filterPercentComplete = true;
	protected Map<Integer, Date> scheduledDate;

	public ReportCompletePQF(ContractorAuditDAO contractorAuditDAO, EmailBuilder emailBuilder) {
		sql = new SelectContractorAudit();
		this.contractorAuditDAO = contractorAuditDAO;
		this.emailBuilder = emailBuilder;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.AuditVerification);
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
						emailBuilder.setTemplate(12);
						emailBuilder.setPermissions(permissions);
						emailBuilder.setConAudit(conAudit);
						EmailQueue email = emailBuilder.build();
						EmailSender.send(email);
						ContractorBean.addNote(conAudit.getContractorAccount().getId(), permissions, "Pending PQF email sent to " + email.getToAddresses());
					} catch (Exception e) {
						e.printStackTrace();
					}
					contractorAuditDAO.save(conAudit);
				}
			}
		}
		getFilter().setShowVisible(false);
		getFilter().setShowTrade(false);
		getFilter().setAuditType(false);
		getFilter().setAuditStatus(false);
		getFilter().setCompletedDate(false);
		getFilter().setClosedDate(false);
		getFilter().setExpiredDate(false);
		
		return super.execute();
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
