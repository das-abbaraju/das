package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.PICS.AuditCriteriaAnswer;
import com.picsauditing.PICS.AuditCriteriaAnswerBuilder;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagCalculatorSingle;
import com.picsauditing.PICS.redFlagReport.Note;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.util.AnswerMapByAudits;

@SuppressWarnings("serial")
public class ContractorFlagAction extends ContractorActionSupport {
	protected ContractorOperatorDAO contractorOperatorDao;
	protected AuditDataDAO auditDataDAO;

	protected int opID;
	protected ContractorOperator co;
	protected FlagCalculatorSingle calculator = new FlagCalculatorSingle();
	protected ContractorOperatorFlagDAO coFlagDao;
	protected String action = "";

	protected List<AuditCriteriaAnswer> acaList;
	protected Date forceEnd;
	protected FlagColor forceFlag;
	protected boolean overrideAll = false;
	protected boolean deleteAll = false;
	
	private ContractorAuditOperatorDAO caoDAO;

	public ContractorFlagAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDao, AuditDataDAO auditDataDAO,
			ContractorOperatorFlagDAO contractorOperatorFlagDAO, ContractorAuditOperatorDAO caoDAO) {
		super(accountDao, auditDao);
		this.contractorOperatorDao = contractorOperatorDao;
		this.auditDataDAO = auditDataDAO;
		this.coFlagDao = contractorOperatorFlagDAO;
		this.caoDAO = caoDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

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
		
		List<Integer> criteriaQuestionIDs = co.getOperatorAccount().getQuestionIDs();
		AnswerMapByAudits answerMapByAudits = auditDataDAO.findAnswersByAudits( contractor.getAudits(), criteriaQuestionIDs );
		
		if ("Override".equals(action)) {
			String text = "Changed the flag color to " + forceFlag;
			Note note = new Note(co.getOperatorAccount().getIdString(), co.getContractorAccount().getIdString(),
					permissions.getUserIdString(), permissions.getUsername(), text);
			note.writeToDB();
		}
		if ("deleteOverride".equals(action)) {
			permissions.tryPermission(OpPerms.EditForcedFlags);
			if (deleteAll == true) {
				for (ContractorOperator co2 : getOperators()) {
					
					//prune our answermapMAP for this operator (take out audits they can't see, and answers to questions they shouldn't see)
					//also note that this uses the copy constructor, so our local variable answerMapByAUdits is not affected by pruning 
					//on each run through the "operators" list.
					
					// Make sure the operator has only the answers that are visible to them
					AnswerMapByAudits answerMapForOperator = new AnswerMapByAudits(answerMapByAudits, co.getOperatorAccount());
					AuditCriteriaAnswerBuilder acaBuilder = new AuditCriteriaAnswerBuilder(answerMapForOperator, co.getOperatorAccount().getFlagQuestionCriteria());
					calculator.setAcaList(acaBuilder.getAuditCriteriaAnswers());

					co2.setForceBegin(null);
					co2.setForceEnd(null);
					co2.setForceFlag(null);
					FlagColor newColor = calculator.calculate();
					co2.getFlag().setFlagColor(newColor);
					contractorOperatorDao.save(co2);
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
		
		// Make sure the operator has only the answers that are visible to them
		AnswerMapByAudits answerMapForOperator = new AnswerMapByAudits(answerMapByAudits, co.getOperatorAccount());
		AuditCriteriaAnswerBuilder acaBuilder = new AuditCriteriaAnswerBuilder(answerMapForOperator, co.getOperatorAccount().getFlagQuestionCriteria());
		calculator.setAcaList(acaBuilder.getAuditCriteriaAnswers());
		
		for( ContractorAudit audit : contractor.getAudits() ) {
			if( audit.getAuditType().getClassType() == AuditTypeClass.Policy ) {
				for (ContractorAuditOperator cao : audit.getOperators()) {
					if (cao.getStatus() == CaoStatus.Awaiting) {
						CaoStatus recommendedStatus = calculator
								.calculateCaoRecommendedStatus(cao);
						cao.setRecommendedStatus(recommendedStatus);
						caoDAO.save(cao);
					}
				}
			}
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

	public List<AuditCriteriaAnswer> getAcaList() {
		return acaList;
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
