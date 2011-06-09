package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class ReportContractorAuditAuditor extends ReportContractorAuditOperator {

	public ReportContractorAuditAuditor(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		sql.addJoin("LEFT JOIN (SELECT ca.conID, ca.auditTypeID, MAX(ca.expiresDate) expired FROM contractor_audit ca "
				+ "WHERE ca.auditTypeID = 2 AND expiresDate < NOW() GROUP BY ca.conID "
				+ "ORDER BY ca.expiresDate DESC) ca2 ON ca2.conID = ca.conID AND ca2.auditTypeID = ca.auditTypeID");
		sql.addField("ca2.expired");

		sql.addWhere("ca.auditorID=" + permissions.getUserId());
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
		orderByDefault = "ISNULL(ca2.expired), ca2.expired, ca.assignedDate DESC";

		getFilter().setShowAuditor(false);
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
}
