package com.picsauditing.actions.employees;

import java.util.List;

import org.jboss.util.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.operators.OperatorActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.OperatorCompetency;

@SuppressWarnings("serial")
public class DefineCompetencies extends OperatorActionSupport implements Preparable {
	protected OperatorCompetencyDAO operatorCompetencyDAO;
	protected OperatorAccountDAO operatorDao;

	protected String category;
	protected String label;
	protected String description;
	protected String helpPage;
	protected Integer competencyID;

	protected List<OperatorCompetency> competencies;
	protected List<String> categories;

	protected OperatorCompetency competency;

	protected JSONArray dtable = new JSONArray();

	@Override
	public void prepare() throws Exception {
		int cID = getParameter("competency.id");
		if (cID > 0)
			competency = operatorCompetencyDAO.find(cID);
	}

	public DefineCompetencies(OperatorAccountDAO operatorDao, OperatorCompetencyDAO operatorCompetencyDAO) {
		super(operatorDao);
		this.operatorCompetencyDAO = operatorCompetencyDAO;
		this.operatorDao = operatorDao;
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findOperator();
		tryPermissions(OpPerms.DefineCompetencies);

		if ("Save".equals(button)) {
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

		if ("Remove".equals(button)) {
			OperatorCompetency removed = operatorCompetencyDAO.find(competencyID);
			operatorCompetencyDAO.remove(removed);

			addActionMessage("Successfully removed " + removed.getLabel() + " from competencies");

			return SUCCESS;
		}

		if ("load".equals(button)) {
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

		if ("save".equals(button)) {
			if (competency != null) {
				try {
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

		if ("delete".equals(button)) {
			if (competency != null) {
				final int compID = competency.getId();

				operatorCompetencyDAO.remove(competency);
				json = new JSONObject() {
					{
						put("result", "success");
						put("gritter", new JSONObject() {
							{
								put("title", "Competency Deleted");
								put("text", "Competency " + competency.getLabel() + " removed successfully.");
							}
						});
						put("id", compID);
					}
				};
			} else {
				json = new JSONObject() {
					{
						put("result", "failure");
						put("gritter", new JSONObject() {
							{
								put("title", "Competency Not Deleted!");
								put("text", "Competency does not exist");
							}
						});
					}
				};
			}

			return JSON;
		}

		for (OperatorCompetency competency : getCompetencies()) {
			dtable.add(competency.toTableJSON());
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

	public OperatorCompetency getCompetency() {
		return competency;
	}

	public void setCompetency(OperatorCompetency competency) {
		this.competency = competency;
	}

	public JSONArray getDtable() {
		return dtable;
	}

	public void setDtable(JSONArray dtable) {
		this.dtable = dtable;
	}
}
