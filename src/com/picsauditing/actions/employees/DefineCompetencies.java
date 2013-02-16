package com.picsauditing.actions.employees;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.operators.OperatorActionSupport;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.report.RecordNotFoundException;

@SuppressWarnings("serial")
public class DefineCompetencies extends OperatorActionSupport {
	@Autowired
	protected OperatorCompetencyDAO operatorCompetencyDAO;

	protected OperatorCompetency competency;
	protected String category;
	protected String label;
	protected String description;
	protected String helpPage;

	protected List<OperatorCompetency> competencies;
	protected List<String> categories;

	@RequiredPermission(value=OpPerms.DefineCompetencies)
	public String execute() throws Exception {
		if (operator == null && permissions.isOperatorCorporate())
			operator = operatorDao.find(permissions.getAccountId());
		
		if (operator == null)
			throw new RecordNotFoundException(getText(String.format("%s.error.MissingOperator")));

		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String load() throws Exception {
		if (competency != null) {
			json = new JSONObject() {
				{
					put("result", "success");
					put("competency", competency.toJSON());
				}
			};
		} else {
			json = new JSONObject() {
				{
					put("result", "failure");
					put("gritter", new JSONObject() {
						{
							put("title", "Competency Not Loaded!");
							put("text", "There was no competency to load.");
						}
					});
				}
			};
		}

		return JSON;
	}
	
	@SuppressWarnings("unchecked")
	@RequiredPermission(value=OpPerms.DefineCompetencies, type=OpType.Edit)
	public String save() throws Exception {
		if (competency != null) {
			try {
				if(competency.getOperator() == null)
					competency.setOperator(operator);
				operatorCompetencyDAO.save(competency);

				json = new JSONObject() {
					{
						put("result", "success");
						put("gritter", new JSONObject() {
							{
								put("title", "Competency Saved");
								put("text", "Competency " + competency.getLabel() + " saved successfully.");
							}
						});
						put("competency", competency.toJSON());
					}
				};
			} catch (final Exception e) {
				json = new JSONObject() {
					{
						put("result", "failure");
						put("gritter", new JSONObject() {
							{
								put("title", "Competency Not Saved!");
								put("text", "Competency " + competency.getLabel() + " not saved. " + e.getMessage());
							}
						});
						put("competency", competency.toJSON());
					}
				};

				return JSON;
			}
		} else {
			json = new JSONObject() {
				{
					put("result", "failure");
					put("gritter", new JSONObject() {
						{
							put("title", "Competency Not Saved!");
							put("text", "There was no competency to save.");
						}
					});
				}
			};
		}

		return JSON;
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

	public OperatorCompetency getCompetency() {
		return competency;
	}

	public void setCompetency(OperatorCompetency competency) {
		this.competency = competency;
	}
}
