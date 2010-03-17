package com.picsauditing.actions.contractors;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.FlagDataDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagData;

@SuppressWarnings("serial")
public class ContractorDashboard extends ContractorActionSupport {

	private ContractorOperatorDAO contractorOperatorDAO;
	private AuditDataDAO dataDAO;
	private FlagDataDAO flagDataDAO;

	private ContractorOperator co;
	private int opID;

	public ContractorDashboard(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDAO, AuditDataDAO dataDAO, FlagDataDAO flagDataDAO) {
		super(accountDao, auditDao);
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.dataDAO = dataDAO;
		this.flagDataDAO = flagDataDAO;
		this.subHeading = "Contractor Dashboard";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		if (opID == 0 && permissions.isOperator())
			opID = permissions.getAccountId();

		co = contractorOperatorDAO.find(id, opID);

		return super.execute();
	}

	public ContractorOperator getCo() {
		return co;
	}

	public int getOpID() {
		return opID;
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

	public List<FlagData> getProblems() {
		return flagDataDAO.findProblems(id, opID);
	}

	public Map<String, Set<FlagData>> getFlaggableData() {
		Map<String, Set<FlagData>> result = new TreeMap<String, Set<FlagData>>();

		for (FlagData fd : flagDataDAO.findByContractorAndOperator(id, opID)) {
			if (result.get(fd.getCriteria().getCategory()) == null)
				result.put(fd.getCriteria().getCategory(), new LinkedHashSet<FlagData>());

			result.get(fd.getCriteria().getCategory()).add(fd);
		}

		return result;
	}
}
