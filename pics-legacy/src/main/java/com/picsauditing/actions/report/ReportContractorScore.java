package com.picsauditing.actions.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.ReportFilterContractorScore;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportContractorScore extends ReportAccount {
	protected OperatorAccountDAO opDAO;
	protected ReportFilterContractorScore filter = new ReportFilterContractorScore();

	public ReportContractorScore(OperatorAccountDAO opDAO) {
		orderByDefault = "a.name";
		this.opDAO = opDAO;
	}

	@Override
	protected void checkPermissions() throws Exception {
		if (permissions.isOperatorCorporate()) {
			OperatorAccount op = opDAO.find(permissions.getAccountId());
			if (!op.getOperatorHeirarchy().contains(10566))
				throw new NoRightsException("Suncor User or PICS Administrator");
		} else if (!permissions.isAdmin())
			throw new NoRightsException("Suncor User or PICS Administrator");
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		getFilter().setShowAuditType(false);
		getFilter().setShowOperator(false);
		getFilter().setShowCaoStatusChangedDate(true);
		getFilter().setShowAuditStatus(true);
		getFilter().setShowPercentComplete(true);
		getFilter().setShowPercentVerified(true);

		// TODO make these constants in AuditType?
		addAuditFields(195); // Suncor Quality Management
		addAuditFields(196); // Suncor 3rd Party

		sql.addField("DATE_FORMAT(IFNULL(c.membershipDate, a.creationDate), '%Y') regYear");

		sql.addWhere("a.status IN ('Active'"
				+ (permissions.getAccountStatus().isDemo() || permissions.isAdmin() ? ",'Demo'" : "") + ")");

		sql.addGroupBy("a.id");
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		if (filterOn(getFilter().getScoreMin())) {
			sql.addWhere("ca195.score >= " + getFilter().getScoreMin());
			setFiltered(true);
		}

		if (filterOn(getFilter().getScoreMax())) {
			sql.addWhere("ca195.score <= " + getFilter().getScoreMax());
			setFiltered(true);
		}
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();

		excelSheet.addColumn(new ExcelColumn("regYear", "Registration Year"));
		excelSheet.addColumn(new ExcelColumn("195Status", getText("AuditType.195.name") + " Status"));
		excelSheet.addColumn(new ExcelColumn("195Score", "QM Score", ExcelCellType.Double));
		excelSheet.addColumn(new ExcelColumn("195Updated", "QM Status Updated", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("196Status", getText("AuditType.196.name") + " Status"));
		excelSheet.addColumn(new ExcelColumn("196Updated", "Status Updated", ExcelCellType.Date));
	}

	public ReportFilterContractorScore getFilter() {
		return filter;
	}

	private void addAuditFields(int type) {
		sql.addJoin((type != 195 ? "LEFT " : "") + "JOIN contractor_audit ca" + type + " ON ca" + type
				+ ".conID = a.id AND ca" + type + ".auditTypeID = " + type);
		sql.addJoin((type != 195 ? "LEFT " : "") + "JOIN contractor_audit_operator cao" + type + " ON cao" + type
				+ ".auditID = ca" + type + ".id");

		if (permissions.isOperatorCorporate())
			sql.addJoin((type != 195 ? "LEFT " : "") + "JOIN contractor_audit_operator_permission caop" + type
					+ " ON caop" + type + ".caoID = cao" + type + ".id AND (caop" + type
					+ ".opID IN (SELECT f.opID FROM facilities f WHERE f.corporateID = 10566) OR caop" + type
					+ ".opID = 10566)");

		sql.addField("ca" + type + ".id " + type + "ID");
		sql.addField("cao" + type + ".status " + type + "Status");
		sql.addField("ca" + type + ".score " + type + "Score");
		sql.addField("DATE_FORMAT(cao" + type + ".statusChangedDate, '%m/%d/%Y') " + type + "Updated");
	}

	@Override
	public Date parseDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(PicsDateFormat.American);

		try {
			return sdf.parse(date);
		} catch (Exception e) {
			return null;
		}
	}
}
