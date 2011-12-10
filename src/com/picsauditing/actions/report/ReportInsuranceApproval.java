package com.picsauditing.actions.report;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAuditOperator;

@SuppressWarnings("serial")
public class ReportInsuranceApproval extends ReportContractorAuditOperator {
	@Autowired
	protected ContractorAuditOperatorDAO conAuditOperatorDAO;
	@Autowired
	protected NoteDAO noteDao;
	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;

	protected Map<Integer, ContractorAuditOperator> caos = null;
	protected List<Integer> caoids = null;

	protected List<String> newStatuses = null;
	protected Set<String> updatedContractors = new HashSet<String>();

	public ReportInsuranceApproval() {
		super();
		this.report.setLimit(25);
		orderByDefault = "cao.status DESC, cao.updateDate ASC";
		auditTypeClass = AuditTypeClass.Policy;
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceApproval, OpType.View);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		getFilter().setShowStatus(false);
		getFilter().setShowRecommendedFlag(true);
		getFilter().setShowAMBest(true);

		sql.addJoin("LEFT JOIN contractor_audit_operator_workflow caow ON cao.id = caow.caoID");

		sql.addWhere("a.status IN ('Active','Demo')");

		sql.addField("cao.status as caoStatus");
		sql.addField("caow.notes as caoNotes");
		sql.addField("cao.id as caoId");
		sql.addField("caoaccount.name as caoOperatorName");
		sql.addField("cao.flag as caoRecommendedFlag");

		// Get certificates
		if (permissions.isOperatorCorporate()) {
			sql.addJoin("LEFT JOIN pqfdata d ON d.auditID = ca.id");
			sql.addJoin("JOIN audit_question q ON q.id = d.questionID AND q.questionType = 'FileCertificate' AND q.columnHeader = 'Certificate' AND q.number = 1");
			sql.addJoin("JOIN audit_category_rule acr ON q.categoryID = acr.catID");
			sql.addWhere("acr.opID = cao.opID");

			sql.addField("d.answer certID");

			sql.addWhere("atype.classType = 'Policy'");
		}

		sql.addGroupBy("cao.id");
	}

	public Map<Integer, ContractorAuditOperator> getCaos() {
		return caos;
	}

	public void setCaos(Map<Integer, ContractorAuditOperator> caos) {
		this.caos = caos;
	}

	public List<String> getNewStatuses() {
		return newStatuses;
	}

	public void setNewStatuses(List<String> newStatuses) {
		this.newStatuses = newStatuses;
	}

	public List<Integer> getCaoids() {
		return caoids;
	}

	public void setCaoids(List<Integer> caoids) {
		this.caoids = caoids;
	}
}
