package com.picsauditing.actions.report;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportContractorAuditAssignment extends ReportContractorAudits {
	@Autowired
	private ContractorAuditDAO contractorAuditDAO;
	@Autowired
	private EmailBuilder emailBuilder;
	@Autowired
	private EmailSenderSpring emailSender;
	@Autowired
	private NoteDAO noteDAO;

	private int[] auditIDs;
	private User auditor;

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.AssignAudits);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		sql.addField("ca.contractorConfirm");
		sql.addField("ca.auditorConfirm");
		sql.addField("ca2.expiresDate AS current_expiresDate");
		sql.addJoin("LEFT JOIN contractor_audit ca2 ON "
				+ "ca2.conID = a.id "
				+ "AND ca2.auditTypeID = ca.auditTypeID AND atype.hasMultiple = 0 AND ca2.id != ca.id "
				+ "AND (ca2.id IN (SELECT auditID FROM contractor_audit_operator WHERE visible = 1 AND status = 'Pending')) ");
		sql.addWhere("ca.id IN (SELECT auditID FROM contractor_audit_operator WHERE visible = 1 AND status = 'Pending')");

		if (getFilter().isUnScheduledAudits()) {
			sql.addWhere("(ca.contractorConfirm IS NULL OR ca.auditorConfirm IS NULL) AND atype.isScheduled = 1");
		} else {
			sql.addWhere("atype.isScheduled=1 OR atype.hasAuditor=1");
		}
		sql.addJoin("LEFT JOIN contractor_audit pqf ON pqf.conID = ca.conID AND pqf.audittypeID = 1");
		sql.addJoin("LEFT JOIN pqfdata manual ON manual.auditID = pqf.id AND manual.questionID = 1331");
		sql.addField("manual.answer AS manswer");
		sql.addField("manual.comment AS mcomment");
		sql.addField("manual.id AS mid");
		sql.addWhere("manual.dateVerified IS NOT NULL");
		if (getFilter().isNotRenewingContractors())
			sql.addWhere("c.renew = 0");
		if (getFilter().isContractorsWithPendingMembership())
			sql.addWhere("c.id in ("
					+ "select c.id from contractor_info c "
					+ "join invoice i on i.accountID = c.id "
					+ "join invoice_item ii on i.id = ii.invoiceID join invoice_fee invf on ii.feeID = invf.id "
					+ "where c.payingFacilities <= 9 and invf.feeClass = 'Membership' and invf.id != 100 and invf.id != 4 and i.status = 'Unpaid'"
					+ " and (ii.amount = invf.defaultAmount or i.totalAmount >= 450) and c.payingFacilities <= 9"
					+ " and i.dueDate < NOW())");
		if (!getFilter().isNotRenewingContractors() && !getFilter().isContractorsWithPendingMembership()) {
			sql.addWhere("c.renew = 1");
			sql.addWhere("c.id not in (" + "select c.id from contractor_info c "
					+ "join invoice i on i.accountID = c.id "
					+ "join invoice_item ii on i.id = ii.invoiceID join invoice_fee invf on ii.feeID = invf.id "
					+ "where invf.feeClass = 'Membership' and invf.id != 100 and invf.id != 4 and i.status = 'Unpaid'"
					+ " and (ii.amount = invf.defaultAmount or i.totalAmount >= 450) and c.payingFacilities <= 9"
					+ " and i.dueDate < NOW())");

		}
		orderByDefault = "ca.creationDate";

		getFilter().setShowUnConfirmedAudits(true);
		getFilter().setShowAuditFor(false);
		getFilter().setShowNotRenewingContractors(true);
		getFilter().setShowContractorsWithPendingMembership(true);
	}

	@RequiredPermission(value = OpPerms.AssignAudits, type = OpType.Edit)
	public String updateAll() throws Exception {
		List<ContractorAudit> contractorAudits = Collections.emptyList();
		String name = getRequestURL();
		String serverName = name.replace(ActionContext.getContext().getName() + ".action", "");

		if (auditIDs != null)
			contractorAudits = contractorAuditDAO.findWhere(1000, "id IN (" + Strings.implode(auditIDs) + ")", "");

		for (ContractorAudit contractorAudit : contractorAudits) {
			User origAuditor = contractorAudit.getAuditor();

			if (contractorAudit.getAuditType().isScheduled() && origAuditor != null && !origAuditor.equals(auditor)) {
				contractorAudit.setAuditorConfirm(null);
			}

			contractorAudit.setAuditor(auditor);
			
			if (auditor == null)
				contractorAudit.setAssignedDate(null);
			else
				contractorAudit.setAssignedDate(new Date());
			// Don't automatically assign a closing auditor to a (new) pending Manual Audit unless it all ready has
			// a closing auditor
			boolean newManualAudit = contractorAudit.getAuditType().isDesktop()
					&& contractorAudit.hasCaoStatus(AuditStatus.Pending) && contractorAudit.getClosingAuditor() == null;
			if (!newManualAudit) {
				if (auditor == null)
					contractorAudit.setClosingAuditor(null);
				else
					contractorAudit.setClosingAuditor(new User(contractorAudit.getIndependentClosingAuditor(auditor)));
			}

			contractorAuditDAO.save(contractorAudit);

			if (contractorAudit.getAuditType().isScheduled() && contractorAudit.getAuditor() != null
					&& contractorAudit.getScheduledDate() != null) {
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
					emailBuilder.setFromAddress("\"Jesse Cota\"<jcota@picsauditing.com>");
					EmailQueue email = emailBuilder.build();
					email.setCcAddresses(null);
					email.setViewableById(Account.PicsID);
					emailSender.send(email);
				}

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
		}

		getFilter().setStatus(new AccountStatus[] { AccountStatus.Active });
		getFilter().setAuditTypeID(new int[] { 2, 17 });

		return execute();
	}

	public int[] getAuditIDs() {
		return auditIDs;
	}

	public void setAuditIDs(int[] auditIDs) {
		this.auditIDs = auditIDs;
	}

	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	public boolean isCanEdit() {
		return permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit);
	}

	public String getYesterday() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_YEAR, -1);
		return DateBean.format(date.getTime(), "M/d/yyyy");
	}

	public String getFileSize(String dataID) {
		int fileID = Integer.parseInt(dataID);
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(fileID));
		File[] files = FileUtils.getSimilarFiles(dir, PICSFileType.data + "_" + fileID);
		File file = files[0];
		if (file != null)
			return FileUtils.size(file);
		return "";
	}
}
