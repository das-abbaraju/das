package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentResultDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.dao.LegacyEmployeeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.Employee;

@SuppressWarnings("serial")
public class EmployeeAssessmentResults extends AccountActionSupport {
	@Autowired
	protected AccountDAO accountDAO;
	@Autowired
	protected AssessmentResultDAO resultDAO;
	@Autowired
	protected AssessmentTestDAO testDAO;
	@Autowired
	protected LegacyEmployeeDAO legacyEmployeeDAO;

	protected Account account;
	protected Employee employee;
	protected Account assessmentCenter;
	protected Date effectiveDate;
	protected String date;
	protected List<AssessmentResult> effective;
	protected List<AssessmentResult> expired;

	protected int resultID;
	protected boolean showHeader = false;

	public String execute() throws Exception {
		subHeading = getText("EmployeeAssessmentResults.title");

		return SUCCESS;
	}

	public boolean isCanEdit() {
		if ((permissions.isContractor() || permissions.isAdmin())
				&& (date == null || date.equals(DateBean.format(new Date(), "yyyy-MM-dd"))))
			return true;

		return false;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Account getAssessmentCenter() {
		return assessmentCenter;
	}

	public void setAssessmentCenter(Account assessmentCenter) {
		this.assessmentCenter = assessmentCenter;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Date getEffectiveDate() {
		if (effectiveDate == null)
			effectiveDate = new Date();

		return effectiveDate;
	}

	public int getResultID() {
		return resultID;
	}

	public void setResultID(int resultID) {
		this.resultID = resultID;
	}

	public boolean isShowHeader() {
		if (permissions.isContractor())
			showHeader = true;

		return showHeader;
	}

	public List<AssessmentResult> getEffective() {
		if (effective == null) {
			Date date = getEffectiveDate();

			if (employee != null)
				effective = resultDAO.findInEffect("a.employee.id = " + employee.getId(), date);
			else
				effective = resultDAO.findInEffect("a.employee.account.id = " + account.getId(), date);

			if (assessmentCenter != null) {
				List<AssessmentResult> list = new ArrayList<AssessmentResult>();
				for (AssessmentResult result : effective) {
					if (result.getAssessmentTest().getAssessmentCenter().equals(assessmentCenter))
						list.add(result);
				}

				effective = list;
			}
		}

		return effective;
	}

	public List<AssessmentResult> getExpired() {
		if (expired == null) {
			Date date = getEffectiveDate();

			if (employee != null)
				expired = resultDAO.findExpired("a.employee.id = " + employee.getId(), date);
			else
				expired = resultDAO.findExpired("a.employee.account.id = " + account.getId(), date);

			if (assessmentCenter != null) {
				List<AssessmentResult> list = new ArrayList<AssessmentResult>();
				for (AssessmentResult result : expired) {
					if (result.getAssessmentTest().getAssessmentCenter().equals(assessmentCenter))
						list.add(result);
				}

				expired = list;
			}
		}

		return expired;
	}

	public List<Account> getAllAssessmentCenters() {
		return accountDAO.findWhere("a.type = 'Assessment'");
	}
}