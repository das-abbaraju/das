package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.AuditCriteriaAnswer;
import com.picsauditing.PICS.AuditCriteriaAnswerBuilder;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagCalculatorSingle;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.util.AnswerMapByAudits;
import com.picsauditing.util.log.PicsLogger;

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
		this.noteCategory = NoteCategory.Flags;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		PicsLogger.start("ContractorFlagAction");
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
			String text = "Changed the flag color to " + forceFlag + " for "+ co.getOperatorAccount();
			addNote(co.getContractorAccount(), text);
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
					AuditCriteriaAnswerBuilder acaBuilder = new AuditCriteriaAnswerBuilder(answerMapForOperator, co.getOperatorAccount().getFlagQuestionCriteriaInherited());
					acaList = acaBuilder.getAuditCriteriaAnswers();
					calculator.setAcaList(acaList);
					
					co2.setForceBegin(null);
					co2.setForceEnd(null);
					co2.setForceFlag(null);
					FlagColor newColor = calculator.calculate();
					co2.getFlag().setFlagColor(newColor);
					contractorOperatorDao.save(co2);
				}
				PicsLogger.stop();
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
				PicsLogger.stop();
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
		PicsLogger.log(" Found " + answerMapForOperator.getAuditSet().size() + " audits in answerMapForOperator");
		AuditCriteriaAnswerBuilder acaBuilder = new AuditCriteriaAnswerBuilder(answerMapForOperator, co.getOperatorAccount().getFlagQuestionCriteriaInherited());
		acaList = acaBuilder.getAuditCriteriaAnswers();
		calculator.setAcaList(acaList);
		
		PicsLogger.start("CaoStatus");
		for( ContractorAudit audit : contractor.getAudits() ) {
			if( audit.getAuditType().getClassType().isPolicy() ) {
				for (ContractorAuditOperator cao : audit.getOperators()) {
					if (cao.getStatus().isSubmitted() || cao.getStatus().isVerified()) {
						CaoStatus recommendedStatus = calculator
								.calculateCaoRecommendedStatus(cao);
						cao.setRecommendedStatus(recommendedStatus);
						caoDAO.save(cao);
					}
				}
			}
		}
		PicsLogger.stop();
		
		PicsLogger.start("Flag.calculate");
		FlagColor newColor = calculator.calculate();
		PicsLogger.stop();
		if (newColor != null && !newColor.equals(co.getFlag().getFlagColor())) {
			addActionMessage("Flag color has been now updated from " + co.getFlag().getFlagColor() + " to " + newColor);
			addNote(contractor, 
					"Flag color changed from " + co.getFlag().getFlagColor() + " to " + newColor + " for " + co.getOperatorAccount().getName(), 
					NoteCategory.Flags, LowMedHigh.Med, true, co.getOperatorAccount().getId());
			co.getFlag().setLastUpdate(new Date());
			co.getFlag().setFlagColor(newColor);
		}
		co.getFlag().setWaitingOn(calculator.calculateWaitingOn());
		contractorOperatorDao.save(co);

		PicsLogger.stop();
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
		for (FlagOshaCriteria criteria : co.getOperatorAccount().getInheritFlagCriteria().getFlagOshaCriteria()) {
			if (criteria.getTrir().isRequired())
				return true;
		}
		return false;
	}

	public boolean isOshaLwcrUsed() {
		for (FlagOshaCriteria criteria : co.getOperatorAccount().getInheritFlagCriteria().getFlagOshaCriteria()) {
			if (criteria.getLwcr().isRequired())
				return true;
		}
		return false;
	}

	public boolean isOshaFatalitiesUsed() {
		for (FlagOshaCriteria criteria : co.getOperatorAccount().getInheritFlagCriteria().getFlagOshaCriteria()) {
			if (criteria.getFatalities().isRequired())
				return true;
		}
		return false;
	}

	public boolean isOshaAveragesUsed() {
		for (FlagOshaCriteria criteria : co.getOperatorAccount().getInheritFlagCriteria().getFlagOshaCriteria()) {
			if (criteria.getFatalities().isTimeAverage())
				return true;
			if (criteria.getLwcr().isTimeAverage())
				return true;
			if (criteria.getTrir().isTimeAverage())
				return true;
		}
		return false;
	}

	/**
	 * The contractor's OSHA/MSHA record that the operator uses for evaluation
	 * @return
	 */
	public Map<String, OshaAudit> getOshas() {
		return co.getContractorAccount().getOshas().get(co.getOperatorAccount().getOshaType());
	}
	
	public List<AuditCriteriaAnswer> getAcaList() {
		return acaList;
	}
	
	public List<AuditCriteriaAnswer> getAcaListAudits() {
		List<AuditCriteriaAnswer> list = new ArrayList<AuditCriteriaAnswer>();
		for(AuditCriteriaAnswer aca : acaList)
			if (!aca.getClassType().isPolicy())
				list.add(aca);
		return list;
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
