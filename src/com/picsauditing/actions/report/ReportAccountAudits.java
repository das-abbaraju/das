package com.picsauditing.actions.report;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportAccountAudits extends ReportAccount {

	@Override
	protected void buildQuery() {
		super.buildQuery();

		getFilter().setShowMinorityOwned(true);

		if (filterOn(getFilter().getInvoiceDueDate1()) || filterOn(getFilter().getInvoiceDueDate2())) {
			sql.addJoin("JOIN invoice i ON a.id = i.accountID AND i.status = 'Unpaid' AND i.tableType = 'I'");
			if (filterOn(getFilter().getInvoiceDueDate1())) {
				report.addFilter(new SelectFilterDate("invoiceDueDate1", "i.dueDate >= '?'", DateBean.format(
						getFilter().getInvoiceDueDate1(), "M/d/yy")));
			}
			if (filterOn(getFilter().getInvoiceDueDate2())) {
				report.addFilter(new SelectFilterDate("invoiceDueDate2", "i.dueDate < '?'", DateBean.format(getFilter()
						.getInvoiceDueDate2(), "M/d/yy")));
			}
		}

		sql.addAudit(AuditType.PQF);
		if (download) {
			sql.addField("pd2340.answer AS 2340answer");
			sql.addField("pd2354.answer AS 2354answer");
			sql.addField("pd2373.answer AS 2373answer");
			if (getFilter().getMinorityQuestion() != 3) {
				sql.addJoin("LEFT JOIN pqfdata pd2340 ON ca1.id = pd2340.auditID AND pd2340.questionID = 2340");
				sql.addJoin("LEFT JOIN pqfdata pd2354 ON ca1.id = pd2354.auditID AND pd2354.questionID = 2354");
				sql.addJoin("LEFT JOIN pqfdata pd2373 ON ca1.id = pd2373.auditID AND pd2373.questionID = 2373");
			}
			// Stop duplicates from appearing
			sql.addGroupBy("a.name");
		}
		if (permissions.isOperator()) {
			sql.addField("gc.waitingOn");
			if (download) {
				sql.addJoin("LEFT JOIN contractor_tag cg ON cg.conID = a.id");
				sql
						.addJoin("LEFT JOIN operator_tag ot ON ot.id = cg.tagID AND ot.opID = "
								+ permissions.getAccountId());
				sql.addField("ot.tag");
			}
		}

		filteredDefault = true;

		// Getting the certificate info per contractor is too difficult!
		/*
		 * String certTable =
		 * "SELECT contractor_id, count(*) certificateCount FROM certificates WHERE status = 'Approved'"
		 * ; if (permissions.isOperator()) certTable += " AND operator_id = " +
		 * permissions.getAccountId(); if (permissions.isCorporate()) certTable
		 * +=
		 * " AND operator_id IN (SELECT facilityID FROM facilities WHERE corporateID = "
		 * + permissions.getAccountId() + ")"; certTable +=
		 * " GROUP BY contractor_id"; sql.addJoin("LEFT JOIN (" + certTable +
		 * ") certs ON certs.contractor_id = a.id");
		 * sql.addField("certs.certificateCount");
		 */
	}

	public boolean isPqfVisible() {
		return permissions.canSeeAudit(AuditType.PQF);
	}

	@Override
	public void addExcelColumns() {
		super.addExcelColumns();

		if (permissions.isOperator()) {
			excelSheet.addColumn(new ExcelColumn("waitingOn", "Waiting On", ExcelCellType.Enum), 405);
			excelSheet.addColumn(new ExcelColumn("tag", "Contractor Tag"));
		}
		excelSheet.addColumn(new ExcelColumn("2340answer", "Small Business"));
		excelSheet.addColumn(new ExcelColumn("2354answer", "Minority-Owned"));
		excelSheet.addColumn(new ExcelColumn("2373answer", "Women-Owned"));
	}
}
