package com.picsauditing.actions.report;

import java.util.List;

import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;

@SuppressWarnings("serial")
public class ConfigChanges extends PicsActionSupport {

	@Autowired
	private AppPropertyDAO apppropertyDAO;
	@Autowired
	private FlagCriteriaDAO flagCriteriaDAO;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private AuditCategoryDAO auditCategoryDAO;
	@Autowired
	private AuditQuestionDAO auditQuestionDAO;
	@Autowired
	private FlagCriteriaOperatorDAO flagCriteriaOperatorDAO;

	private String lastRelease;

	@Override
	public String execute() throws Exception {
		lastRelease = apppropertyDAO.getProperty("ConfigUpdateDate");
		return SUCCESS;
	}

	public String getLastRelease() {
		return lastRelease;
	}

	public List<FlagCriteria> getCriteriaList() {
		return flagCriteriaDAO.findWhere("updateDate > '" + lastRelease + "'");
	}
	
	public List<FlagCriteria> getFlagCriteriaOperatorList() {
		return flagCriteriaOperatorDAO.findWhere("updateDate > '" + lastRelease + "'");
	}

	public List<AuditType> getAuditTypes() {
		return auditTypeDAO.findWhere("updateDate > '" + lastRelease + "'");
	}

	public List<AuditCategory> getAuditCategories() {
		return auditCategoryDAO.findWhere("updateDate > '" + lastRelease + "'");
	}

	public List<Workflow> getWorkFlows() {
		return (List<Workflow>) dao.findWhere(Workflow.class, "updateDate > '"
				+ lastRelease + "'", 0, "updateDate");

	}

	public List<WorkflowStep> getWorkFlowSteps() {
		return (List<WorkflowStep>) dao.findWhere(WorkflowStep.class,
				"updateDate > '" + lastRelease + "'", 0, "updateDate");
	}

	public List<AuditQuestion> getQuestions() {
		return auditQuestionDAO.findWhere("updateDate > '" + lastRelease + "'");
	}

	public List<AppTranslation> getTranslations() {
		return (List<AppTranslation>) dao.findWhere(AppTranslation.class,
				"updateDate > '" + lastRelease + "' AND updatedBy != 1", 0,
				"updateDate");
	}

}
