package com.picsauditing.actions.employees;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.operators.OperatorActionSupport;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class DefineCompetencies extends OperatorActionSupport {
	@Autowired
	protected OperatorCompetencyDAO operatorCompetencyDAO;

	protected OperatorCompetency competency;
	protected String category;
	protected String label;
	protected String description;
	protected String helpPage;
	protected OperatorCompetencyCourseType courseType;

	protected List<OperatorCompetency> competencies;
	protected List<String> categories;

	@RequiredPermission(value = OpPerms.DefineCompetencies)
	public String execute() throws Exception {
		if (operator == null && permissions.isOperatorCorporate()) {
			operator = operatorDao.find(permissions.getAccountId());
		}

		if (operator == null) {
			throw new RecordNotFoundException(getText(String.format("%s.error.MissingOperator")));
		}

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String load() throws Exception {
		if (competency == null) {
			competency = new OperatorCompetency();
			competency.setOperator(operator);
		}

		if (!competency.getCourses().isEmpty()) {
			courseType = competency.getCourses().get(0).getCourseType();
		}

		return "load";
	}

	@RequiredPermission(value = OpPerms.DefineCompetencies, type = OpType.Edit)
	public String save() throws Exception {
		if (competency != null) {
			if (competency.getOperator() == null) {
				competency.setOperator(operator);
			}

			competency.setAuditColumns(permissions);
			operatorCompetencyDAO.save(competency);

			if (courseType != null) {
				OperatorCompetencyCourse course = getOperatorCompetencyCourse(competency);
				course.setAuditColumns(permissions);

				if (course.getId() == 0) {
					competency.getCourses().add(course);
				}

				dao.save(course);
			}

			if (competency.isRequiresDocumentation()) {
				addToAllEmployees(competency);
			}
		} else {
			addActionError(getText("DefineCompetencies.NoCompetencySelected"));
		}

		return SUCCESS;
	}

	private OperatorCompetencyCourse getOperatorCompetencyCourse(OperatorCompetency competency) {
		OperatorCompetencyCourse course = new OperatorCompetencyCourse();
		course.setCompetency(competency);
		course.setCourseType(courseType);

		if (competency != null) {
			for (OperatorCompetencyCourse competencyCourse : competency.getCourses()) {
				if (competencyCourse.getCourseType() == courseType) {
					course = competencyCourse;
				}
			}
		}

		return course;
	}

	// TODO: Replace this with an employee cron?
	private void addToAllEmployees(OperatorCompetency competencyRequiringDocumentation) {
		List<EmployeeSite> employeesAtSite = dao.findWhere(EmployeeSite.class, "t.operator.id = " + operator.getId());
		if (employeesAtSite != null) {
			Set<Employee> employees = new HashSet<>();
			for (EmployeeSite employeeSite : employeesAtSite) {
				employees.add(employeeSite.getEmployee());
			}

			for (Employee employee : employees) {
				EmployeeCompetency employeeCompetency = new EmployeeCompetency();
				employeeCompetency.setEmployee(employee);
				employeeCompetency.setCompetency(competencyRequiringDocumentation);
				employeeCompetency.setAuditColumns(permissions);
				dao.save(employeeCompetency);
			}
		}
	}

	public List<OperatorCompetency> getCompetencies() {
		if (competencies == null) {
			competencies = operatorCompetencyDAO.findByOperator(operator.getId());
		}

		return competencies;
	}

	public List<String> getCategories() {
		if (categories == null) {
			categories = operatorCompetencyDAO.findDistinctCategories();
		}

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

	public OperatorCompetencyCourseType getCourseType() {
		return courseType;
	}

	public void setCourseType(OperatorCompetencyCourseType courseType) {
		this.courseType = courseType;
	}
}
