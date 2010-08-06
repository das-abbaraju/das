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
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditQuestionTextDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionOption;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.util.AuditTypeCache;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageAuditType extends PicsActionSupport implements Preparable {

	protected int id = 0;
	protected AuditType auditType = null;
	protected AuditCategory category = null;
	protected AuditCategory subCategory = null;
	protected AuditQuestion question = null;
	protected String operatorID;
	protected int originalID = 0;
	protected int targetID = 0;
	protected Integer emailTemplateID;

	private List<AuditType> auditTypes = null;

	protected AuditTypeDAO auditTypeDAO;
	protected EmailTemplateDAO emailTemplateDAO;
	protected AuditCategoryDAO auditCategoryDAO;
	protected AuditQuestionDAO auditQuestionDAO;
	protected AuditQuestionTextDAO auditQuestionTextDAO;

	public ManageAuditType(EmailTemplateDAO emailTemplateDAO,
			AuditTypeDAO auditTypeDAO, AuditCategoryDAO auditCategoryDAO,
			AuditQuestionDAO auditQuestionDAO,
			AuditQuestionTextDAO auditQuestionTextDAO) {
		this.emailTemplateDAO = emailTemplateDAO;
		this.auditTypeDAO = auditTypeDAO;
		this.auditCategoryDAO = auditCategoryDAO;
		this.auditQuestionDAO = auditQuestionDAO;
		this.auditQuestionTextDAO = auditQuestionTextDAO;
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
					return "saved";
				}
			}
			if (button.equalsIgnoreCase("delete")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Delete);
				if (delete()) {
					addActionMessage("Successfully removed"); // default message
					new AuditTypeCache();
					return "deleted";
				}
			}
			if (button.equalsIgnoreCase("updateAllAudits")) {
				auditTypeDAO.updateAllAudits(id);
				return "saved";
			}
			if (button.equalsIgnoreCase("updateAllAuditsCategories")) {
				auditTypeDAO.updateAllCategories(auditType.getId(), id);
				return "saved";
			}
			if (button.equalsIgnoreCase("Copy")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);
				if (copy()) {
					addActionMessage("Successfully copied"); // default message
					new AuditTypeCache();
					return "copied";
				}
			}
			if (button.equalsIgnoreCase("CopyAll")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);
				if (copyAll()) {
					addActionMessage("Successfully copied"); // default message
					new AuditTypeCache();
					return "copied";
				}
			}
			if (button.equalsIgnoreCase("Move")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);
				if (move()) {
					addActionMessage("Successfully moved"); // default message
					new AuditTypeCache();
					return "moved";
				}
			}
		}

		if ("Add New".equals(button)) {
			auditType = new AuditType();
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

		String[] ids = (String[]) ActionContext.getContext().getParameters()
				.get("id");

		String[] parentIds = (String[]) ActionContext.getContext()
				.getParameters().get("parentID");

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
			if (auditType.getAuditName() == null
					|| auditType.getAuditName().length() == 0) {
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
			} else if (auditType.getTemplate() == null
					|| auditType.getTemplate().getId() != emailTemplateID) {
				auditType.setTemplate(emailTemplateDAO.find(emailTemplateID));
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
	
	protected boolean copy() {
		try {
			if (auditTypeDAO.findWhere(
					"auditName LIKE '" + auditType.getAuditName() + "'")
					.size() > 0) {
				addActionMessage("The Audit Name is not Unique");
				return false;
			}

			AuditType a = copyAuditType(auditType);

			addActionMessage("Copied the Audit Type only. <a href=\"ManageAuditType.action?id="
					+ a.getId() + "\">Go to this Audit?</a>");
			return true;

		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}
	
	protected boolean copyAll() {
		try {
			if (auditTypeDAO.findWhere(
					"auditName LIKE '" + auditType.getAuditName() + "'")
					.size() > 0) {
				addActionMessage("The Audit Name is not Unique");
				return false;
			}

			int id = copyAllRecursive(); // Wanting a transactional method here
			// for batching

			addActionMessage("Copied Audit Type, and all related Categories, Subcategories and Questions. <a href=\"ManageAuditType.action?id="
					+ id + "\">Go to this Audit?</a>");
			return true;

		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}
	
	protected boolean move() {
		return false;
	}

	@Transactional
	protected int copyAllRecursive() {
		// Copying Audit
		AuditType auditTypeCopy = copyAuditType(auditType);

		AuditType originalAudit = auditTypeDAO.find(originalID);
		List<AuditCategory> categories = auditCategoryDAO
				.findByAuditTypeID(originalAudit.getId());

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

		/*for (AuditQuestionText text : a.getQuestionTexts()) {
			AuditQuestionText aqtCopy = new AuditQuestionText(text, copy);
			aqtCopy.setAuditColumns(permissions);

			copy.getQuestionTexts().add(aqtCopy);
			auditQuestionTextDAO.save(aqtCopy);
		}*/

		if (a.getOptions() != null && copy.getOptions() == null)
			copy.setOptions(new ArrayList<AuditQuestionOption>());
		for (AuditQuestionOption questionOption : a.getOptions()) {
			AuditQuestionOption aqoCopy = new AuditQuestionOption(
					questionOption, copy);
			aqoCopy.setAuditColumns(permissions);

			copy.getOptions().add(questionOption);
			auditQuestionDAO.save(aqoCopy);
		}
		
		auditQuestionDAO.save(copy);

		return copy;
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
}
