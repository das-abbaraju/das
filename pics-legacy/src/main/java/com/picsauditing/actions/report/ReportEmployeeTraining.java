package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.report.RecordNotFoundException;

@SuppressWarnings("serial")
public class ReportEmployeeTraining extends ReportEmployee {
	@Autowired
	protected EmployeeDAO employeeDAO;
	@Autowired
	protected ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	protected OperatorAccountDAO operatorDAO;

	protected int page = 1;
	protected OperatorAccount operator;
	protected List<EmployeeTraining> employeeTraining;

	@Override
	public String execute() throws Exception {
		if (permissions.isContractor())
			throw new NoRightsException(getText("global.Operator"));

		if (operator == null && permissions.isOperatorCorporate())
			operator = operatorDAO.find(permissions.getAccountId());

		if (operator == null)
			throw new RecordNotFoundException("Operator");

		getFilter().setShowSsn(false);
		getFilter().setShowEmail(false);

		buildQuery();
		download = true;
		run(sql);

		employeeTraining = new ArrayList<EmployeeTraining>();
		List<BasicDynaBean> adds = new ArrayList<BasicDynaBean>();

		Calendar cal = Calendar.getInstance();
		for (BasicDynaBean d : data) {
			/*
			 * if ("1".equals(d.get("completed1"))) { cal.setTime((Date) d.get("completionDate"));
			 * cal.add(Calendar.YEAR, 1);
			 * 
			 * d.set("expiration", cal.getTime()); d.set("training",
			 * getTextParameterized("ReportEmployeeTraining.SiteOrientation", getOrientationTraining()));
			 * d.set("completed", "1"); }
			 * 
			 * if ("1".equals(d.get("completed2"))) { BasicDynaBean d2 = new BasicDynaBean(new BasicDynaClass());
			 * d2.set("training", getText("ReportEmployeeTraining.JourneyToZero")); d2.set("accountID",
			 * d.get("accountID")); d2.set("name", d.get("name")); d2.set("employeeID", d.get("employeeID"));
			 * d2.set("lastName", d.get("lastName")); d2.set("firstName", d.get("firstName")); d2.set("completed", "1");
			 * 
			 * cal.setTime((Date) d.get("completionDate")); cal.add(Calendar.DAY_OF_YEAR, 7);
			 * 
			 * d2.set("completedDate", cal.getTime());
			 * 
			 * cal.add(Calendar.YEAR, 1); d2.set("expiration", getCompletedDate(cal.getTime(), 0)); } else {
			 * 
			 * }
			 * 
			 * if ("1".equals(d.get("completed3"))) { d.set("expiration", getCompletedDate((Date)
			 * d.get("completionDate"), 21)); } else {
			 * 
			 * }
			 */

			Employee e = new Employee();
			e.setId((Integer) d.get("employeeID"));
			e.setLastName(d.get("lastName").toString());
			e.setFirstName(d.get("firstName").toString());
			e.setUpdateDate((Date) d.get("completionDate"));

			Account a = new Account();
			a.setId((Integer) d.get("accountID"));
			a.setName(d.get("name").toString());

			e.setAccount(a);

			if ("1".equals(d.get("completed1").toString())) {
				EmployeeTraining et = new EmployeeTraining(e, getTextParameterized(
						"ReportEmployeeTraining.SiteOrientation", getOrientationTraining()), getCompletedDate(
						e.getUpdateDate(), 0), true);
				employeeTraining.add(et);
			} else {
				EmployeeTraining et = new EmployeeTraining(e, getTextParameterized(
						"ReportEmployeeTraining.SiteOrientation", getOrientationTraining()), null, false);
				employeeTraining.add(et);
			}

			if ("1".equals(d.get("completed2").toString())) {
				EmployeeTraining et = new EmployeeTraining(e, getText("ReportEmployeeTraining.JourneyToZero"),
						getCompletedDate(e.getUpdateDate(), 21), true);
				employeeTraining.add(et);
			} else {
				EmployeeTraining et = new EmployeeTraining(e, getText("ReportEmployeeTraining.JourneyToZero"), null,
						false);
				employeeTraining.add(et);
			}

			if ("1".equals(d.get("completed3").toString())) {
				EmployeeTraining et = new EmployeeTraining(e, getText("ReportEmployeeTraining.OperationalExcellence"),
						getCompletedDate(e.getUpdateDate(), 14), true);
				employeeTraining.add(et);
			} else {
				EmployeeTraining et = new EmployeeTraining(e, getText("ReportEmployeeTraining.OperationalExcellence"),
						null, false);
				employeeTraining.add(et);
			}
		}

		return SUCCESS;
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID = " + operator.getId());
		sql.addField("CASE WHEN DATE_ADD(e.updateDate, INTERVAL 1 YEAR) < NOW() "
				+ "THEN DATE_ADD(e.updateDate, INTERVAL 1 YEAR) ELSE e.updateDate END AS completionDate");
		sql.addField("1 AS completed1");
		sql.addField("e.id % 2 = 0 AS completed2");
		sql.addField("e.id % 3 = 0 AS completed3");
	}

	private String getOrientationTraining() {
		if (operator != null) {
			return operator.getName().replaceAll("Suncor Energy", "").replaceAll("Refinery", "").trim();
		}

		return "";
	}

	private Date getCompletedDate(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, days);

		Calendar lastYear = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);

		if (cal.getTime().before(lastYear.getTime())) {
			cal.add(Calendar.YEAR, 1);
		}

		return cal.getTime();
	}

	public int getEnd() {
		int end = page * 100 - 1;
		
		if (end > employeeTraining.size())
			end = employeeTraining.size() - 1;
		
		return end;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public List<EmployeeTraining> getEmployees() {
		return employeeTraining;
	}

	public class EmployeeTraining {
		private Employee employee;
		private String training;
		private Date completed;
		private Date expiration;
		private boolean complete;

		public EmployeeTraining(Employee employee, String training, Date completed, boolean complete) {
			this.employee = employee;
			this.training = training;
			this.completed = completed;
			this.complete = complete;

			if (completed != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(completed);
				cal.add(Calendar.YEAR, 1);
				this.expiration = cal.getTime();
			}
		}

		public Employee getEmployee() {
			return employee;
		}

		public void setEmployee(Employee employee) {
			this.employee = employee;
		}

		public String getTraining() {
			return training;
		}

		public void setTraining(String training) {
			this.training = training;
		}

		public Date getCompleted() {
			return completed;
		}

		public void setCompleted(Date completed) {
			this.completed = completed;
		}

		public Date getExpiration() {
			return expiration;
		}

		public void setExpiration(Date expiration) {
			this.expiration = expiration;
		}

		public boolean isComplete() {
			return complete;
		}

		public void setComplete(boolean complete) {
			this.complete = complete;
		}
	}
}
