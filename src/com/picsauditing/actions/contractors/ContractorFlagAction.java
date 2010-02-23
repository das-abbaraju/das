package com.picsauditing.actions.contractors;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.FlagDataDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorFlagAction extends ContractorActionSupport {
	protected ContractorOperatorDAO contractorOperatorDao;
	protected AuditCategoryDataDAO auditCategoryDataDAO;
	protected AuditQuestionDAO auditQuestionDAO;
	protected FlagDataDAO flagDataDAO;

	protected int opID;
	protected ContractorOperator co;
	protected String action = "";

	protected Date forceEnd;
	protected FlagColor forceFlag;
	protected String forceNote;
	protected boolean overrideAll = false;
	protected List<FlagData> flagData;

	public ContractorFlagAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDao,AuditCategoryDataDAO auditCategoryDataDAO,
			AuditQuestionDAO auditQuestionDAO,FlagDataDAO flagDataDAO) {
		super(accountDao, auditDao);
		this.contractorOperatorDao = contractorOperatorDao;
		this.auditCategoryDataDAO = auditCategoryDataDAO;
		this.auditQuestionDAO = auditQuestionDAO;
		this.flagDataDAO = flagDataDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		PicsLogger.start("ContractorFlagAction");
		contractor.setNeedsRecalculation(true);
		contractor.setLastRecalculation(null);
		accountDao.save(contractor);

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

	public AuditCatData getAuditCatData(int auditID, int questionID) {
		AuditQuestion auditQuestion = auditQuestionDAO.find(questionID);
		if (isCanSeeAudit(auditQuestion.getAuditType())) {
			int catID = auditQuestion.getSubCategory().getCategory().getId();
			List<AuditCatData> aList = auditCategoryDataDAO.findAllAuditCatData(auditID, catID);
			if (aList != null && aList.size() > 0) {
				return aList.get(0);
			}
		}
		return null;
	}

	public int getShaTypeID() {
		OshaType shaType = co.getOperatorAccount().getOshaType();
		if (shaType.equals(OshaType.COHS))
			return AuditCategory.CANADIAN_STATISTICS;
		if (shaType.equals(OshaType.MSHA))
			return AuditCategory.MSHA;
		else
			return AuditCategory.OSHA_AUDIT;
	}

	public boolean isCanSeeAudit(AuditType auditType) {
		if (permissions.isContractor() && auditType.isCanContractorView())
			return true;
		if (permissions.hasPermission(OpPerms.ContractorDetails))
			return true;
		return false;
	}
	
	public List<FlagData> getFlagData() {
		if(flagData == null)
			flagData = flagDataDAO.findByContractorAndOperator(contractor.getId(), opID);
		return flagData;
	}
	
	public boolean isOshaFlagged() {
		for(FlagData flagDatas : getFlagData()) {
			if(flagDatas.getCriteria().getOshaType() != null) {
				return true;
			}
		}
		return false;
	}
}
