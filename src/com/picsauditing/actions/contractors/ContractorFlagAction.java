package com.picsauditing.actions.contractors;

import java.util.Date;
import java.util.Map;

import com.picsauditing.PICS.FlagCalculatorSingle;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;

public class ContractorFlagAction extends ContractorActionSupport {
	protected ContractorOperatorDAO contractorOperatorDao;
	protected AuditDataDAO auditDataDAO;

	protected int opID;
	protected ContractorOperator co;
	protected FlagCalculatorSingle calculator = new FlagCalculatorSingle();
	protected Map<Integer, AuditData> auditData;
	protected ContractorOperatorFlagDAO coFlagDao;
	protected String action = "";
	
	protected Date forceEnd;
	protected FlagColor forceFlag;

	public ContractorFlagAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDao, AuditDataDAO auditDataDAO,
			ContractorOperatorFlagDAO contractorOperatorFlagDAO) {
		super(accountDao, auditDao);
		this.contractorOperatorDao = contractorOperatorDao;
		this.auditDataDAO = auditDataDAO;
		this.coFlagDao = contractorOperatorFlagDAO;
	}

	public String execute() throws Exception {
		findContractor();
		if (opID == 0)
			opID = permissions.getAccountId();

		co = contractorOperatorDao.find(id, opID);
		co.getOperatorAccount().getFlagOshaCriteria();
		co.getOperatorAccount().getAudits();

		calculator.setAnswerOnly(false);
		calculator.setOperator(co.getOperatorAccount());
		calculator.setContractor(contractor);
		calculator.setConAudits(contractor.getAudits());
		auditData = auditDataDAO.findAnswersByContractor(contractor.getId(), co.getOperatorAccount().getQuestionIDs());
		calculator.setAuditAnswers(auditData);

		if ("deleteOverride".equals(action)) {
			permissions.tryPermission(OpPerms.EditForcedFlags);
			co.setForceBegin(null);
			co.setForceEnd(null);
			co.setForceFlag(null);
		}
		
		if (forceFlag != null && forceEnd != null) {
			permissions.tryPermission(OpPerms.EditForcedFlags);
			co.setForceEnd(forceEnd);
			co.setForceFlag(forceFlag);
		}
		
		FlagColor newColor = calculator.calculate();
		co.getFlag().setFlagColor(newColor);
		contractorOperatorDao.save(co);

		return SUCCESS;
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public ContractorOperator getCo() {
		return co;
	}

	public void setCo(ContractorOperator co) {
		this.co = co;
	}

	public Map<Integer, AuditData> getAuditData() {
		return auditData;
	}

	public void setAuditData(Map<Integer, AuditData> auditData) {
		this.auditData = auditData;
	}

	// Other helper getters for osha criteria
	public boolean isOshaTrirUsed() {
		for (FlagOshaCriteria criteria : co.getOperatorAccount().getFlagOshaCriteria()) {
			if (criteria.getTrir().isRequired())
				return true;
		}
		return false;
	}

	public boolean isOshaLwcrUsed() {
		for (FlagOshaCriteria criteria : co.getOperatorAccount().getFlagOshaCriteria()) {
			if (criteria.getLwcr().isRequired())
				return true;
		}
		return false;
	}

	public boolean isOshaFatalitiesUsed() {
		for (FlagOshaCriteria criteria : co.getOperatorAccount().getFlagOshaCriteria()) {
			if (criteria.getFatalities().isRequired())
				return true;
		}
		return false;
	}

	public boolean isOshaAveragesUsed() {
		for (FlagOshaCriteria criteria : co.getOperatorAccount().getFlagOshaCriteria()) {
			if (criteria.getFatalities().isTimeAverage())
				return true;
			if (criteria.getLwcr().isTimeAverage())
				return true;
			if (criteria.getTrir().isTimeAverage())
				return true;
		}
		return false;
	}
	
	public FlagColor[] getFlagList() {
		return FlagColor.values();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getForceEnd() {
		return forceEnd;
	}

	public void setForceEnd(Date forceEnd) {
		this.forceEnd = forceEnd;
	}

	public FlagColor getForceFlag() {
		return forceFlag;
	}

	public void setForceFlag(FlagColor forceFlag) {
		this.forceFlag = forceFlag;
	}
}
