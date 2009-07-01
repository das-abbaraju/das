package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportAccountAudits extends ReportAccount {

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addAudit(AuditType.PQF);
		if (permissions.isOperator()) {
			sql.addField("flags.waitingOn");
			if(download) {
				sql.addJoin("LEFT JOIN contractor_tag cg ON cg.conID = a.id");
				sql.addJoin("LEFT JOIN operator_tag ot ON ot.id = cg.tagID AND ot.opID = "+ permissions.getAccountId());
				sql.addField("ot.tag");
			}
		}	
		
		filteredDefault = true;

		// Getting the certificate info per contractor is too difficult!
		/*
		String certTable = "SELECT contractor_id, count(*) certificateCount FROM certificates WHERE status = 'Approved'";
		if (permissions.isOperator())
			certTable += " AND operator_id = " + permissions.getAccountId();
		if (permissions.isCorporate())
			certTable += " AND operator_id IN (SELECT facilityID FROM facilities WHERE corporateID = "
					+ permissions.getAccountId() + ")";
		certTable += " GROUP BY contractor_id";
		sql.addJoin("LEFT JOIN (" + certTable + ") certs ON certs.contractor_id = a.id");
		sql.addField("certs.certificateCount");
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
	}
}
