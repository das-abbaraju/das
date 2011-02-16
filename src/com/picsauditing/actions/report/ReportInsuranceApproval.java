package com.picsauditing.actions.report;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportInsuranceApproval extends ReportContractorAuditOperator {
	protected ContractorAuditOperatorDAO conAuditOperatorDAO = null;
	protected NoteDAO noteDao = null;
	protected ContractorAccountDAO contractorAccountDAO;

	protected Map<Integer, ContractorAuditOperator> caos = null;
	protected List<Integer> caoids = null;

	protected List<String> newStatuses = null;
	protected Set<String> updatedContractors = new HashSet<String>();

	public ReportInsuranceApproval(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, ContractorAuditOperatorDAO conAuditOperatorDAO, NoteDAO noteDao,
			ContractorAccountDAO contractorAccountDAO, AmBestDAO amBestDAO) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
		this.conAuditOperatorDAO = conAuditOperatorDAO;
		this.noteDao = noteDao;
		this.contractorAccountDAO = contractorAccountDAO;
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
			SelectSQL sql2 = new SelectSQL("pqfdata d");
			sql2.addJoin("JOIN contractor_audit ca ON ca.id = d.auditID AND ca.expiresDate > NOW()");
			sql2.addJoin("JOIN audit_type atype ON atype.id = ca.auditTypeID AND atype.classType = 'Policy'");
			sql2.addJoin("JOIN audit_question q ON q.id = d.questionID");
			sql2.addJoin("JOIN audit_category_rule c ON c.catID = q.categoryID");
			sql2.addField("d.auditID");
			sql2.addField("d.answer certID");
			sql2.addField("c.opID");
			sql2.addWhere("q.questionType = 'FileCertificate'");
			sql2.addWhere("q.columnHeader = 'Certificate'");
			sql2.addWhere("q.number = 1");
			if (permissions.isOperator())
				sql2.addWhere("c.opID = " + permissions.getAccountId());
			if (permissions.isCorporate())
				sql2.addWhere("c.opID IN (" + Strings.implode(permissions.getOperatorChildren()) + ")");

			sql.addJoin("LEFT JOIN (" + sql2.toString() + ") cert ON cert.auditID = ca.id AND cert.opID = caoaccount.id");
			sql.addField("cert.certID");
		}

		sql.addGroupBy("cao.id");
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		return super.execute();
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
