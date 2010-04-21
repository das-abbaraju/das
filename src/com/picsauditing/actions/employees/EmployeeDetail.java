package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.EmployeeCompetencyDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeRoleDAO;
import com.picsauditing.dao.JobCompetencyDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.OperatorCompetency;

@SuppressWarnings("serial")
public class EmployeeDetail extends AccountActionSupport implements Preparable {
	private EmployeeDAO employeeDAO;
	protected EmployeeCompetencyDAO ecDAO;
	protected EmployeeRoleDAO erDAO;
	protected JobCompetencyDAO jcDAO;
	protected OperatorCompetencyDAO competencyDAO;
	
	protected Employee employee;
	protected List<OperatorCompetency> opComps;
	protected List<EmployeeCompetency> competencies;

	public EmployeeDetail(EmployeeDAO employeeDAO, EmployeeCompetencyDAO ecDAO, EmployeeRoleDAO erDAO,
			JobCompetencyDAO jcDAO, OperatorCompetencyDAO competencyDAO) {
		this.employeeDAO = employeeDAO;
		this.ecDAO = ecDAO;
		this.erDAO = erDAO;
		this.jcDAO = jcDAO;
		this.competencyDAO = competencyDAO;
	}

	@Override
	public void prepare() throws Exception {
		int employeeID = getParameter("employee.id");
		if (employeeID > 0) {
			employee = employeeDAO.find(employeeID);
		}
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (employee == null || employee.getId() == 0)
			throw new RecordNotFoundException("Missing employee id");

		return SUCCESS;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public int getAge() {
		Calendar now = Calendar.getInstance();
		Calendar dob = Calendar.getInstance();
		
		dob.setTime(employee.getBirthDate());
		
		// terrible approximation
		int age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
		if (now.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR))
			age--;
		
		return age;
	}
	
	public List<OperatorCompetency> getOpComps() {
		if (opComps == null) {
			opComps = new ArrayList<OperatorCompetency>();
			List<EmployeeCompetency> allEC = ecDAO.findByEmployee(employee.getId());
			
			for (EmployeeCompetency ec : allEC) {
				if (!opComps.contains(ec.getCompetency()))
					opComps.add(ec.getCompetency());
			}
		}
		
		return opComps;
	}
	
	public List<EmployeeCompetency> getCompetencies() {
		if (competencies == null)
			competencies = ecDAO.findByEmployee(employee.getId());
		
		return competencies;
	}
	
	public List<EmployeeCompetency> getCompetenciesByRole(int jobRoleID) {
		return ecDAO.findByJobRole(jobRoleID, employee.getId());
	}
}
