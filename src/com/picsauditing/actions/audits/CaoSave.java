package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorAuditOperatorWorkflowDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class CaoSave extends AuditActionSupport {

	protected int caoID = 0;
	private String note;
	private String noteMessage = "";
	private String saveMessage = "";
	private boolean noteRequired = false;
	private List<Integer> caoIDs = new ArrayList<Integer>();
	private AuditStatus status;
	private List<ContractorAuditOperatorWorkflow> caoWorkflow = null;
	// Insurance Policies
	private List<ContractorAuditOperator> caoList;
	private boolean insurance = false;

	protected AuditPercentCalculator auditPercentCalculator;
	
	private NoteDAO noteDAO;
	protected ContractorAuditOperatorDAO caoDAO;
	protected OshaAuditDAO oshaAuditDAO;
	protected ContractorAuditOperatorWorkflowDAO caoWDAO;
	
	// Update flags
	private Set<FlagCriteriaContractor> fco;
	private FlagDataCalculator flagCalc;

	public CaoSave(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, CertificateDAO certificateDao, OshaAuditDAO oshaAuditDAO, ContractorAuditOperatorDAO caoDAO,
			AuditPercentCalculator auditPercentCalculator, NoteDAO noteDAO,
			ContractorAuditOperatorWorkflowDAO caoWDAO, AuditCategoryRuleCache auditCategoryRuleCache) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditCategoryRuleCache);
		this.caoDAO = caoDAO;
		this.oshaAuditDAO = oshaAuditDAO;
		this.auditPercentCalculator = auditPercentCalculator;
		this.noteDAO = noteDAO;
		this.caoWDAO = caoWDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (auditID > 0) {
			findConAudit();
			
			if (conAudit.isExpired()) {
				addActionError("You can't change an expired " + conAudit.getAuditType().getAuditName());
				return SUCCESS;
			}
			
			caoList = conAudit.getOperators();
		} else {
			if(caoIDs.size() > 0)
				caoList = caoDAO.find(caoIDs);
		}
		if (caoID > 0) {
			if ("statusHistory".equals(button)) {
				if (caoID > 0)
					caoWorkflow = caoWDAO.findByCaoID(caoID);

				if (caoWorkflow == null) {
					addActionError("Error pulling up record, please try again");
					return BLANK;
				}

				return "caoStatus";
			}

			caoIDs.add(caoID);
		}

		if (caoIDs.size() > 0) {
			if ("statusLoad".equals(button)) {
				Set<String> accountNames = new HashSet<String>();
				Set<String> auditNames = new HashSet<String>();

				for (ContractorAuditOperator cao : caoList) {
					if (caoIDs.contains(cao.getId())) {
						if (insurance)
							accountNames.add(cao.getAudit().getContractorAccount().getName());
						else
							accountNames.add(cao.getOperator().getName());

						auditNames.add(cao.getAudit().getAuditType().getAuditName());

						if (!noteRequired) {
							for (WorkflowStep s : cao.getAudit().getAuditType().getWorkFlow().getSteps()) {
								if (s.getOldStatus() != null && cao.getStatus().equals(s.getOldStatus())
										&& status.equals(s.getNewStatus()) && s.isNoteRequired()) {
									noteRequired = true;
									break;
								}
							}
						}
					}
				}

				if (!accountNames.isEmpty()) {
					saveMessage += status.getButton() + " " + Strings.implode(auditNames, ", ") + " for "
							+ Strings.implode(accountNames, ", ") + "";

					if (noteRequired)
						noteMessage += "Explain why you are changing the status to " + status;
				} else
					return ERROR;

				return "caoNoteSave";
			}
			
			Set<String> updatedContractors = new HashSet<String>();
			for (Integer caoID : caoIDs) {
				ContractorAuditOperator cao = getCaoByID(caoID);
				if (cao == null)
					throw new RecordNotFoundException("ContractorAuditOperator");

				WorkflowStep step = getStep(cao);

				if (hasActionErrors())
					return SUCCESS;

				AuditStatus prevStatus = cao.getStatus();
				cao.changeStatus(step.getNewStatus(), permissions);
				// Setting the expiration date
				if(step.getNewStatus().isSubmittedResubmitted()) {
					if(cao.getAudit().getExpiresDate() == null)
						cao.getAudit().setExpiresDate(setExpirationDate());
					else if(cao.getAudit().getAuditType().isRenewable())
						cao.getAudit().setExpiresDate(setExpirationDate());
				}
				if( !cao.getAudit().getAuditType().getWorkFlow().isHasSubmittedStep())
					cao.getAudit().setExpiresDate(setExpirationDate());
				
				if (cao.getAudit().getAuditType().getClassType().isPolicy()
						&& cao.getStatus().after(AuditStatus.Submitted))
					updateFlag(cao);
				
				// we need handle the PQF specific's
				if (insurance) {
					ContractorAccount con = cao.getAudit().getContractorAccount();
					con.incrementRecalculation();
					accountDao.save(con);
					updatedContractors.add(con.getName());
				} else
					checkNewStatus(step, cao);
				
				
				if (step.getEmailTemplate() != null)
					sendStatusChangeEmail(step, cao);
				
				caoDAO.save(cao);
				setCaoUpdatedNote(prevStatus, cao);
			}
			
			if (insurance && updatedContractors.size() > 0) {
				addActionMessage("Email is sent to " + Strings.implode(updatedContractors, ", ")
						+ " notifying them about the policy status change");
			
				return BLANK;
			}
		}
		
		if (conAudit != null) {

			if ("Refresh".equals(button)) {
				auditPercentCalculator
						.percentCalculateComplete(conAudit, false);
				getValidSteps();
				auditDao.save(conAudit);
				return "refresh";
			}
			getValidSteps();
		}

		if ("caoAjaxSave".equals(button))
			return "caoTable";
		return SUCCESS;
	}

	public void temp() {
		// TODO Move this over to the CaoSave class
		/*
		 * findConAudit(); String note = ""; if
		 * (auditStatus.equals(AuditStatus.Active.toString())) {
		 * if(conAudit.getPercentComplete() < 100) return SUCCESS;
		 * 
		 * conAudit.changeStatus(AuditStatus.Active, getUser()); note =
		 * "Verified and Activated the " +
		 * conAudit.getAuditType().getAuditName();
		 * 
		 * if (conAudit.getAuditType().isAnnualAddendum() &&
		 * DateBean.getCurrentYear() - 1 ==
		 * Integer.parseInt(conAudit.getAuditFor())) { // We're activating the
		 * most recent year's audit (ie 2008) for (ContractorAudit audit :
		 * contractor.getAudits()) { if (audit.getAuditType().isAnnualAddendum()
		 * && Integer.parseInt(audit.getAuditFor()) < DateBean.getCurrentYear()
		 * - 3 && !audit.getAuditStatus().isExpired()) { // Any annual audit
		 * before 2006 (ie 2005) audit.setAuditStatus(AuditStatus.Expired);
		 * auditDao.save(audit); } } } } // TODO add a column to auditData to
		 * keep track when the contractor has // changed the answer. if
		 * (auditStatus.equals(AuditStatus.Incomplete.toString())) {
		 * conAudit.changeStatus(AuditStatus.Incomplete, getUser()); if
		 * (conAudit.getAuditType().isPqf()) { List<AuditData> temp =
		 * auditDataDao.findCustomPQFVerifications(conAudit.getId()); for
		 * (AuditData auditData : temp) { AuditCategory auditCategory =
		 * auditData.getQuestion().getCategory(); for (AuditCatData aCatData :
		 * conAudit.getCategories()) { if (aCatData.getCategory() ==
		 * auditCategory && aCatData.getPercentVerified() < 100) {
		 * aCatData.setRequiredCompleted(aCatData.getRequiredCompleted() - 1);
		 * aCatData.setPercentCompleted(99); } } }
		 * conAudit.setPercentComplete(99); auditDao.save(conAudit); } if
		 * (conAudit.getAuditType().isAnnualAddendum()) { for (AuditCatData
		 * aCatData : conAudit.getCategories()) { if
		 * (aCatData.getCategory().getId() == AuditCategory.EMR ||
		 * aCatData.getCategory().getId() == AuditCategory.GENERAL_INFORMATION
		 * || aCatData.getCategory().getId() == AuditCategory.OSHA_AUDIT ||
		 * aCatData.getCategory().getId() == AuditCategory.LOSS_RUN) { if
		 * (aCatData.getPercentVerified() < 100) {
		 * aCatData.setRequiredCompleted(aCatData.getRequiredCompleted() - 1);
		 * aCatData.setPercentCompleted(99);
		 * aCatData.setAuditColumns(permissions); } } }
		 * conAudit.setPercentComplete(99); conAudit.setLastRecalculation(new
		 * Date()); auditDao.save(conAudit); } note = "Rejected " +
		 * conAudit.getAuditType().getAuditName(); }
		 * 
		 * if(!Strings.isEmpty(note)) {
		 * if(!Strings.isEmpty(conAudit.getAuditFor())) note += " " +
		 * conAudit.getAuditFor(); addNote(contractor, note,
		 * NoteCategory.Audits, LowMedHigh.Low, true, Account.EVERYONE,
		 * getUser()); }
		 * 
		 * conAudit = auditDao.save(conAudit); ContractorAccount
		 * contractorAccount = conAudit.getContractorAccount();
		 * contractor.incrementRecalculation();
		 * accountDao.save(contractorAccount);
		 */
	}

	public int getCaoID() {
		return caoID;
	}

	public void setCaoID(int caoID) {
		this.caoID = caoID;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<Integer> getCaoIDs() {
		return caoIDs;
	}

	public void setCaoIDs(List<Integer> caoIDs) {
		this.caoIDs = caoIDs;
	}

	public AuditStatus getStatus() {
		return status;
	}

	public void setStatus(AuditStatus status) {
		this.status = status;
	}

	public String getNoteMessage() {
		return noteMessage;
	}

	public String getSaveMessage() {
		return saveMessage;
	}

	public List<ContractorAuditOperatorWorkflow> getCaoWorkflow() {
		return caoWorkflow;
	}

	public void setCaoWorkflow(List<ContractorAuditOperatorWorkflow> caoWorkflow) {
		this.caoWorkflow = caoWorkflow;
	}

	public boolean isInsurance() {
		return insurance;
	}

	public void setInsurance(boolean insurance) {
		this.insurance = insurance;
	}

	private Date setExpirationDate() {
		Date expiresDate = null;
		Integer months = conAudit.getAuditType().getMonthsToExpire();
		if (months != null && months > 0) {
			if (conAudit.getAuditType().getClassType().isPqf())
				expiresDate = DateBean.getMarchOfThatYear(DateBean.addMonths(new Date(), months));
			else
				expiresDate = DateBean.addMonths(new Date(), months);
		} else {
			// check months first, then do date if empty
			expiresDate = DateBean.getMarchOfNextYear(new Date());
		}
		return expiresDate;
	}

	private ContractorAuditOperator getCaoByID(int id) {
		if (caoList != null) {
			for (ContractorAuditOperator cao : caoList) {
				if (cao.getId() == id)
					return cao;
			}
		}

		return null;
	}
	
	private WorkflowStep getStep(ContractorAuditOperator cao) {
		WorkflowStep step = null;
		for (WorkflowStep w : cao.getAudit().getAuditType().getWorkFlow().getSteps()) {
			if (w.getOldStatus() != null && w.getOldStatus().equals(cao.getStatus())
					&& w.getNewStatus().equals(status)) {
				step = w;
				break;
			}
		}
		
		if (step == null)
			addAlertMessage("No action specified");
		else {
			if (step.getOldStatus().isSubmitted() && step.getNewStatus().isComplete()) {
				if (cao.getPercentVerified() < 100)
					addActionError("Please complete all requirements.");
			}

			if (!cao.getStatus().equals(step.getOldStatus()))
				addActionError("This action cannot be performed because it is not longer in the "
						+ step.getOldStatus() + " state");

			if (step.isNoteRequired() && Strings.isEmpty(note))
				addActionError("You must enter a note");

			if (step.getNewStatus().isSubmittedResubmitted()) {
				if (cao.getPercentComplete() < 100)
					addActionError("Please complete all required questions.");
			}
		}
		
		return step;
	}
	
	private void setCaoUpdatedNote(AuditStatus prevStatus, ContractorAuditOperator cao) {
		if (prevStatus != cao.getStatus()) {
			// Stamping cao workflow
			ContractorAuditOperatorWorkflow caoW = new ContractorAuditOperatorWorkflow();
			if (!Strings.isEmpty(note)) {
				Note newNote = new Note();
				newNote.setAccount(cao.getAudit().getContractorAccount());
				newNote.setAuditColumns(permissions);
				newNote.setSummary("Changed Status from " + prevStatus + " to " + cao.getStatus());
				newNote.setNoteCategory(NoteCategory.Audits);
				newNote.setViewableBy(cao.getOperator());
				newNote.setBody(note);
				noteDAO.save(newNote);
				caoW.setNotes(note);
			}
			
			caoW.setCao(cao);
			caoW.setAuditColumns(permissions);
			caoW.setPreviousStatus(prevStatus);
			caoW.setStatus(cao.getStatus());
			caoDAO.save(caoW);
		}
	}
	
	private void sendStatusChangeEmail(WorkflowStep step, ContractorAuditOperator cao)
	throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(step.getEmailTemplate());

		emailBuilder.setPermissions(permissions);
		if (cao.getAudit().getAuditType().getClassType().isAudit())
			emailBuilder.setFromAddress("\"PICS Auditing\"<audits@picsauditing.com>");
		else
			emailBuilder.setFromAddress("\"" + permissions.getName() + "\"<" 
					+ permissions.getEmail() + ">");
		// One day we may need to store the from and to into the workflow step

		emailBuilder.setContractor(cao.getAudit().getContractorAccount(), 
				cao.getAudit().getAuditType().getClassType().isPolicy() ? 
						OpPerms.ContractorInsurance : OpPerms.ContractorSafety);
		// or??
		emailBuilder.setConAudit(cao.getAudit());

		emailBuilder.addToken("cao", cao);
		EmailQueue email = emailBuilder.build();
		email.setViewableBy(cao.getOperator());
		EmailSender.send(email);
	}
	
	private void checkNewStatus(WorkflowStep step, ContractorAuditOperator cao) {
		ContractorAudit audit = cao.getAudit();
		
		if (step.getNewStatus().isSubmitted()) {
			if (audit.getExpiresDate() == null)
				audit.setExpiresDate(setExpirationDate());
		}

		if (step.getNewStatus().isComplete()) {
			if (cao.getAudit().getAuditType().getClassType().isPolicy()
					&& cao.getOperator().isAutoApproveInsurance()) {
				if (cao.getFlag() != null) {
					if (cao.getFlag().isGreen())
						cao.setStatus(AuditStatus.Approved);
				}
			}
		}

		if (step.getNewStatus().after(AuditStatus.Submitted)) {
			// Expire previous audits
			int lastYear = DateBean.getCurrentYear() - 1;
			for (ContractorAudit oldAudit : audit.getContractorAccount().getAudits()) {
				if (!oldAudit.equals(audit) && !oldAudit.isExpired()) {
					if (oldAudit.getAuditType().equals(audit.getAuditType())) {
						if (audit.getAuditType().isAnnualAddendum()) {
							if (lastYear == Integer.parseInt(audit.getAuditFor())
									&& Integer.parseInt(oldAudit.getAuditFor()) < lastYear - 2) {
								oldAudit.setExpiresDate(new Date());
								auditDao.save(oldAudit);
							}
						} else if (!audit.getAuditType().isHasMultiple()) {
							oldAudit.setExpiresDate(new Date());
							auditDao.save(oldAudit);
						}
					}
				}
			}

			for (AuditCatData auditCatData : audit.getCategories()) {
				if (!auditCatData.isApplies()) {
					PicsLogger.log("removing unused data for category " + auditCatData.getCategory().getName());
					if (audit.getAuditType().isAnnualAddendum() && auditCatData.getCategory().isSha()) {
						switch (auditCatData.getCategory().getId()) {
						case AuditCategory.OSHA_AUDIT:
							oshaAuditDAO.removeByType(audit.getId(), OshaType.OSHA);
							break;
						case AuditCategory.MSHA:
							oshaAuditDAO.removeByType(audit.getId(), OshaType.MSHA);
							break;
						case AuditCategory.CANADIAN_STATISTICS:
							oshaAuditDAO.removeByType(audit.getId(), OshaType.COHS);
							break;
						}
					} else {
						auditDataDao.removeDataByCategory(audit.getId(), auditCatData.getCategory().getId());
					}
				}
			}
		}
	}
	
	private void updateFlag(ContractorAuditOperator cao) {
		if (fco == null || fco != cao.getAudit().getContractorAccount().getFlagCriteria()) {
			fco = cao.getAudit().getContractorAccount().getFlagCriteria();
			flagCalc = new FlagDataCalculator(fco);
		}

		for (ContractorOperator co : cao.getAudit().getContractorAccount().getNonCorporateOperators()) {
			if (cao.hasCaop(co.getOperatorAccount().getId())) {
				FlagColor flagColor = flagCalc.calculateCaoStatus(cao.getAudit().getAuditType(), co.getFlagDatas());
				cao.setFlag(flagColor);
				
				return;
			}
		}
	}

	public boolean isNoteRequired() {
		return noteRequired;
	}

	public void setNoteRequired(boolean noteRequired) {
		this.noteRequired = noteRequired;
	}
}
