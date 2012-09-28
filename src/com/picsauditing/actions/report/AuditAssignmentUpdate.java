package com.picsauditing.actions.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditAssignmentUpdate extends PicsActionSupport implements Preparable {

	protected ContractorAudit contractorAudit = null;
	protected User auditor = null;
	protected User origAuditor = null;

	@Autowired
	protected ContractorAuditDAO dao = null;
	@Autowired
	protected UserDAO userDao = null;
	@Autowired
	protected EmailBuilder emailBuilder;
	@Autowired
	private EmailSender emailSender;

	protected Date origScheduledDate = null;
	protected String origLocation = null;

	@Override
	public void prepare() throws Exception {

		String[] ids = (String[]) ActionContext.getContext().getParameters().get("contractorAudit.id");

		if (ids != null && ids.length > 0) {
			int auditId = Integer.parseInt(ids[0]);
			contractorAudit = dao.find(auditId);
			origAuditor = contractorAudit.getAuditor();
			origScheduledDate = contractorAudit.getScheduledDate();
			origLocation = contractorAudit.getAuditLocation();
		}
	}

	public String execute() throws Exception {
		if (auditor.getId() == 0) {
			return SUCCESS;
		}
		auditor = userDao.find(auditor.getId());

		if (contractorAudit.getAuditType().isScheduled()) {
			if (origAuditor != null && !origAuditor.equals(auditor))
				contractorAudit.setAuditorConfirm(null);

			if ((origScheduledDate != null && !origScheduledDate.equals(contractorAudit.getScheduledDate()))
					|| (origLocation != null && !origLocation.equals(contractorAudit.getAuditLocation()))) {
				contractorAudit.setAuditorConfirm(null);
				contractorAudit.setContractorConfirm(null);
			}
		}
		contractorAudit.setAssignedDate(new Date());
		contractorAudit.setAuditor(auditor);
		// Don't automatically assign a closing auditor to a (new) pending Manual Audit unless it all ready has a
		// closing auditor
		boolean newManualAudit = contractorAudit.getAuditType().isDesktop()
				&& contractorAudit.hasCaoStatus(AuditStatus.Pending) && contractorAudit.getClosingAuditor() == null;
		if (!newManualAudit)
			contractorAudit.setClosingAuditor(new User(contractorAudit.getIndependentClosingAuditor(auditor)));

		if (permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit))
			dao.save(contractorAudit);

		if (contractorAudit.getAssignedDate() != null) {
			output = new SimpleDateFormat(PicsDateFormat.Datetime12Hour).format(contractorAudit.getAssignedDate());
		}

		String name = getRequestURL();
		String serverName = name.replace(ActionContext.getContext().getName() + ".action", "");

		if (contractorAudit.getAuditType().isScheduled() && contractorAudit.getAuditor() != null
				&& contractorAudit.getScheduledDate() != null) {
			if (contractorAudit.getContractorConfirm() == null) {
				emailBuilder.setTemplate(15);
				emailBuilder.clear();
				emailBuilder.setPermissions(permissions);
				emailBuilder.setConAudit(contractorAudit);
				emailBuilder.setUser(contractorAudit.getContractorAccount().getUsers().get(0));

				String seed = "c" + contractorAudit.getContractorAccount().getId() + "id" + contractorAudit.getId();
				String confirmLink = serverName + "ScheduleAuditUpdate.action?type=c&contractorAudit="
						+ contractorAudit.getId() + "&key=" + Strings.hashUrlSafe(seed);
				emailBuilder.addToken("confirmLink", confirmLink);
				emailBuilder.setFromAddress(EmailAddressUtils.PICS_AUDIT_EMAIL_ADDRESS_WITH_NAME);
				EmailQueue email = emailBuilder.build();
				if (contractorAudit.getAuditType().getAccount() != null)
					email.setViewableBy(contractorAudit.getAuditType().getAccount());
				else
					email.setViewableById(Account.EVERYONE);
				emailSender.send(email);
			}
			if (contractorAudit.getAuditorConfirm() == null) {
				emailBuilder.setTemplate(14);
				emailBuilder.clear();
				emailBuilder.setPermissions(permissions);
				emailBuilder.setConAudit(contractorAudit);
				emailBuilder.setUser(contractorAudit.getAuditor());

				String seed = "a" + contractorAudit.getAuditor().getId() + "id" + contractorAudit.getId();
				String confirmLink = serverName + "ScheduleAuditUpdate.action?type=a&contractorAudit="
						+ contractorAudit.getId() + "&key=" + Strings.hashUrlSafe(seed);
				emailBuilder.addToken("confirmLink", confirmLink);
				emailBuilder.setFromAddress(EmailAddressUtils.PICS_AUDIT_EMAIL_ADDRESS_WITH_NAME);
				EmailQueue email = emailBuilder.build();
				email.setCcAddresses(null);
				email.setViewableById(Account.PicsID);
				emailSender.send(email);
			}

			NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
			Note note = new Note();
			note.setAccount(contractorAudit.getContractorAccount());
			note.setAuditColumns(permissions);
			note.setSummary("Audit Schedule updated");
			note.setNoteCategory(NoteCategory.Audits);
			if (contractorAudit.getAuditType().getAccount() != null)
				note.setViewableBy(contractorAudit.getAuditType().getAccount());
			else
				note.setViewableById(Account.EVERYONE);
			noteDAO.save(note);
		}
		return SUCCESS;
	}

	public ContractorAudit getContractorAudit() {
		return contractorAudit;
	}

	public void setContractorAudit(ContractorAudit contractorAudit) {
		this.contractorAudit = contractorAudit;
	}

	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

}
