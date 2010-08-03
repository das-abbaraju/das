package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmployeeCompetencyDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeRoleDAO;
import com.picsauditing.dao.JobCompetencyDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.EmployeeQualification;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.JobTaskCriteria;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.SpringUtils;

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
	protected List<EmployeeQualification> tasks;
	protected Map<EmployeeQualification, List<AssessmentResult>> qualification;
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
	
	public boolean canViewContractor(int conID) {
		ContractorOperatorDAO coDAO = (ContractorOperatorDAO) SpringUtils.getBean("ContractorOperatorDAO");
		ContractorOperator co = coDAO.find(conID, permissions.getAccountId());
		
		if (co != null)
			return true;
		
		return false;
	}
	
	public List<EmployeeCompetency> getCompetencies() {
		if (competencies == null)
			competencies = ecDAO.findByEmployee(employee.getId());
		
		return competencies;
	}
	
	public Boolean getCompetenciesByRole(OperatorCompetency opComp, JobRole jobRole) {
		return map.get(opComp, jobRole);
	}
	
	public List<EmployeeQualification> getJobTasks() {
		if (tasks == null) {
			tasks = new ArrayList<EmployeeQualification>(employee.getEmployeeQualifications());
			
			Iterator<EmployeeQualification> iterator = tasks.iterator();
			while (iterator.hasNext()) {
				EmployeeQualification e = iterator.next();
				
				if (e.getEffectiveDate() != null && e.getExpirationDate() != null && 
						(e.getEffectiveDate().after(new Date()) || e.getExpirationDate().before(new Date()))) {
					iterator.remove();
				}
			}
			
			Collections.sort(tasks, new SortTaskByLabel());
		}
		
		return tasks;
	}
	
	public Map<EmployeeQualification, List<AssessmentResult>> getQualification() {
		if (qualification == null) {
			qualification = new HashMap<EmployeeQualification, List<AssessmentResult>>();
			tasks = getJobTasks();

			Set<AssessmentTest> taken = new HashSet<AssessmentTest>();
			List<AssessmentResult> results = employee.getAssessmentResults();
			for (AssessmentResult result : results) {
				if (result.isCurrent())
					taken.add(result.getAssessmentTest());
			}
			
			// Get job task criteria, get tests (required), look at assessment results
			for (EmployeeQualification task : tasks) {
				Map<Integer, Set<JobTaskCriteria>> criterias = task.getTask().getJobTaskCriteriaMap(new Date());
				
				for (Integer key : criterias.keySet()) {
					Set<JobTaskCriteria> currentGroup = criterias.get(key);
					
					Set<AssessmentTest> tests = new HashSet<AssessmentTest>();
					for (JobTaskCriteria criteria : currentGroup) {
						tests.add(criteria.getAssessmentTest());
					}
					
					if (taken.containsAll(tests)) {
						// Find the results that satisfy this qualification?
						for (AssessmentResult result : results) {
							if (tests.contains(result.getAssessmentTest())) {
								if (qualification.get(task) == null)
									qualification.put(task, new ArrayList<AssessmentResult>());
								
								qualification.get(task).add(result);
							}
						}
					}
				}
			}
		}
		
		return qualification;
	}
	
	private class SortTaskByLabel implements Comparator<EmployeeQualification> {
		public int compare(EmployeeQualification o1, EmployeeQualification o2) {
			try {
				Double d1 = Double.parseDouble(o1.getTask().getLabel());
				Double d2 = Double.parseDouble(o2.getTask().getLabel());
				
				return d1.compareTo(d2);
			} catch (Exception e) {
				return o1.getTask().getLabel().compareTo(o2.getTask().getLabel());
			}
		}
	}
}
