package com.picsauditing.actions.report;

import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAuditOperatorWorkflowDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.util.ReportFilterCAOW;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportContractorAuditOperatorWorkflow extends ReportContractorAuditOperator {

	private ContractorAuditOperatorWorkflowDAO caowDAO;

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

	}

	public void setCaowDAO(ContractorAuditOperatorWorkflowDAO caowDAO) {
		this.caowDAO = caowDAO;
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		ReportFilterCAOW f = getFilter();

		String caowStatusList = Strings.implodeForDB(f.getCaowStatus(), ",");
		if (filterOn(caowStatusList)) {
			sql.addWhere("caow.status IN (" + caowStatusList + ")");
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
