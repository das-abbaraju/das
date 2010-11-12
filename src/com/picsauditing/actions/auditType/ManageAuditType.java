package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.WorkFlowDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionOption;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.Workflow;
import com.picsauditing.util.AuditTypeCache;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageAuditType extends PicsActionSupport implements Preparable {

	protected int id = 0;
	protected AuditType auditType = null;
	protected AuditCategory category = null;
	protected AuditQuestion question = null;
	protected String operatorID;
	protected int originalID = 0;
	protected int targetID = 0;
	protected int workFlowID = 0;
	protected Integer emailTemplateID;
	protected String editPerm;

	private List<AuditType> auditTypes = null;
	
	protected AuditTypeDAO auditTypeDAO;
	protected EmailTemplateDAO emailTemplateDAO;
	protected AuditCategoryDAO auditCategoryDAO;
	protected AuditQuestionDAO auditQuestionDAO;
	protected AuditDecisionTableDAO ruleDAO;
	protected WorkFlowDAO wfDAO;

	List<? extends AuditRule> relatedRules;
	List<Workflow> workFlowList = null;

	public ManageAuditType(EmailTemplateDAO emailTemplateDAO, AuditTypeDAO auditTypeDAO,
			AuditCategoryDAO auditCategoryDAO, AuditQuestionDAO auditQuestionDAO, AuditDecisionTableDAO ruleDAO, WorkFlowDAO wfDAO) {
		this.emailTemplateDAO = emailTemplateDAO;
		this.auditTypeDAO = auditTypeDAO;
		this.auditCategoryDAO = auditCategoryDAO;
		this.auditQuestionDAO = auditQuestionDAO;
		this.ruleDAO = ruleDAO;
		this.wfDAO = wfDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.ManageAudits);

		if (button != null) {
			if (button.equalsIgnoreCase("save")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);
				if (save()) {
					addActionMessage("Successfully saved"); // default message
					new AuditTypeCache();
					this.redirect(getRedirectURL());
					return BLANK;
				}
			}
			if (button.equalsIgnoreCase("delete")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Delete);
				if (delete()) {
					addActionMessage("Successfully removed"); // default message
					new AuditTypeCache();
					
					this.redirect(getDeletedRedirectURL());
					
					return BLANK;
				}
			}
			if (button.equalsIgnoreCase("updateAllAudits")) {
				auditTypeDAO.updateAllAudits(id);
				this.redirect(getRedirectURL());
				return BLANK;
			}
			if (button.equalsIgnoreCase("updateAllAuditsCategories")) {
				auditTypeDAO.updateAllCategories(auditType.getId(), id);
				this.redirect(getRedirectURL());
				return BLANK;
			}
			
			// Move and Copy only available for questions
			if (button.equalsIgnoreCase("Move")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);

				if (move()) {
					addActionMessage("Successfully moved.");
					new AuditTypeCache();
					return redirect(getCopyMoveURL());
				}
			}
			if (button.equalsIgnoreCase("Copy")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);
				
				if (copy()) {
					addActionMessage("Successfully copied.");
					new AuditTypeCache();
					return redirect(getCopyMoveURL());
				}
			}
		}

		if ("Add New".equals(button)) {
			auditType = new AuditType();
			return SUCCESS;
		}

		if ("AddNew".equals(button)) {
			category = new AuditCategory();

			if (auditType != null && auditType.getId() > 0)
				category.setAuditType(auditType);

			return SUCCESS;
		}

		if (auditType != null) {
			return SUCCESS;
		}

		return "top";
	}

	protected void load(int id) {
		if (id != 0) {
			load(auditTypeDAO.find(id));
		}
	}

	protected void loadParent(int id) {
		// do nothing
	}

	protected void load(AuditType newType) {
		this.auditType = newType;
	}

	@Override
	public void prepare() throws Exception {

		String[] ids = (String[]) ActionContext.getContext().getParameters().get("id");

		String[] parentIds = (String[]) ActionContext.getContext().getParameters().get("parentID");

		if (ids != null && ids.length > 0) {
			int thisId = Integer.parseInt(ids[0]);
			if (thisId > 0) {
				load(thisId);
				return; // don't try to load the parent too
			}
		}

		if (parentIds != null && parentIds.length > 0) {
			int thisId = Integer.parseInt(parentIds[0]);
			loadParent(thisId);
		}
	}

	public boolean save() {
		try {
			if (auditType == null)
				return false;
			if (auditType.getAuditName() == null || auditType.getAuditName().length() == 0) {
				addActionError("Audit name is required");
				return false;
			}
			if (!Strings.isEmpty(operatorID)) {
				auditType.setAccount(new Account());
				auditType.getAccount().setId(Integer.parseInt(operatorID));
			} else
				auditType.setAccount(null);

			if (emailTemplateID == null) {
				auditType.setTemplate(null);
			} else if (auditType.getTemplate() == null || auditType.getTemplate().getId() != emailTemplateID) {
				auditType.setTemplate(emailTemplateDAO.find(emailTemplateID));
			}

			if(editPerm!=null && !editPerm.isEmpty()){
				auditType.setEditPermission(OpPerms.valueOf(editPerm));
			} else auditType.setEditPermission(null);
			if(workFlowID>0){
				auditType.setWorkFlow(wfDAO.find(workFlowID));
			} else {
				addActionError("You must set a workflow in order to save the Audit Type");
				return false;
			}
			auditType.setAuditColumns(permissions);
			auditType = auditTypeDAO.save(auditType);
			id = auditType.getId();
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	protected boolean delete() {
		try {
			if (auditType.getCategories().size() > 0) {
				addActionError("Can't delete - Categories still exist");
				return false;
			}

			auditTypeDAO.remove(auditType.getId());
			id = auditType.getId();
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	protected boolean move() {
		return false;
	}
	
	protected boolean copy() {
		return false;
	}

	@Transactional
	protected int copyAllRecursive() {
		// Copying Audit
		AuditType auditTypeCopy = copyAuditType(auditType);

		AuditType originalAudit = auditTypeDAO.find(originalID);
		List<AuditCategory> categories = auditCategoryDAO.findByAuditTypeID(originalAudit.getId());

		// Copying Categories
		for (AuditCategory category : categories) {
			AuditCategory categoryCopy = copyAuditCategory(category, auditTypeCopy);

			// Copying Questions
			for (AuditQuestion question : categoryCopy.getQuestions())
				copyAuditQuestion(question, categoryCopy);
		}

		auditTypeDAO.save(auditTypeCopy);

		return auditTypeCopy.getId();
	}

	@Transactional
	protected AuditType copyAuditType(AuditType a) {
		AuditType copy = new AuditType(a);
		copy.setAuditColumns(permissions);
		auditTypeDAO.save(copy);
		return copy;
	}

	// Copy this audit category to this audit type
	@Transactional
	protected AuditCategory copyAuditCategory(AuditCategory a, AuditType at) {
		AuditCategory copy = new AuditCategory(a, at);
		copy.setAuditColumns(permissions);

		if (at.getCategories() == null)
			at.setCategories(new ArrayList<AuditCategory>());
		at.getCategories().add(copy);

		auditCategoryDAO.save(copy);

		return copy;
	}

	// Copy this audit question to this audit subcategory
	@Transactional
	protected AuditQuestion copyAuditQuestion(AuditQuestion a, AuditCategory asc) {
		AuditQuestion copy = new AuditQuestion(a, asc);
		copy.setAuditColumns(permissions);
		auditQuestionDAO.save(copy);

		/*
		 * for (AuditQuestionText text : a.getQuestionTexts()) {
		 * AuditQuestionText aqtCopy = new AuditQuestionText(text, copy);
		 * aqtCopy.setAuditColumns(permissions);
		 * 
		 * copy.getQuestionTexts().add(aqtCopy);
		 * auditQuestionTextDAO.save(aqtCopy); }
		 */

		if (a.getOptions() != null && copy.getOptions() == null)
			copy.setOptions(new ArrayList<AuditQuestionOption>());
		for (AuditQuestionOption questionOption : a.getOptions()) {
			AuditQuestionOption aqoCopy = new AuditQuestionOption(questionOption, copy);
			aqoCopy.setAuditColumns(permissions);

			copy.getOptions().add(questionOption);
			auditQuestionDAO.save(aqoCopy);
		}

		auditQuestionDAO.save(copy);

		return copy;
	}

	protected String getRedirectURL() {
		return "ManageAuditType.action?id=" + id;
	}
	
	protected String getCopyMoveURL() {
		return "ManageAuditType.action?id=" + id;
	}

	protected String getDeletedRedirectURL() {
		return "ManageAuditType.action";
	}

	public List<? extends AuditRule> getRelatedRules() {
		if (relatedRules == null) {
			relatedRules = ruleDAO.findByAuditType(auditType);
		}

		return relatedRules;
	}
	
	public List<Workflow> getWorkFlowList(){
		if(workFlowList==null){
			workFlowList = wfDAO.findAll();
		}
		return workFlowList;
	}
	
	// GETTERS && SETTERS

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<AuditType> getAuditTypes() {
		if (auditTypes == null) {
			auditTypes = auditTypeDAO.findAll();
		}
		return auditTypes;
	}

	public void setAuditTypes(List<AuditType> auditTypes) {
		this.auditTypes = auditTypes;
	}

	public AuditType getAuditType() {
		return auditType;
	}

	public AuditTypeClass[] getClassList() {
		return AuditTypeClass.values();
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory category) {
		this.category = category;
	}

	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	public void setParentID(int parentID) {
		// Do nothing here...we use this in the prepare statement
	}

	public String getOperatorID() {
		return operatorID;
	}

	public void setOperatorID(String operatorID) {
		this.operatorID = operatorID;
	}

	public int getOriginalID() {
		return originalID;
	}

	public void setOriginalID(int originalID) {
		this.originalID = originalID;
	}

	public int getTargetID() {
		return targetID;
	}

	public void setTargetID(int targetID) {
		this.targetID = targetID;
	}

	public Integer getEmailTemplateID() {
		return emailTemplateID;
	}

	public void setEmailTemplateID(Integer emailTemplateID) {
		this.emailTemplateID = emailTemplateID;
	}

	public List<EmailTemplate> getTemplateList() {
		return emailTemplateDAO.findAll();
	}

	public String getEditPerm() {
		String result = "";
		if(auditType.getEditPermission()!=null)
			result = auditType.getEditPermission().name();
		return result;
	}

	public void setEditPerm(String editPerm) {
		this.editPerm = editPerm;
	}

	public int getWorkFlowID() {
		return workFlowID;
	}

	public void setWorkFlowID(int workFlowID) {
		this.workFlowID = workFlowID;
	}
}
