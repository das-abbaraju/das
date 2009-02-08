package com.picsauditing.actions.report;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.SelectContractorAudit;

@SuppressWarnings("serial")
public class ReportCompletePQF extends ReportContractorAudits {
	private Date followUpDate = null;
	private String[] sendMail = null;
	protected ContractorAuditDAO contractorAuditDAO;
	protected EmailBuilder emailBuilder;
	protected NoteDAO noteDAO;

	protected Map<Integer, Date> scheduledDate;

	public ReportCompletePQF(ContractorAuditDAO contractorAuditDAO, EmailBuilder emailBuilder,NoteDAO noteDAO) {
		sql = new SelectContractorAudit();
		this.contractorAuditDAO = contractorAuditDAO;
		this.emailBuilder = emailBuilder;
		this.noteDAO = noteDAO;
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.AuditVerification);
	}
	
	@Override
	public void buildQuery() {
		super.buildQuery();
		
		sql.addWhere("ca.auditStatus = 'Pending'");
		sql.addWhere("ca.auditTypeID IN (1,11)");
		sql.addWhere("a.active = 'Y'");
		sql.addOrderBy("ca.percentComplete DESC");

		getFilter().setShowVisible(false);
		getFilter().setShowTrade(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditStatus(false);
		getFilter().setShowCompletedDate(false);
		getFilter().setShowClosedDate(false);
		getFilter().setShowExpiredDate(false);
		getFilter().setShowPercentComplete(true);

	}
	
	@Override
	public String execute() throws Exception {
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
						
						Note note = new Note();
						note.setAccount(conAudit.getContractorAccount());
						note.setAuditColumns(this.getUser());
						note.setSummary("Pending PQF email sent to " + email.getToAddresses());
						note.setNoteCategory(NoteCategory.Audits);
						noteDAO.save(note);

					} catch (Exception e) {
						e.printStackTrace();
					}
					contractorAuditDAO.save(conAudit);
				}
			}
		}
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

	public Map<Integer, Date> getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Map<Integer, Date> scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
}
