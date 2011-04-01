package com.picsauditing.actions.report;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.util.ReportFilterCAOW;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportContractorAuditOperatorWorkflow extends ReportContractorAuditOperator {

	public ReportContractorAuditOperatorWorkflow(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
		filter = new ReportFilterCAOW();
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addField("caow.status caowStatus");
		sql.addJoin("JOIN contractor_audit_operator_workflow caow ON cao.id = caow.caoID");

		getFilter().setShowAuditStatus(false);
		getFilter().setShowCaoStatusChangedDate(false);

	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		ReportFilterCAOW f = getFilter();

		String caowStatusList = Strings.implodeForDB(f.getCaowStatus(), ",");
		if (filterOn(caowStatusList)) {
			sql.addWhere("caow.status IN (" + caowStatusList + ")");
		}
		
		if (filterOn(f.getCaowUpdateDate1())) {
			report.addFilter(new SelectFilterDate("caowUpdateDate1", "caow.updateDate >= '?'", DateBean
					.format(f.getCaowUpdateDate1(), "M/d/yy")));
		}

		if (filterOn(f.getCaowUpdateDate2())) {
			report.addFilter(new SelectFilterDate("caowUpdateDate2", "caow.updateDate < '?'", DateBean.format(
					f.getCaowUpdateDate2(), "M/d/yy")));
		}

	}

	@Override
	public ReportFilterCAOW getFilter() {
		return (ReportFilterCAOW) filter;
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("caowStatus", "CAO Workflow Status"));
	}

}
