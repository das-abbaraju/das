package com.picsauditing.actions.contractors;

import java.util.Map;

import com.picsauditing.PICS.FlagCalculatorSingle;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
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
	
	public ContractorFlagAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, ContractorOperatorDAO contractorOperatorDao, AuditDataDAO auditDataDAO) {
		super(accountDao, auditDao);
		this.contractorOperatorDao = contractorOperatorDao;
		this.auditDataDAO = auditDataDAO;
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
}
