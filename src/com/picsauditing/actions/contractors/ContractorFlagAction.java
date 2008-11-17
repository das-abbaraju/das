package com.picsauditing.actions.contractors;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagCalculatorSingle;
import com.picsauditing.PICS.redFlagReport.Note;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
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
	protected boolean overrideAll = false;
	protected boolean deleteAll = false;

	public ContractorFlagAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDao, AuditDataDAO auditDataDAO,
			ContractorOperatorFlagDAO contractorOperatorFlagDAO) {
		super(accountDao, auditDao);
		this.contractorOperatorDao = contractorOperatorDao;
		this.auditDataDAO = auditDataDAO;
		this.coFlagDao = contractorOperatorFlagDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		if (contractor.isPaymentOverdue())
			addActionError("This contractor has an outstanding invoice due");

		if (opID == 0)
			opID = permissions.getAccountId();

		// If the contractor isn't assigned to this facility (in
		// generalcontractors table)
		// then the following will throw an exception
		// We must either re-engineer the way we query co's and their flags
		// or merge the gc and flag tables into one (I prefer the latter) Trevor
		// 5/29/08
		co = contractorOperatorDao.find(id, opID);
		co.getOperatorAccount().getFlagOshaCriteria();
		co.getOperatorAccount().getAudits();

		//calculator.setDebug(true); // for development
		calculator.setAnswerOnly(false);
		calculator.setOperator(co.getOperatorAccount());
		calculator.setContractor(contractor);
		calculator.setConAudits(contractor.getAudits());
		auditData = auditDataDAO.findAnswersByContractor(contractor.getId(), co.getOperatorAccount().getQuestionIDs());
		calculator.setAuditAnswers(auditData);

		if ("Override".equals(action)) {
			String text = "Changed the flag color to " + forceFlag;
			Note note = new Note(co.getOperatorAccount().getIdString(), co.getContractorAccount().getIdString(),
					permissions.getUserIdString(), permissions.getUsername(), text);
			note.writeToDB();
		}
		if ("deleteOverride".equals(action)) {
			permissions.tryPermission(OpPerms.EditForcedFlags);
			if (deleteAll == true) {
				for (ContractorOperator operator : getOperators()) {
					operator.setForceBegin(null);
					operator.setForceEnd(null);
					operator.setForceFlag(null);
					FlagColor newColor = calculator.calculate();
					operator.getFlag().setFlagColor(newColor);
					contractorOperatorDao.save(operator);
				}
				return SUCCESS;
			} else {
				co.setForceBegin(null);
				co.setForceEnd(null);
				co.setForceFlag(null);
			}

		}

		if (forceFlag != null && forceEnd != null) {
			permissions.tryPermission(OpPerms.EditForcedFlags);
			if (overrideAll == true) {
				for (ContractorOperator operator : getOperators()) {
					operator.setForceEnd(forceEnd);
					operator.setForceFlag(forceFlag);
					// FlagColor newColor = calculator.calculate();
					operator.getFlag().setFlagColor(forceFlag);
					contractorOperatorDao.save(operator);
				}
				return SUCCESS;
			} else {
				co.setForceEnd(forceEnd);
				co.setForceFlag(forceFlag);
			}
		}

		if (co.getFlag() == null) {
			// Add a new flag for the contractor
			ContractorOperatorFlag newFlag = new ContractorOperatorFlag();
			newFlag.setFlagColor(FlagColor.Red); // Always start with Red
			newFlag.setContractorAccount(co.getContractorAccount());
			newFlag.setOperatorAccount(co.getOperatorAccount());
			newFlag.setLastUpdate(new Date());
			newFlag = coFlagDao.save(newFlag);
			co.setFlag(newFlag);
		}

		FlagColor newColor = calculator.calculate();
		if (newColor != null && !newColor.equals(co.getFlag().getFlagColor()))
			co.getFlag().setLastUpdate(new Date());
		co.getFlag().setFlagColor(newColor);
		co.getFlag().setWaitingOn(calculator.calculateWaitingOn());
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

	public boolean isOverrideAll() {
		return overrideAll;
	}

	public void setOverrideAll(boolean overrideAll) {
		this.overrideAll = overrideAll;
	}

	public boolean isDeleteAll() {
		return deleteAll;
	}

	public void setDeleteAll(boolean deleteAll) {
		this.deleteAll = deleteAll;
	}

	public String getYesterday() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_YEAR, -1);
		return DateBean.format(date.getTime(), "M/d/yyyy");
	}

}
