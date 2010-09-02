package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.WorkFlowDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.Workflow;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageWorkFlow extends PicsActionSupport {
	
	protected String name;
	protected Workflow workFlow;
	protected int id;
	protected int stepID;
	protected int emailTemplateID;
	
	protected AuditStatus oldStatus;
	protected AuditStatus newStatus;
	protected boolean noteRequired;

	protected WorkFlowDAO workFlowDAO;
	protected EmailTemplateDAO templateDAO;
	protected List<WorkflowStep> steps;
	
	public ManageWorkFlow(WorkFlowDAO workFlowDAO, EmailTemplateDAO templateDAO){
		this.workFlowDAO = workFlowDAO;
		this.templateDAO = templateDAO;
	}
	
	@Override
	public String execute() throws Exception {
		if(!forceLogin())
			return LOGIN;
		
		tryPermissions(OpPerms.ManageAuditWorkFlow);
		if(id>0){
			workFlow = workFlowDAO.find(id);
		}
		
		if (button!=null) {
			tryPermissions(OpPerms.ManageAuditWorkFlow, OpType.Edit);
			// Get workflow steps
			if ("getSteps".equals(button) && workFlow != null)
				return SUCCESS;
			
			//create
			if ("create".equalsIgnoreCase(button)) {
				workFlow = new Workflow();
				workFlow.setAuditColumns(permissions);
				if(Strings.isEmpty(name)){
					addActionError("Invalid Name");
					return SUCCESS;
				}
				workFlow.setName(name);
				workFlowDAO.save(workFlow);
			}
			//add
			if ("add".equalsIgnoreCase(button) && workFlow!=null) {
				if(newStatus==null){
					addActionError("No new status");
					return SUCCESS;
				}
				WorkflowStep ws = new WorkflowStep();
				ws.setWorkflow(workFlow);
				ws.setAuditColumns(permissions);
				ws.setOldStatus(oldStatus);
				ws.setNewStatus(newStatus);
				if(emailTemplateID>0)
					ws.setEmailTemplate(templateDAO.find(emailTemplateID));
				else ws.setEmailTemplate(null);
				ws.setNoteRequired(noteRequired);
				workFlowDAO.save(ws);
			}
			//edit
			if ("editWorkFlow".equalsIgnoreCase(button) && !Strings.isEmpty(name)) {
				workFlow.setName(name);
				workFlowDAO.save(workFlow);
			}
			if ("editStep".equalsIgnoreCase(button)) {
				WorkflowStep ws = workFlowDAO.getWorkFlowStepById(stepID);
				ws.setAuditColumns(permissions);
				ws.setOldStatus(oldStatus);
				ws.setNewStatus(newStatus);
				if(emailTemplateID>0)
					ws.setEmailTemplate(templateDAO.find(emailTemplateID));
				else ws.setEmailTemplate(null);
				ws.setNoteRequired(noteRequired);
				workFlowDAO.save(ws);
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
		if(oldStatus!=null && !oldStatus.equals(AuditStatus.DEFAULT))
			this.oldStatus = AuditStatus.valueOf(oldStatus);
		else this.oldStatus = null;
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
/*			
			List<WorkflowStep> result = new ArrayList<WorkflowStep>();
			
			WorkflowStep current = null;
			while (steps.size() > 0) {
				Iterator<WorkflowStep> i = steps.iterator();
			
				while (i.hasNext()) {
					WorkflowStep s = i.next();
					if (s.getOldStatus() == null || (current != null && s.getOldStatus().equals(current.getNewStatus()))) {
						current = s;
						result.add(s);
						i.remove();
					}
					
					
				}
			}
			
			steps = result;*/
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
	
	public int getEmailTemplateID() {
		return emailTemplateID;
	}
	
	public void setEmailTemplateID(int emailTemplateID) {
		this.emailTemplateID = emailTemplateID;
	}
}
