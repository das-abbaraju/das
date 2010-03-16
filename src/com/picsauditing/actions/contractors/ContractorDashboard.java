package com.picsauditing.actions.contractors;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorOperator;

@SuppressWarnings("serial")
public class ContractorDashboard extends ContractorActionSupport {

	private ContractorOperatorDAO contractorOperatorDAO;
	private AuditDataDAO dataDAO;

	private ContractorOperator co;
	private int opID;

	public ContractorDashboard(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDAO, AuditDataDAO dataDAO) {
		super(accountDao, auditDao);
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.dataDAO = dataDAO;
		this.subHeading = "Contractor Dashboard";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		if (opID > 0)
			co = contractorOperatorDAO.find(id, opID);
		else if (permissions.isOperator())
			co = contractorOperatorDAO.find(id, permissions.getAccountId());

		if (co == null) {
			addActionError("This contractor doesn't work at the given site");
			return BLANK;
		}

		return super.execute();
	}

	public ContractorOperator getCo() {
		return co;
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
