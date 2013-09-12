package com.picsauditing.actions.report;

import java.io.File;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportContractorAuditAssignment extends ReportContractorAudits {
	private int[] auditIDs;
	private User auditor;

	public ReportContractorAuditAssignment() {
		super();
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
		sql.addField("If(ca2.expiresDate IS NULL OR ca2.expiresDate <= NOW(), null, ca2.expiresDate) AS current_expiresDate");
		sql.addJoin("LEFT JOIN contractor_audit ca2 ON "
				+ "ca2.conID = a.id "
				+ "AND ca2.auditTypeID = ca.auditTypeID AND atype.hasMultiple = 0 AND ca2.id != ca.id "
				+ "AND (ca2.id IN (SELECT auditID FROM contractor_audit_operator WHERE visible = 1 AND status = 'Complete')) ");
		sql.addWhere("ca.id IN (SELECT auditID FROM contractor_audit_operator WHERE visible = 1 AND status = 'Pending')");
		sql.addGroupBy("a.id, ca.id");

		if (getFilter().isUnScheduledAudits()) {
			sql.addWhere("(ca.contractorConfirm IS NULL OR ca.auditorConfirm IS NULL) AND atype.isScheduled = 1");
		} else {
			sql.addWhere("atype.isScheduled=1 OR atype.hasAuditor=1");
		}
		sql.addJoin("LEFT JOIN contractor_audit pqf ON pqf.conID = ca.conID AND pqf.audittypeID = 1");
		sql.addJoin("LEFT JOIN contractor_audit_operator pqfCao ON pqf.id = pqfCao.auditID AND pqfCao.status = 'Complete' AND pqfCao.visible = 1");
		sql.addJoin("LEFT JOIN pqfdata manual ON manual.auditID = pqf.id AND manual.questionID = 1331");
		sql.addField("max(pqfCao.`updateDate`) as pqfCompletionDate");
		sql.addField("manual.answer AS manswer");
		sql.addField("manual.comment AS mcomment");
		sql.addField("manual.id AS mid");
		sql.addField("manual.dateVerified");
		if (!permissions.isOperatorCorporate())
			sql.addWhere("manual.dateVerified IS NOT NULL");
		sql.addWhere("ca.expiresDate IS NULL OR ca.expiresDate > NOW()");
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
		
		if (permissions.isOperatorCorporate()) {
			String groupIds = Strings.implode(permissions.getAllInheritedGroupIds());
			sql.addWhere("atype.assignAudit in (" + groupIds + ")");
		}
		orderByDefault = "ca.creationDate";

		getFilter().setShowUnConfirmedAudits(true);
		getFilter().setShowAuditFor(false);
		getFilter().setShowNotRenewingContractors(true);
		getFilter().setShowContractorsWithPendingMembership(true);
		if (permissions.isOperatorCorporate()) {
			getFilter().setShowTaxID(false);
			getFilter().setShowTrade(false);
			getFilter().setShowUnConfirmedAudits(false);
			getFilter().setShowContractorsWithPendingMembership(false);
			getFilter().setShowTradeInformation(false);
			getFilter().setShowExpiredDate(false);
			getFilter().setShowRegistrationDate(false);
			getFilter().setShowSoleProprietership(false);
			getFilter().setShowDeactivationReason(false);
			getFilter().setShowNotRenewingContractors(false);
			getFilter().setShowPqfType(false);
		}

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
	
	@Override
    public Set<User> getSafetyList() {
    	if (permissions.isAdmin())
    		return super.getSafetyList();
        Set<User> auditorList = new TreeSet<User>();
        auditorList.addAll(userDAO.findAuditors(permissions.getAllInheritedGroupIds()));
        return auditorList;
    }


}
