package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.WorkFlowDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.jpa.entities.Workflow;
import com.picsauditing.jpa.entities.WorkflowState;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageWorkFlow extends PicsActionSupport {

	protected String name;
	protected Workflow workFlow;
	protected int id;
	protected int stepID;
	protected int emailTemplateID;
	protected int statusID;

	protected AuditStatus status;
	protected String label;
	protected String helpText;
	protected AuditStatus oldStatus;
	protected AuditStatus newStatus;
	protected boolean noteRequired;
	protected boolean hasRequirements;

	protected WorkFlowDAO workFlowDAO;
	protected EmailTemplateDAO templateDAO;
	protected List<WorkflowStep> steps;

	public ManageWorkFlow(WorkFlowDAO workFlowDAO, EmailTemplateDAO templateDAO) {
		this.workFlowDAO = workFlowDAO;
		this.templateDAO = templateDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.ManageAuditWorkFlow);
		if (id > 0) {
			workFlow = workFlowDAO.find(id);
		}

		if (button != null) {
			tryPermissions(OpPerms.ManageAuditWorkFlow, OpType.Edit);
			// Get workflow steps
			if ("getSteps".equals(button) && workFlow != null)
				return "steps";

			// create
			if ("create".equalsIgnoreCase(button)) {
				workFlow = new Workflow();
				workFlow.setAuditColumns(permissions);
				workFlow.setHasRequirements(hasRequirements);
				if (Strings.isEmpty(name)) {
					addActionError("Invalid Name");
					return SUCCESS;
				}
				workFlow.setName(name);
				workFlowDAO.save(workFlow);
			}
			// add
			if ("add".equalsIgnoreCase(button) && workFlow != null) {
				if (newStatus == null) {
					addActionError("No new status");
					return "steps";
				}
				if (oldStatus != null && !workFlow.isHasState(oldStatus)) {
					addActionError("Old state must be one of the workflow's states.");
					return "steps";
				}
				if (!workFlow.isHasState(newStatus)) {
					addActionError("New state must be one of the workflow's states.");
					return "steps";
				}
				WorkflowStep ws = new WorkflowStep();
				ws.setWorkflow(workFlow);
				ws.setAuditColumns(permissions);
				ws.setOldStatus(oldStatus);
				ws.setNewStatus(newStatus);
				if (emailTemplateID > 0)
					ws.setEmailTemplate(templateDAO.find(emailTemplateID));
				else
					ws.setEmailTemplate(null);
				ws.setNoteRequired(noteRequired);
				
				if (Strings.isEmpty(label))
					label = newStatus.name();
				TranslatableString buttonLabel = new TranslatableString();
				buttonLabel.putTranslation("en", label, true);
				ws.setName(buttonLabel);
				
				if (!Strings.isEmpty(helpText)) {
					TranslatableString text = new TranslatableString();
					text.putTranslation("en", helpText, true);
					ws.setHelpText(text);
				}

				workFlowDAO.save(ws);
				return "steps";
			}
			// add
			if ("addStatus".equalsIgnoreCase(button) && workFlow != null) {
				if (status == null) {
					addActionError("No status selected.");
					return "steps";
				}
				if (workFlow.isHasState(status)) {
					addActionError("Workflow already has this state.");
					return "steps";
				}
				WorkflowState ws = new WorkflowState();
				ws.setWorkflow(workFlow);
				ws.setAuditColumns(permissions);
				ws.setStatus(status);
				if (Strings.isEmpty(label))
					label = status.name();
				TranslatableString name = new TranslatableString();
				name.putTranslation("en", label, true);
				ws.setName(name);
				dao.save(ws);
				workFlow.getStates().add(ws);
				dao.save(workFlow);
				return "steps";
			}
			// edit
			if ("Edit Workflow".equalsIgnoreCase(button) && !Strings.isEmpty(name)) {
				if (workFlow == null) {
					addActionError("Error Editing Workflow, please try again.");
					return SUCCESS;
				} else {
					workFlow.setName(name);
					workFlow.setHasRequirements(hasRequirements);
					workFlowDAO.save(workFlow);
				}
			}
			if ("editStep".equalsIgnoreCase(button)) {
				if (oldStatus != null && !workFlow.isHasState(oldStatus)) {
					addActionError("Old status must be one of the workflow's states.");
					return "steps";
				}
				if (!workFlow.isHasState(newStatus)) {
					addActionError("New status must be one of the workflow's states.");
					return "steps";
				}
				WorkflowStep ws = workFlowDAO.getWorkFlowStepById(stepID);
				ws.setAuditColumns(permissions);
				ws.setOldStatus(oldStatus);
				ws.setNewStatus(newStatus);
				if (emailTemplateID > 0)
					ws.setEmailTemplate(templateDAO.find(emailTemplateID));
				else
					ws.setEmailTemplate(null);
				ws.setNoteRequired(noteRequired);
				workFlowDAO.save(ws);
				return "steps";
			}
			if ("deleteStep".equalsIgnoreCase(button)) {
				WorkflowStep ws = workFlowDAO.getWorkFlowStepById(stepID);
				if (ws == null) {
					addActionError("Could not delete step, please try again.");
					return "steps";
				} else {
					workFlowDAO.remove(ws);
				}
				return "steps";
			}
			if ("deleteStatus".equalsIgnoreCase(button)) {
				WorkflowState ws = dao.find(WorkflowState.class, statusID);
				if (ws == null) {
					addActionError("Could not delete state, please try again.");
					return "steps";
				}
				
				if (workFlow.isUsingState(ws.getStatus())) {
					addActionError("Could not delete state; it is in use.");
					return "steps";
					
				}
				workFlowDAO.remove(ws);
				
				return "steps";
			}
		}
		return SUCCESS;
	}
	
	public List<Workflow> getWorkflowList() {
		return workFlowDAO.findAll();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Workflow getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(Workflow workFlow) {
		this.workFlow = workFlow;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AuditStatus getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(String oldStatus) {
		if (oldStatus != null && !oldStatus.equals(AuditStatus.DEFAULT))
			this.oldStatus = AuditStatus.valueOf(oldStatus);
		else
			this.oldStatus = null;
	}

	public AuditStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(AuditStatus newStatus) {
		this.newStatus = newStatus;
	}

	public List<WorkflowStep> getSteps() {
		if (steps == null) {
			steps = workFlowDAO.getWorkFlowSteps(workFlow.getId());
		}

		return steps;
	}

	public int getStepID() {
		return stepID;
	}

	public void setStepID(int stepID) {
		this.stepID = stepID;
	}

	public List<EmailTemplate> getEmailTemplates() {
		return templateDAO.findAll();
	}

	public boolean isNoteRequired() {
		return noteRequired;
	}

	public void setNoteRequired(boolean noteRequired) {
		this.noteRequired = noteRequired;
	}

	public boolean isHasRequirements() {
		return hasRequirements;
	}

	public void setHasRequirements(boolean hasRequirements) {
		this.hasRequirements = hasRequirements;
	}

	public int getEmailTemplateID() {
		return emailTemplateID;
	}

	public void setEmailTemplateID(int emailTemplateID) {
		this.emailTemplateID = emailTemplateID;
	}

	public int getStatusID() {
		return statusID;
	}

	public void setStatusID(int statusID) {
		this.statusID = statusID;
	}

	public AuditStatus getStatus() {
		return status;
	}

	public void setStatus(AuditStatus status) {
		this.status = status;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}
	
	
}
