package com.picsauditing.actions.audits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditSubStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailException;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.models.audits.CaoSaveModel;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class CaoSave extends AuditActionSupport {
	@Autowired
	protected AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private CaoSaveModel caoSaveModel;

	protected int caoID = 0;
	private int noteID = 0;
	private String note;
	private String noteMessage = "";
	private String saveMessage = "";
	private String rejectionReasonCodes;
	private boolean noteRequired = false;
	private boolean viewCaoTable = false;
	private List<Integer> caoIDs = new ArrayList<Integer>();
	private AuditStatus status;
	private AuditSubStatus auditSubStatus;
	private List<ContractorAuditOperatorWorkflow> caoWorkflow = null;
	private boolean addUserNote=false;

	// Insurance Policies
	private List<ContractorAuditOperator> caoList;
	private boolean insurance = false;

	// Update flags
	private Set<FlagCriteriaContractor> contractorFlagCriteria;
	private FlagDataCalculator flagCalculator;

	public String showHistory() {
		if (caoID > 0) {
			if (addUserNote) {
				createUserNote();
			}

			caoWorkflow = caowDAO.findByCaoID(caoID);
		}

		if (caoWorkflow == null) {
			addActionError(getText("CaoSave.ErrorPullingUpRecord"));
			return BLANK;
		}

		return "caoStatus";
	}
	
	private void createUserNote() {
		ContractorAuditOperator cao = dao.find(ContractorAuditOperator.class, caoID);
		
		if (cao == null)
			return;

		ContractorAuditOperatorWorkflow caow = new ContractorAuditOperatorWorkflow();
		caow.setCao(cao);
		caow.setAuditColumns(permissions);
		caow.setPreviousStatus(cao.getStatus());
		caow.setStatus(cao.getStatus());
		
		caoDAO.save(caow);
	}

	public String editNote() {
		ContractorAuditOperatorWorkflow caoW = caowDAO.findByCaoNoteID(caoID, noteID);
		if (caoW != null) {
			caoW.setNotes(note);
			caowDAO.save(caoW);
		}
		return "caoStatus";
	}
	
	/**
	 * Undo: change the cao status back to the previous status it had.
	 * 
	 * @return
	 * @throws RecordNotFoundException
	 * @throws NoRightsException
	 */
	public String undo() throws RecordNotFoundException, NoRightsException {
		setup();
		if (caoID > 0) {
			ContractorAuditOperator cao = caoDAO.find(caoID);
			List<ContractorAuditOperatorWorkflow> caows = caowDAO.findByCaoID(caoID);
			
			ContractorAuditOperatorWorkflow previousCaow = getPreviousCaoWorkflow(caows);
			
			rollbackToPreviousCao(cao, previousCaow);
		}
		
		return SUCCESS;	
	}

	private void rollbackToPreviousCao(ContractorAuditOperator cao,	ContractorAuditOperatorWorkflow previousCaow) {
		if (previousCaow != null) {			
			ContractorAuditOperatorWorkflow newCaow = cao.changeStatus(previousCaow.getPreviousStatus(), permissions);
			newCaow.setNotes(getText("CaoSave.Undo"));
			caowDAO.save(newCaow);
			caoDAO.save(cao);
		}
	}

	private ContractorAuditOperatorWorkflow getPreviousCaoWorkflow(List<ContractorAuditOperatorWorkflow> caows) {
		ContractorAuditOperatorWorkflow previousCaow = null;
		if (hasPreviousCaow(caows)) {
			previousCaow = caows.get(0);
		}
		
		return previousCaow;
	}

	private boolean hasPreviousCaow(List<ContractorAuditOperatorWorkflow> caows) {
		return (caows != null && !caows.isEmpty());
	}

	public String loadStatus() throws RecordNotFoundException, NoRightsException {
		setup();
		Set<String> accountNames = new HashSet<String>();
		Set<String> auditNames = new HashSet<String>();

		for (ContractorAuditOperator cao : caoList) {
			if (caoIDs.contains(cao.getId())) {
				if (insurance)
					accountNames.add(cao.getAudit().getContractorAccount().getName());
				else
					accountNames.add(cao.getOperator().getName());

				auditNames.add(cao.getAudit().getAuditType().getName().toString());
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
			saveMessage += getText(status.getI18nKey("button")) + " " + Strings.implode(auditNames, ", ") + " for "
					+ Strings.implode(accountNames, ", ") + "";

			// Explain why you are changing the status (used for Complete, Reject, Approve)
			if (noteRequired)
				noteMessage += getText("Audit.message.ExplainStatusChange",
						new Object[] { getText(status.getI18nKey()) });

			if (status.isIncomplete() && Strings.isEmpty(note) && conAudit != null) {
				if (note == null)
					note = "";
				if (conAudit.getAuditType().isPqf()) {
					List<AuditData> temp = auditDataDAO.findCustomPQFVerifications(conAudit.getId());
					note += caoSaveModel.generateNote(temp);
				} else if (conAudit.getAuditType().isAnnualAddendum()) {
					note += caoSaveModel.generateNote(conAudit.getData());
				}
			}
		} else
			return ERROR;

		return "caoNoteSave";
	}
	
	public String refresh() throws RecordNotFoundException, NoRightsException {
		findConAudit();
		auditBuilder.recalculateCategories(conAudit);
		auditPercentCalculator.percentCalculateComplete(conAudit, false);
		getValidSteps();
		auditDao.save(conAudit);

		return "refresh";
	}
	
	/**
	 * This is used to save Multiple Rejection Reasons as a "JSON Object"
	 * 
	 * @return
	 * @throws RecordNotFoundException
	 * @throws NoRightsException
	 */
	public String saveRejectionReasons() throws RecordNotFoundException, EmailException, IOException, NoRightsException, ParseException {
		List<String> rejectionReasons = new ArrayList<String>();
		StringBuilder concatenatedNote = new StringBuilder();
		for (int index = 0; index < jsonArray.size(); index++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(index);
			rejectionReasons.add((String)jsonObject.get("id"));
			concatenatedNote.append((String) jsonObject.get("value")).append("\n"); 
		}
		
		note = concatenatedNote.append(note).append("\n").toString(); 
		
		auditSubStatus = determineAuditSubStatus(rejectionReasons);		
        
        return save();
	}
	
	private AuditSubStatus determineAuditSubStatus(List<String> rejectionCodes) {
		for (AuditSubStatus status : AuditSubStatus.values()) {
			if (rejectionCodes.contains(status.toString())) {
				return status;
			}
		}
		
		return AuditSubStatus.Other;
	}

	public String save() throws RecordNotFoundException, EmailException, IOException, NoRightsException {
		setup();
		if (conAudit != null) {
			if (isExpiredPolicy()) {
				addActionError("You can't change an expired " + conAudit.getAuditType().getName().toString());
				return SUCCESS;
			}
		}
		
		for (Integer cID : caoIDs)
			save(cID);
		
		if (conAudit != null)
			getValidSteps();
			
		if (viewCaoTable)
			return "caoTable";
		else
			return SUCCESS;
	}
	
	public String loadCaoTable() throws RecordNotFoundException, EmailException, IOException, NoRightsException {
	    setup();
	    if (conAudit != null) {
            getValidSteps();
	    }
	    
	    return "caoTable";
	}

	private boolean isExpiredPolicy() {
		return conAudit.isExpired() && !(conAudit.getAuditType().getClassType().isPolicy() && permissions.isAdmin());
	}

	public boolean isAddUserNote() {
		return addUserNote;
	}

	public void setAddUserNote(boolean addUserNote) {
		this.addUserNote = addUserNote;
	}

	private void save(int id) throws RecordNotFoundException, EmailException, IOException {
		ContractorAuditOperator cao = getCaoByID(id);
		if (cao == null)
			throw new RecordNotFoundException("ContractorAuditOperator");

		WorkflowStep step = getWorkflowStep(cao);

		if (hasActionErrors())
			return;

		AuditStatus prevStatus = cao.getStatus();
		AuditStatus newStatus = step.getNewStatus();

		cao.changeStatus(newStatus, permissions);
		cao.setAuditSubStatus(auditSubStatus);
	
		auditSetExpiresDate(cao, newStatus);

		if (cao.getAudit().getAuditType().getClassType().isPolicy() && cao.getStatus().after(AuditStatus.Incomplete))
			updateFlag(cao);

		ContractorAccount con = cao.getAudit().getContractorAccount();
		con.incrementRecalculation();
		contractorAccountDao.save(con);

		checkNewStatus(step, cao);

		if (step.getEmailTemplate() != null) {
			sendStatusChangeEmail(step, cao);

			String summary = "Email sent to contractor for Changed Status for "
					+ cao.getAudit().getAuditType().getName().toString() + "(" + cao.getAudit().getId() + ") ";
			if (!Strings.isEmpty(cao.getAudit().getAuditFor()))
				summary += " for " + cao.getAudit().getAuditFor();
			summary += " from " + prevStatus + " to " + cao.getStatus();
			addNote(cao.getAudit().getContractorAccount(), summary, NoteCategory.General, LowMedHigh.Med, false,
					Account.PicsID, null, null);
		}
		
		if (cao.getAudit().getAuditType().isPqf() && newStatus.isSubmitted())
			EventSubscriptionBuilder.pqfSubmittedForCao(cao);
		
		caoSaveModel.updatePqfOnIncomplete(cao.getAudit(), newStatus);

		caoDAO.save(cao);
		
		// TODO: Change the Database Schema so that the notes are saved in either the 
		// contractor_audit_operator_workflow table or the note table, but NOT BOTH.
		updateCaoWorkflow(prevStatus, cao, note);
		
		if (newStatus.isSubmittedResubmitted() && cao.getAudit().getAuditType().isPqf()
				&& cao.getPercentVerified() == 100) {
			ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Complete, permissions);
			if (caow != null) {
				caow.setNotes("Auto completed based previously completed verification");
				caow.setAuditColumns(new User(User.SYSTEM));
				caowDAO.save(caow);
			}
		}
		
		autoExpireOldAudits(cao.getAudit(), newStatus);
	}

	private void setup() throws RecordNotFoundException, NoRightsException {
		if (auditID > 0) {
			findConAudit();
			caoList = conAudit.getOperators();
		} else {
			if (caoIDs.size() > 0)
				caoList = caoDAO.find(caoIDs);
		}
		if (caoID > 0) {
			caoIDs.add(caoID);
		}
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

	private WorkflowStep getWorkflowStep(ContractorAuditOperator cao) {
		WorkflowStep step = null;
		
		for (WorkflowStep workflowStep : cao.getAudit().getAuditType().getWorkFlow().getSteps()) {
			if (isNextWorkflowStep(cao, workflowStep)) {
				step = workflowStep;
				break;
			}
		}

		doWorkflowStepValidation(cao, step);

		return step;
	}

	private void doWorkflowStepValidation(ContractorAuditOperator cao, WorkflowStep step) {
		String forString = " for " + cao.getOperator().getName();
		
		if (step == null)
			addActionError("No action specified" + forString);
		else {
			if (step.getOldStatus().isSubmitted() && step.getNewStatus().isComplete()) {
				if (cao.getPercentVerified() < 100)
					addActionError("Please complete all requirements" + forString);
			}

			if (!cao.getStatus().equals(step.getOldStatus()))
				addActionError("This action cannot be performed because it is not longer in the " + step.getOldStatus()
						+ " state" + forString);

			if (step.isNoteRequired() && Strings.isEmpty(note))
				addActionError("You must enter a note" + forString);

			if (step.getNewStatus().isSubmittedResubmitted()) {
				if (cao.getPercentComplete() < 100)
					addActionError("Please complete all required questions" + forString);
			}
		}
	}

	private boolean isNextWorkflowStep(ContractorAuditOperator cao, WorkflowStep workflowStep) {
		return (workflowStep.getOldStatus() != null 
				&& workflowStep.getOldStatus().equals(cao.getStatus()) 
				&& workflowStep.getNewStatus().equals(status));
	}

	// TODO: Move this method so it is part of the Email Processing, not in the Action
	private void sendStatusChangeEmail(WorkflowStep step, ContractorAuditOperator cao) throws EmailException,
			IOException {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(step.getEmailTemplate());

		emailBuilder.setPermissions(permissions);
		if (cao.getAudit().getAuditType().getClassType().isAudit())
			emailBuilder.setFromAddress(EmailAddressUtils.PICS_AUDIT_EMAIL_ADDRESS_WITH_NAME);
		else
			emailBuilder.setFromAddress("\"" + permissions.getName() + "\"<" + permissions.getEmail() + ">");
		// One day we may need to store the from and to into the workflow step

		emailBuilder.setContractor(cao.getAudit().getContractorAccount(), cao.getAudit().getAuditType().getClassType()
				.isPolicy() ? OpPerms.ContractorInsurance : OpPerms.ContractorSafety);
		// or??
		emailBuilder.setConAudit(cao.getAudit());

		emailBuilder.addToken("cao", cao);
		emailBuilder.addToken("note", note);
		EmailQueue email = emailBuilder.build();
		email.setViewableBy(cao.getOperator());
		emailSender.send(email);
	}

	private void checkNewStatus(WorkflowStep step, ContractorAuditOperator cao) {
		ContractorAudit audit = cao.getAudit();

		/*
		 * // TODO Consider locking the Manual Audit categories here instead of on the
		 * AuditBuilder.fillAuditCategories() advantage is that newly added categories will get added to a Submitted
		 * Manual Audit and existing Subcategories we be locked on Manual Audit. UPGRADE SQL: UPDATE audit_cat_data acd
		 * join contractor_audit ca on acd.auditID = ca.id AND ca.auditTypeID = 2 JOIN contractor_audit_operator cao on
		 * cao.auditID = ca.id and cao.status != 'Pending' SET acd.override = 1 ;
		 */

		if (step.getNewStatus().after(AuditStatus.Resubmitted)) {
			if (cao.getAudit().getAuditType().getClassType().isPolicy() && cao.getOperator().isAutoApproveInsurance()) {
				if (cao.getFlag() != null) {
					if (cao.getFlag().isGreen()) {
						ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Approved, permissions);
						caow.setNotes(getTextParameterized("CaoSave.AutoApprovedNote", cao.getOperator().getName()));
						caowDAO.save(caow);
					}
				}
			}
		}
		
		expireOldAudits(step, audit);
	}

	private void expireOldAudits(WorkflowStep step, ContractorAudit audit) {
		if (step.getNewStatus().after(AuditStatus.Resubmitted)) {
			// Expire previous audits
			for (ContractorAudit oldAudit : audit.getContractorAccount().getAudits()) {
				if (!oldAudit.equals(audit) && !oldAudit.isExpired()) {
					if (oldAudit.getAuditType().equals(audit.getAuditType())) {
						if (audit.getAuditType().isWCB()) {
							oldAudit.setExpiresDate(DateBean.getWCBExpirationDate(oldAudit.getAuditFor()));
						}
						else if (!audit.getAuditType().isHasMultiple() && !audit.getAuditType().isRenewable()) {
							oldAudit.setExpiresDate(new Date());
							auditDao.save(oldAudit);
						}
					}
				}
			}
		}
	}

	private void updateFlag(ContractorAuditOperator cao) {
		if (contractorFlagCriteria == null || contractorFlagCriteria != cao.getAudit().getContractorAccount().getFlagCriteria()) {
			contractorFlagCriteria = cao.getAudit().getContractorAccount().getFlagCriteria();
			flagCalculator = new FlagDataCalculator(contractorFlagCriteria);
		}

		for (ContractorOperator co : cao.getAudit().getContractorAccount().getNonCorporateOperators()) {
			if (cao.hasCaop(co.getOperatorAccount().getId())) {
				FlagColor flagColor = flagCalculator.calculateCaoStatus(cao.getAudit().getAuditType(), co.getFlagDatas());
				cao.setFlag(flagColor);

				return;
			}
		}
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

	public boolean isNoteRequired() {
		return noteRequired;
	}

	public void setNoteRequired(boolean noteRequired) {
		this.noteRequired = noteRequired;
	}

	public int getNoteID() {
		return noteID;
	}

	public void setNoteID(int noteID) {
		this.noteID = noteID;
	}

	public void setAuditPercentCalculator(AuditPercentCalculator auditPercentCalculator) {
		this.auditPercentCalculator = auditPercentCalculator;
	}

	public boolean isViewCaoTable() {
		return viewCaoTable;
	}

	public void setViewCaoTable(boolean viewCaoTable) {
		this.viewCaoTable = viewCaoTable;
	}
	
	public String getRejectionReasonCodes() {
		return rejectionReasonCodes;
	}
	
	public void setRejectionReasonCodes(String rejectionReasonCodes) {
		this.rejectionReasonCodes = rejectionReasonCodes;
	}
}
