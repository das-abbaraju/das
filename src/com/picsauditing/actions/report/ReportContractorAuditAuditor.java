package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class ReportContractorAuditAuditor extends ReportContractorAuditOperator {

	@Override
	public void buildQuery() {
		super.buildQuery();

		sql.addField("ca2.expiresDate AS expired");
		sql.addJoin("LEFT JOIN contractor_audit ca2 ON "
				+ "ca2.conID = a.id "
				+ "AND ca2.auditTypeID = ca.auditTypeID AND atype.hasMultiple = 0 AND ca2.id != ca.id "
				+ "AND (ca2.id IN (SELECT auditID FROM contractor_audit_operator WHERE visible = 1 AND status = 'Complete')) ");
		sql.addWhere("ca.id IN (SELECT auditID FROM contractor_audit_operator WHERE visible = 1 AND status = 'Pending')");

		if (getFilter().isAuditorType())
			sql.addWhere("ca.auditorID=" + permissions.getUserId());
		else
			sql.addWhere("ca.closingAuditorID=" + permissions.getUserId());

		sql.addWhere("a.status IN ('Active','Demo')");
		if (getFilter().isNotRenewingContractors())
			sql.addWhere("c.renew = 0");
		if (getFilter().isContractorsWithPendingMembership())
			sql.addWhere("c.id in (" + "select c.id from contractor_info c " + "join invoice i on i.accountID = c.id "
					+ "join invoice_item ii on i.id = ii.invoiceID join invoice_fee invf on ii.feeID = invf.id "
					+ "where invf.feeClass = 'Membership' and invf.id != 100 and invf.id != 4 and i.status = 'Unpaid'"
					+ " and (ii.amount = invf.defaultAmount or i.totalAmount >= 450))");
		if (!getFilter().isNotRenewingContractors() && !getFilter().isContractorsWithPendingMembership()) {
			sql.addWhere("c.renew = 1");
			sql.addWhere("c.id not in (" + "select c.id from contractor_info c "
					+ "join invoice i on i.accountID = c.id "
					+ "join invoice_item ii on i.id = ii.invoiceID join invoice_fee invf on ii.feeID = invf.id "
					+ "where invf.feeClass = 'Membership' and invf.id != 100 and invf.id != 4 and i.status = 'Unpaid'"
					+ " and (ii.amount = invf.defaultAmount or i.totalAmount >= 450))");

		}

		sql.addGroupBy("a.id, ca.id");
		orderByDefault = "ISNULL(ca2.expiresDate), ca2.expiresDate, ca.assignedDate DESC";

		getFilter().setShowAuditor(false);
		getFilter().setShowAuditorType(true);
		getFilter().setShowStatus(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
	}

	public AuditStatus[] getAuditStatusList() {
		AuditStatus[] list = { AuditStatus.Pending, AuditStatus.Submitted };
		return list;
	}

	public List<AuditType> getAuditTypeList() {
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		List<AuditType> list = dao.findAll();
		List<AuditType> list2 = new ArrayList<AuditType>();

		// Remove the AuditTypes that don't have an audit
		for (AuditType auditType : list)
			if (auditType.isHasAuditor())
				list2.add(auditType);
		return list2;
	}

	public boolean isIndepenentAuditor() {
		return permissions.isIndependentAuditor();
	}

}
