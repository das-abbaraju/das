package com.picsauditing.actions.auditType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.i18n.RequiredLanguagesSupport;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.WorkFlowDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.jpa.entities.Workflow;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.models.audits.TranslationKeysGenerator;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageAuditType extends RequiredLanguagesSupport implements Preparable {
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
	protected String ruleType;
	protected int ruleID;

	protected List<WorkflowStep> steps;

	private List<AuditType> auditTypes = null;
	
	private List<String> assigneeLabels = null;

	@Autowired
	protected AuditTypeDAO auditTypeDAO;
	@Autowired
	protected AuditCategoryDAO auditCategoryDAO;
	@Autowired
	protected AuditQuestionDAO auditQuestionDAO;
	@Autowired
	protected AuditDecisionTableDAO ruleDAO;
	@Autowired
	protected WorkFlowDAO wfDAO;
	@Autowired
	protected TranslationKeysGenerator translationKeysGenerator;

	List<? extends AuditRule> relatedRules;
	List<Workflow> workFlowList = null;

	@RequiredPermission(value = OpPerms.ManageAudits)
	public String execute() throws Exception {
		if (button != null) {
			if (button.equalsIgnoreCase("save")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);
				if (save()) {
					addActionMessage("Successfully saved");
					return Redirect.SAVE;
				}
			}
			if (button.equalsIgnoreCase("delete")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Delete);
				if (delete()) {
					addActionMessage("Successfully removed");
					return Redirect.DELETE;
				}
			}
			if (button.equalsIgnoreCase("updateAllAudits")) {
				auditTypeDAO.updateAllAudits(id);
				return Redirect.SAVE;
			}
			if (button.equalsIgnoreCase("updateAllAuditsCategories")) {
				auditTypeDAO.updateAllCategories(auditType.getId(), id);
				return Redirect.SAVE;
			}

			// Move and Copy only available for questions
			if (button.equalsIgnoreCase("Move")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);

				if (move()) {
					addActionMessage("Successfully moved.");
					return Redirect.MOVE;
				}
			}
			if (button.equalsIgnoreCase("Copy")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);

				if (copy()) {
					addActionMessage("Successfully copied.");
					return Redirect.MOVE;
				}
			}
		}

		if ("Add New".equals(button)) {
			auditType = new AuditType();
			addUserPreferredLanguage(auditType);

			return SUCCESS;
		}

		if ("AddNew".equals(button)) {
			if (category == null)
				category = new AuditCategory();

			if (question != null && category != null && category.getId() > 0) {
				question.setCategory(category);
				addUserPreferredLanguage(question);
			}

			if (auditType != null && auditType.getId() > 0)
				category.setAuditType(auditType);

			addUserPreferredLanguage(category);

			return SUCCESS;
		}

		if (auditType != null) {
			boolean renumbered = false;
			int i = 1;
			for (AuditCategory category : auditType.getCategories()) {
				if (category.getParent() == null) {
					if (i != category.getNumber()) {
						renumbered = true;
						category.setNumber(i);
					}
					i++;
				}
			}
			if (renumbered)
				addAlertMessage("The categories were not correctly numbered from 1-n, so they were auto renumbered.");
			return SUCCESS;
		}

		return "top";
	}
	
	public String findTranslations() throws IOException {
		Set<String> usedKeys = translationKeysGenerator.generateAuditTypeKeys(auditType);

		populateSessionVariablesForManageTranslationsRedirect(usedKeys);
		
		return setUrlForRedirect("ManageTranslations.action");
	}
	
	protected void populateSessionVariablesForManageTranslationsRedirect(Set<String> usedKeys) {
		Map<String, Object> session = ActionContext.getContext().getSession();
		session.put("usedI18nKeys", usedKeys);
		session.put(i18nTracing, true);
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
			if (auditType == null) {
				return false;
            }
			if (auditType.getName() == null || auditType.getName().toString().length() == 0) {
				addActionError("Audit name is required");
				return false;
			}
			if (!Strings.isEmpty(operatorID)) {
				auditType.setAccount(new Account());
				auditType.getAccount().setId(Integer.parseInt(operatorID));
			} else {
				auditType.setAccount(null);
            }

			if (editPerm != null && !editPerm.isEmpty()) {
				auditType.setEditPermission(OpPerms.valueOf(editPerm));
			} else {
				auditType.setEditPermission(null);
            }
			if ("".equals(auditType.getAssigneeLabel())) {
				auditType.setAssigneeLabel(null);
            }
			if (auditType.getAssigneeLabel() != null) {
				String key = auditType.getAssigneeLabel().trim();
				if (key.startsWith("Assignee.")) {
					addActionError("Do not start Label Key For Auditor with 'Assignee.'");
					return false;
				}
				if (Strings.isEmpty(key) || key.indexOf(" ") > 0) {
					addActionError("Invalid key for Label Key For Auditor.  It should have no spaces.");
					return false;
				}
				auditType.setAssigneeLabel(key);
			}
			if (workFlowID > 0) {
				auditType.setWorkFlow(wfDAO.find(workFlowID));
			} else {
				addActionError("You must set a workflow in order to save the Audit Type");
				return false;
			}
			if (auditType.hasMissingChildRequiredLanguages()) {
				addActionError("Changes to required languages must always have at least one language left. "
						+ "Check your hierarchy to make sure that each type, category and question has at least one language.");
				return false;
			}

			auditType.setAuditColumns(permissions);

			auditType = auditTypeDAO.save(auditType);
			id = auditType.getId();

            saveRequiredTranslationsForAuditTypeName();

            if (auditType.getAssigneeLabel() != null) {
				AppTranslation translation = null;
				try {
					translation = dao.findOne(AppTranslation.class, "t.key='Assignee." + auditType.getAssigneeLabel() + "'");
				} catch (Exception ignore) {
				}
				if (translation == null) {
					translation = new AppTranslation();
					translation.setKey("Assignee." + auditType.getAssigneeLabel());
					translation.setLocale("en");
					translation.setCreatedBy(userDAO.find(permissions.getUserId()));
					translation.setAuditColumns();
					translation.setApplicable(true);
					translation.setSourceLanguage("en");
					translation.setValue("Assignee");
					translation.setQualityRating(TranslationQualityRating.Bad);
					try {
						dao.save(translation);
					} catch (Exception ignore) {
					}
				}
			}
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

    private void saveRequiredTranslationsForAuditTypeName() throws Exception {
        // we can use the same logic for new/update -
        // save the language the user is currently using and any required languages for which there is no extant key
        List<String> localesToSave = new ArrayList<>();
        localesToSave.add(permissions.getLocale().getLanguage());

        TranslationService translationService = TranslationServiceFactory.getTranslationService();
        for (String language : auditType.getLanguages()) {
            if (!translationService.hasKeyInLocale(auditType.getI18nKey("name"), language)) {
                localesToSave.add(language);
            }
        }
        translationService.saveTranslation(auditType.getI18nKey("name"), auditType.getName(), localesToSave);
        translationService.clear();
    }

    public String workFlowSteps() {
		return "workFlowSteps";
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

	protected AuditType copyAuditType(AuditType a) {
		AuditType copy = new AuditType(a);
		copy.setAuditColumns(permissions);
		auditTypeDAO.save(copy);
		return copy;
	}

	/**
	 * Copy this audit category to this audit type
	 */
	protected AuditCategory copyAuditCategory(AuditCategory a, AuditType at) {
		AuditCategory copy = new AuditCategory(a, at);
		copy.setAuditColumns(permissions);

		if (at.getCategories() == null)
			at.setCategories(new ArrayList<AuditCategory>());
		at.getCategories().add(copy);

		auditCategoryDAO.save(copy);

		return copy;
	}

	/**
	 * Copy this audit question to this audit subcategory
	 */
	protected AuditQuestion copyAuditQuestion(AuditQuestion sourceQuestion, AuditCategory destinationCategory) {
		AuditQuestion copy = new AuditQuestion(sourceQuestion, destinationCategory);
		copy.setAuditColumns(permissions);
		auditQuestionDAO.save(copy);
		return copy;
	}

	public List<? extends AuditRule> getRelatedRules() {
		if (relatedRules == null) {
			relatedRules = ruleDAO.findByAuditType(auditType);
		}

		return relatedRules;
	}

	public List<Workflow> getWorkFlowList() {
		if (workFlowList == null) {
			workFlowList = wfDAO.findAll();
		}
		return workFlowList;
	}

	
	// GETTERS && SETTERS

	public List<String> getAssigneeLabels() {
		assigneeLabels = new ArrayList<String>();
		return assigneeLabels;
	}

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

	public String getEditPerm() {
		String result = "";
		if (auditType.getEditPermission() != null)
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

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public int getRuleID() {
		return ruleID;
	}

	public void setRuleID(int ruleID) {
		this.ruleID = ruleID;
	}

	public List<WorkflowStep> getSteps() {
		if (steps == null) {
			steps = wfDAO.getWorkFlowSteps(workFlowID);
		}
		
		return steps;
	}

	@Override
	protected void fillSelectedLocales() {
		if (auditType != null && !auditType.getLanguages().isEmpty()) {
			for (String language : auditType.getLanguages()) {
				selectedLocales.add(new Locale(language));
			}
		}
	}

	protected static class Redirect {
		static final String DELETE = "delete";
		static final String SAVE = "save";
		static final String MOVE = "move";
	}

}
