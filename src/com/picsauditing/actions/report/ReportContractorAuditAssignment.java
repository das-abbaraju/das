package com.picsauditing.actions.report;

import java.io.File;
import java.util.Calendar;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.util.FileUtils;

@SuppressWarnings("serial")
public class ReportContractorAuditAssignment extends ReportContractorAudits {

	public ReportContractorAuditAssignment(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
	}

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
		sql.addJoin("LEFT JOIN contractor_audit ca2 ON " + "ca2.conID = a.id "
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
			sql.addWhere("c.id in (" + "select c.id from contractor_info c " + "join invoice i on i.accountID = c.id "
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
