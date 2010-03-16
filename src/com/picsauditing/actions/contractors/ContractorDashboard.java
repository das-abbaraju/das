package com.picsauditing.actions.contractors;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class ContractorDashboard extends ContractorActionSupport {

	private OperatorAccountDAO operatorDAO;
	private AuditDataDAO dataDAO;

	private OperatorAccount operator;
	private int opID;

	public ContractorDashboard(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			OperatorAccountDAO operatorDAO, AuditDataDAO dataDAO) {
		super(accountDao, auditDao);
		this.operatorDAO = operatorDAO;
		this.dataDAO = dataDAO;
		this.subHeading = "Contractor Dashboard";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		if (opID > 0)
			operator = operatorDAO.find(opID);
		else if (permissions.isOperator())
			operator = operatorDAO.find(permissions.getAccountId());

		return super.execute();
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public List<AuditData> getServicesPerformed() {
		return dataDAO.findServicesPerformed(id);
	}

	public Map<Integer, List<ContractorOperator>> getActiveOperatorsMap() {

		Map<Integer, List<ContractorOperator>> result = new TreeMap<Integer, List<ContractorOperator>>();
		List<ContractorOperator> ops = getActiveOperators();

		result.put(0, ops.subList(0, ops.size() / 2));
		result.put(1, ops.subList(ops.size() / 2, ops.size()));

		return result;
	}
}
