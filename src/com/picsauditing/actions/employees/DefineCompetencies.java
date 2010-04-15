package com.picsauditing.actions.employees;

import java.util.List;

import org.jboss.util.Strings;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.operators.OperatorActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.OperatorCompetency;

@SuppressWarnings("serial")
public class DefineCompetencies extends OperatorActionSupport {
	protected OperatorCompetencyDAO operatorCompetencyDAO;

	protected String category;
	protected String label;
	protected String description;
	protected String helpPage;
	protected Integer competencyID;

	protected List<OperatorCompetency> competencies;
	protected List<String> categories;

	public DefineCompetencies(OperatorAccountDAO operatorDao, OperatorCompetencyDAO operatorCompetencyDAO) {
		super(operatorDao);
		this.operatorCompetencyDAO = operatorCompetencyDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findOperator();
		tryPermissions(OpPerms.DefineCompetencies);

		if ("Save".equalsIgnoreCase(button)) {
			if (Strings.isEmpty(category) || Strings.isEmpty(label)) {
				addActionError("Please enter a category and label to Save");
				return SUCCESS;
			}

			OperatorCompetency operatorCompetency = new OperatorCompetency();
			operatorCompetency.setAuditColumns(permissions);
			operatorCompetency.setOperator(operator);
			operatorCompetency.setCategory(category);
			operatorCompetency.setLabel(label);
			operatorCompetency.setDescription(description);
			operatorCompetency.setHelpPage(helpPage);

			operatorCompetencyDAO.save(operatorCompetency);

			addActionMessage("Successfully added " + operatorCompetency.getLabel() + " to competencies.");
			category = label = description = helpPage = "";

			return SUCCESS;
		}

		if ("Remove".equalsIgnoreCase(button)) {
			OperatorCompetency removed = operatorCompetencyDAO.find(competencyID);
			operatorCompetencyDAO.remove(removed);

			addActionMessage("Successfully removed " + removed.getLabel() + " from competencies");

			return SUCCESS;
		}

		return SUCCESS;
	}

	public List<OperatorCompetency> getCompetencies() {
		if (competencies == null)
			competencies = operatorCompetencyDAO.findByOperator(operator.getId());
		return competencies;
	}

	public List<String> getCategories() {
		if (categories == null)
			categories = operatorCompetencyDAO.findDistinctCategories();
		return categories;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHelpPage() {
		return helpPage;
	}

	public void setHelpPage(String helpPage) {
		this.helpPage = helpPage;
	}

	public Integer getCompetencyID() {
		return competencyID;
	}

	public void setCompetencyID(Integer competencyID) {
		this.competencyID = competencyID;
	}
}
