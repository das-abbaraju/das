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
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.util.AnswerMapByAudits;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorFlagAction extends ContractorActionSupport {
	protected ContractorOperatorDAO contractorOperatorDao;
	protected AuditDataDAO auditDataDAO;
	protected static AuditCategoryDataDAO auditCategoryDataDAO;
	protected static AuditQuestionDAO auditQuestionDAO;

	protected int opID;
	protected ContractorOperator co;
	protected FlagCalculatorSingle calculator = new FlagCalculatorSingle();
	protected ContractorOperatorFlagDAO coFlagDao;
	protected String action = "";

	protected List<AuditCriteriaAnswer> acaList;
	protected Date forceEnd;
	protected FlagColor forceFlag;
	protected String forceNote;
	protected boolean overrideAll = false;

	private ContractorAuditOperatorDAO caoDAO;
	private EmailSubscriptionDAO subscriptionDAO;

	public ContractorFlagAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDao, AuditDataDAO auditDataDAO,
			ContractorOperatorFlagDAO contractorOperatorFlagDAO, ContractorAuditOperatorDAO caoDAO,
			EmailSubscriptionDAO subscriptionDAO, AuditCategoryDataDAO auditCategoryDataDAO, AuditQuestionDAO auditQuestionDAO) {
		super(accountDao, auditDao);
		this.contractorOperatorDao = contractorOperatorDao;
		this.auditDataDAO = auditDataDAO;
		this.coFlagDao = contractorOperatorFlagDAO;
		this.caoDAO = caoDAO;
		this.subscriptionDAO = subscriptionDAO;
		this.auditCategoryDataDAO = auditCategoryDataDAO;
		this.auditQuestionDAO = auditQuestionDAO;
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
		if (co == null) {
			addActionError("This contractor doesn't work at the given site");
			return BLANK;
		}
		co.getOperatorAccount().getFlagOshaCriteria();
		co.getOperatorAccount().getAudits();

		// calculator.setDebug(true); // for development
		calculator.setAnswerOnly(false);
		calculator.setOperator(co.getOperatorAccount());
		calculator.setContractor(contractor);
		calculator.setConAudits(contractor.getAudits());

		List<Integer> criteriaQuestionIDs = co.getOperatorAccount().getQuestionIDs();
		AnswerMapByAudits answerMapByAudits = auditDataDAO.findAnswersByAudits(contractor.getAudits(),
				criteriaQuestionIDs);

		if (button != null) {
			permissions.tryPermission(OpPerms.EditForcedFlags);

			Note note = new Note();
			note.setAccount(co.getContractorAccount());
			note.setAuditColumns(permissions);
			note.setNoteCategory(noteCategory);
			note.setViewableByOperator(permissions);
			note.setCanContractorView(true);
			note.setBody(forceNote);

			String noteText = "";
			if (button.equalsIgnoreCase("Force Flag")) {
				if (forceFlag.equals(co.getForceFlag()))
					addActionError("You didn't change the flag color");
				if (forceEnd == null)
					addActionError("You didn't specify an end date");
				if (Strings.isEmpty(forceNote))
					addActionError("You must enter a note when forcing a flag ");

				if (getActionErrors().size() > 0) {
					PicsLogger.stop();
					return SUCCESS;
				}

				co.setForceEnd(forceEnd);
				co.setForceFlag(forceFlag);
				noteText = "Forced the flag to " + forceFlag + " for " + co.getOperatorAccount().getName();

				if (overrideAll == true) {
					for (ContractorOperator co2 : getOperators()) {
						if (!co.equals(co2) && !forceFlag.equals(co2.getForceFlag())) {
							co2.setForceEnd(forceEnd);
							co2.setForceFlag(forceFlag);
							co2.setAuditColumns(permissions);
							contractorOperatorDao.save(co2);

							noteText += ", " + co.getOperatorAccount().getName();
						}
					}
				}

			} else if (button.equalsIgnoreCase("Cancel Override")) {
				co.setForceEnd(null);
				co.setForceFlag(null);
				noteText = "Removed the forced flag for " + co.getOperatorAccount().getName();

				if (overrideAll == true) {
					for (ContractorOperator co2 : getOperators()) {
						if (!co.equals(co2) && co2.getForceFlag() != null) {
							// cancel the flag for all my other operators for
							// this contractor
							contractor.setNeedsRecalculation(true);
							co2.setForceEnd(null);
							co2.setForceFlag(null);
							co2.setAuditColumns(permissions);
							contractorOperatorDao.save(co2);

							noteText += ", " + co.getOperatorAccount().getName();
						}
					}
				}
			}
			note.setSummary(noteText);
			getNoteDao().save(note);
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
		AuditCriteriaAnswerBuilder acaBuilder = new AuditCriteriaAnswerBuilder(answerMapForOperator, co
				.getOperatorAccount().getFlagQuestionCriteriaInherited());
		acaList = acaBuilder.getAuditCriteriaAnswers();
		calculator.setAcaList(acaList);

		PicsLogger.start("CaoStatus");
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getClassType().isPolicy()) {
				for (ContractorAuditOperator cao : audit.getOperators()) {
					if (cao.isVisible()) {
						if (cao.getStatus().isSubmitted() || cao.getStatus().isVerified()) {
							FlagColor flagColor = calculator.calculateCaoRecommendedFlag(cao);
							cao.setFlag(flagColor);
							caoDAO.save(cao);
						}
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
			addNote(contractor, "Flag color changed from " + co.getFlag().getFlagColor() + " to " + newColor + " for "
					+ co.getOperatorAccount().getName(), NoteCategory.Flags, LowMedHigh.Med, true, co
					.getOperatorAccount().getId(), new User(User.SYSTEM));
			co.getFlag().setLastUpdate(new Date());
			co.getFlag().setFlagColor(newColor);
		}
		WaitingOn waitingOn = calculator.calculateWaitingOn();
		if (!co.getFlag().getWaitingOn().equals(WaitingOn.None) && waitingOn.equals(WaitingOn.None))
			EventSubscriptionBuilder.contractorFinishedEvent(subscriptionDAO, co);

		co.getFlag().setWaitingOn(waitingOn);
		co.setAuditColumns(permissions);
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

	public boolean isOshaCad7Used() {
		for (FlagOshaCriteria criteria : co.getOperatorAccount().getInheritFlagCriteria().getFlagOshaCriteria()) {
			if (criteria.getCad7().isRequired())
				return true;
		}
		return false;
	}

	public boolean isOshaNeerUsed() {
		for (FlagOshaCriteria criteria : co.getOperatorAccount().getInheritFlagCriteria().getFlagOshaCriteria()) {
			if (criteria.getNeer().isRequired())
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
	 * 
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
		for (AuditCriteriaAnswer aca : acaList)
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

	public String getForceNote() {
		return forceNote;
	}

	public void setForceNote(String forceNote) {
		this.forceNote = forceNote;
	}

	public boolean isOverrideAll() {
		return overrideAll;
	}

	public void setOverrideAll(boolean overrideAll) {
		this.overrideAll = overrideAll;
	}

	public String getYesterday() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_YEAR, -1);
		return DateBean.format(date.getTime(), "M/d/yyyy");
	}
	
	static public AuditCatData getAuditCatData(int auditID, int questionID) {
		AuditQuestion auditQuestion = auditQuestionDAO.find(questionID);
		int catID = auditQuestion.getSubCategory().getCategory().getId();
		List<AuditCatData> aList = auditCategoryDataDAO.findAllAuditCatData(auditID, catID);
		if(aList != null && aList.size() > 0) {
			return aList.get(0);
		}
		return null;
	}
	
	public int getShaTypeID() {
		OshaType shaType = co.getOperatorAccount().getOshaType();
		if(shaType.equals(OshaType.COHS))
			return AuditCategory.CANADIAN_STATISTICS;
		if(shaType.equals(OshaType.MSHA))
			return AuditCategory.MSHA;
		else
			return AuditCategory.OSHA_AUDIT;
	}
}
