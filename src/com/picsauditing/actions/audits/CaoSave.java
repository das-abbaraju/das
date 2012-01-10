package com.picsauditing.actions.audits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
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
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailException;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class CaoSave extends AuditActionSupport {
	@Autowired
	protected AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private EmailSenderSpring emailSender;

	protected int caoID = 0;
	private int noteID = 0;
	private String note;
	private String noteMessage = "";
	private String saveMessage = "";
	private boolean noteRequired = false;
	private boolean viewCaoTable = false;
	private List<Integer> caoIDs = new ArrayList<Integer>();
	private AuditStatus status;
	private List<ContractorAuditOperatorWorkflow> caoWorkflow = null;

	// Insurance Policies
	private List<ContractorAuditOperator> caoList;
	private boolean insurance = false;

	// Update flags
	private Set<FlagCriteriaContractor> fco;
	private FlagDataCalculator flagCalc;

	public String showHistory() {
		if (caoID > 0)
			caoWorkflow = caowDAO.findByCaoID(caoID);

		if (caoWorkflow == null) {
			addActionError(getText("CaoSave.ErrorPullingUpRecord"));
			return BLANK;
		}

		return "caoStatus";
	}

	public String editNote() {
		ContractorAuditOperatorWorkflow caoW = caowDAO.findByCaoNoteID(caoID, noteID);
		if (caoW != null) {
			caoW.setNotes(note);
			caowDAO.save(caoW);
		}
		return "caoStatus";
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

			// "Explain why you are changing the status to " + status;
			if (noteRequired)
				noteMessage += getText("Audit.message.ExplainStatusChange",
						new Object[] { getText(status.getI18nKey()) });

			if (status.isIncomplete() && Strings.isEmpty(note) && conAudit != null) {
				if (conAudit.getAuditType().isPqf()) {
					List<AuditData> temp = auditDataDao.findCustomPQFVerifications(conAudit.getId());
					for (AuditData ad : temp) {
						if (!ad.isVerified() && !Strings.isEmpty(ad.getComment())) {
							note += ad.getQuestion().getColumnHeaderOrQuestion() + " Comment : " + ad.getComment();
							note += "\n";
						}
					}
				} else if (conAudit.getAuditType().isAnnualAddendum()) {
					for (OshaAudit oshaAudit : conAudit.getOshas()) {
						if (!oshaAudit.isVerified() && !Strings.isEmpty(oshaAudit.getComment())) {
							note += "OSHA : " + oshaAudit.getComment();
							note += "\n";
						}
					}
					for (AuditData auditData : conAudit.getData()) {
						if (!auditData.isVerified() && !Strings.isEmpty(auditData.getComment())) {
							note += auditData.getQuestion().getColumnHeaderOrQuestion() + " Comment : "
									+ auditData.getComment();
							note += "\n";
						}
					}
				}
			}
		} else
			return ERROR;

		return "caoNoteSave";
	}

	public String refresh() throws RecordNotFoundException, NoRightsException {
		findConAudit();
		auditPercentCalculator.percentCalculateComplete(conAudit, false);
		getValidSteps();
		auditDao.save(conAudit);

		return "refresh";
	}

	public String save() throws RecordNotFoundException, EmailException, IOException, NoRightsException {
		setup();
		if (conAudit != null) {
			if (conAudit.isExpired() && !(conAudit.getAuditType().getClassType().isPolicy() && permissions.isAdmin())) {
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

	private void save(int id) throws RecordNotFoundException, EmailException, IOException {
		ContractorAuditOperator cao = getCaoByID(id);
		if (cao == null)
			throw new RecordNotFoundException("ContractorAuditOperator");

		WorkflowStep step = getStep(cao);

		if (hasActionErrors())
			return;

		AuditStatus prevStatus = cao.getStatus();
		AuditStatus newStatus = step.getNewStatus();

		cao.changeStatus(newStatus, permissions);
		// TODO Are we actually saving this new CAOW?
		
		if (newStatus.isSubmittedResubmitted() && cao.getAudit().getAuditType().isPqf()
				&& cao.getPercentVerified() == 100) {
			ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Complete, permissions);
			caow.setNotes("Auto completed based previously completed verification");
			caow.setAuditColumns(new User(User.SYSTEM));
			// caowDAO.save(caow);
		}

		auditSetExpiresDate(cao, newStatus);

		if (cao.getAudit().getAuditType().getClassType().isPolicy() && cao.getStatus().after(AuditStatus.Incomplete))
			updateFlag(cao);

		// we need to handle the PQF specific's
		if (insurance) {
			ContractorAccount con = cao.getAudit().getContractorAccount();
			con.incrementRecalculation();
			accountDao.save(con);
			// updatedContractors.add(con.getName());
		} else
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

		caoDAO.save(cao);
		setCaoUpdatedNote(prevStatus, cao, note);
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

	private WorkflowStep getStep(ContractorAuditOperator cao) {
		WorkflowStep step = null;
		String forString = " for " + cao.getOperator().getName();
		for (WorkflowStep w : cao.getAudit().getAuditType().getWorkFlow().getSteps()) {
			if (w.getOldStatus() != null && w.getOldStatus().equals(cao.getStatus()) && w.getNewStatus().equals(status)) {
				step = w;
				break;
			}
		}

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

		return step;
	}

	private void sendStatusChangeEmail(WorkflowStep step, ContractorAuditOperator cao) throws EmailException,
			IOException {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(step.getEmailTemplate());

		emailBuilder.setPermissions(permissions);
		if (cao.getAudit().getAuditType().getClassType().isAudit())
			emailBuilder.setFromAddress("\"PICS Auditing\"<audits@picsauditing.com>");
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
		// if (step.getNewStatus().after(AuditStatus.Pending)) {
		// if (audit.getAuditType().isDesktop()) {
		// // Desktops after Pending mode should have their categories locked
		// down
		// for (AuditCatData catData : audit.getCategories()) {
		// if (!catData.isOverride()) {
		// catData.setOverride(true);
		// auditDao.save(catData);
		// }
		// }
		// }
		// }

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

		if (step.getNewStatus().after(AuditStatus.Resubmitted)) {
			// Expire previous audits
			int lastYear = DateBean.getCurrentYear() - 1;
			for (ContractorAudit oldAudit : audit.getContractorAccount().getAudits()) {
				if (!oldAudit.equals(audit) && !oldAudit.isExpired()) {
					if (oldAudit.getAuditType().equals(audit.getAuditType())) {
						if (!audit.getAuditType().isHasMultiple()) {
							oldAudit.setExpiresDate(new Date());
							auditDao.save(oldAudit);
						}
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
}
