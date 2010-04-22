package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;

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
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class EmployeeDetail extends AccountActionSupport implements Preparable {
	private EmployeeDAO employeeDAO;
	protected EmployeeCompetencyDAO ecDAO;
	protected EmployeeRoleDAO erDAO;
	protected JobCompetencyDAO jcDAO;
	protected OperatorCompetencyDAO competencyDAO;
	
	protected Employee employee;
	protected TreeSet<OperatorCompetency> opComps;
	protected List<EmployeeCompetency> competencies;
	protected DoubleMap<OperatorCompetency, JobRole, Boolean> map = new DoubleMap<OperatorCompetency, JobRole, Boolean>();

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
	
	public TreeSet<OperatorCompetency> getOpComps() {
		if (opComps == null) {
			List<Integer> jobRoleIDs = new ArrayList<Integer>();
			opComps = new TreeSet<OperatorCompetency>();
			
			for (EmployeeRole er : employee.getEmployeeRoles()) {
				jobRoleIDs.add(er.getJobRole().getId());
			}
			
			List<JobCompetency> jcs = competencyDAO.findByJobRoles(jobRoleIDs);
			
			for (JobCompetency jc : jcs) {
				opComps.add(jc.getCompetency());
				map.put(jc.getCompetency(), jc.getJobRole(), false);
			}
			
			for (EmployeeCompetency ec : getCompetencies()) {
				for (JobCompetency jc : jcs) {
					if (jc.getCompetency().equals(ec.getCompetency())) {
						map.put(jc.getCompetency(), jc.getJobRole(), ec.isSkilled());
					}
				}
			}
		}
		
		return opComps;
	}
	
	public List<EmployeeCompetency> getCompetencies() {
		if (competencies == null)
			competencies = ecDAO.findByEmployee(employee.getId());
		
		return competencies;
	}
	
	public Boolean getCompetenciesByRole(OperatorCompetency opComp, JobRole jobRole) {
		return map.get(opComp, jobRole);
	}
}
