package com.picsauditing.actions.audits;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.audits.AuditBuilderFactory;
import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class ContractorSimulator extends PicsActionSupport {

	private ContractorAccount contractor;
	private Set<Integer> operatorIds;
	private List<OperatorAccount> operators;
	private Map<AuditType, List<AuditTypeRule>> audits;
	private AuditType auditType;
	private List<AuditCategory> categories = new ArrayList<AuditCategory>();

    @Autowired
    private AuditBuilderFactory auditBuilderFactory;

	@Override
	@RequiredPermission(value = OpPerms.ContractorSimulator)
	public String execute() throws Exception {
		if (contractor == null) {
			if (operatorIds != null && operatorIds.size() == 1) {
				operators = new ArrayList<OperatorAccount>();
				for (Integer opID : operatorIds) {
					OperatorAccount operator = dao.findWhere(OperatorAccount.class, "id = " + opID, 1).get(0);
					operators.add(operator);
				}
			}

			return SUCCESS;
		}

		operators = new ArrayList<OperatorAccount>();
		for (Integer opID : operatorIds) {
			ContractorOperator co = new ContractorOperator();
			OperatorAccount operator = dao.findWhere(OperatorAccount.class, "id = " + opID, 1).get(0);
			operators.add(operator);
			co.setContractorAccount(contractor);
			co.setOperatorAccount(operator);
			co.setDefaultWorkStatus();
			contractor.getOperators().add(co);
		}
		contractor.setAuditColumns();

		if (auditType != null && auditType.getId() > 0) {
            categories = auditBuilderFactory.getContractorSimulatorCategories(auditType.getId(), contractor);
			return "categories";
		} else {
            audits = auditBuilderFactory.getContractorSimulatorAudits(contractor);
			return "audits";
		}
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public Map<AuditType, List<AuditTypeRule>> getAudits() {
		return audits;
	}

	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType audit) {
		this.auditType = audit;
	}

	public Set<Integer> getOperatorIds() {
		return operatorIds;
	}

	public void setOperatorIds(Set<Integer> operatorIds) {
		this.operatorIds = operatorIds;
	}

	public List<OperatorAccount> getOperators() {
		return operators;
	}

	public List<AuditCategory> getCategories() {
		return categories;
	}
}
