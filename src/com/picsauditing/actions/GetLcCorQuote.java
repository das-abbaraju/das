package com.picsauditing.actions;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.LcCorPhase;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class GetLcCorQuote extends PicsActionSupport {
	protected int id;
	protected ContractorAccount contractor;
	protected User user;
	
	protected String totalEmployees = "";
	
	protected int[] provIndex;
	protected String[] provinces;
	protected String[] partners;
	protected String[] employees;
	protected boolean confirmQuote;

	@Autowired
	protected ContractorAccountDAO contractorAccountDao;
	@Autowired
	protected UserDAO userDao;
	@Autowired
	protected EmailSender emailSender;

	@Anonymous
	@Override
	public String execute() throws Exception {
		loadPermissions(false);
		
		findContractor();
		if (contractor == null)
			contractor = new ContractorAccount();
		findUser();
		findTotalEmployees();
		
		setup();
		return SUCCESS;
	}
	
	private void setup() {
		setupProvinces();

		partners = new String[13];
		employees = new String[13];
		provIndex = new int[13];
		for (int i=0; i<13; i++) {
			partners[i] = "";
			employees[i] = "";
			provIndex[i] = -1;
		}
	}

	private void setupProvinces() {
		int i;
		i=0;
		provinces = new String[13];
		provinces[i++] = "Alberta";
		provinces[i++] = "British Columbia";
		provinces[i++] = "Manitoba";
		provinces[i++] = "New Brunswick";
		provinces[i++] = "Newfoundland & Labrador";
		provinces[i++] = "Northwest Territories";
		provinces[i++] = "Nova Scotia";
		provinces[i++] = "Nunavut";
		provinces[i++] = "Ontario";
		provinces[i++] = "Prince Edward Island";
		provinces[i++] = "Quebec";
		provinces[i++] = "Saskatchewan";
		provinces[i++] = "Yukon";
	}

	private void findUser() {
		if (permissions.isContractor()) {
			user = userDao.find(permissions.getUserId());
		} else if (contractor.getPrimaryContact() != null) {
			user = contractor.getPrimaryContact();
		}
		
		if (user == null) {
			user = new User();
		}
	}
	
	private void findTotalEmployees() {
		if (contractor.getSortedAnnualUpdates().size() == 0)
			return;
		
		ContractorAudit audit = contractor.getSortedAnnualUpdates().get(0);
		
		for (AuditData data:audit.getData()) {
			if (data.getQuestion().getId() == 5179) {
				totalEmployees = data.getAnswer();
				break;
			}
		}
	}

	public String remindMeLater() throws Exception {
		if (id != 0) {
			findContractor();
			changeLcCorPhase(contractor, LcCorPhase.RemindMeLater);
			contractorAccountDao.save(contractor);
			return setUrlForRedirect("ContractorView.action?id=" + contractor.getId());
		} else {
			return setUrlForRedirect("http://www.picsauditing.com/");
		}
	}

	public String noThanks() throws Exception {
		if (id != 0) {
			findContractor();
			changeLcCorPhase(contractor, LcCorPhase.NoThanks);
			contractorAccountDao.save(contractor);
			return setUrlForRedirect("ContractorView.action?id=" + contractor.getId());
		} else {
			return setUrlForRedirect("http://www.picsauditing.com/");
		}
	}

	public String generateQuote() throws Exception {
		setupProvinces();
		
		if (Strings.isEmpty(user.getName())) {
			addActionError(getText("GetLcCorQuote.error.name"));
		}
		if (Strings.isEmpty(user.getEmail())) {
			addActionError(getText("GetLcCorQuote.error.email"));
		}
		if (Strings.isEmpty(user.getPhone())) {
			addActionError(getText("GetLcCorQuote.error.phone"));
		}
		if (Strings.isEmpty(contractor.getName())) {
			addActionError(getText("GetLcCorQuote.error.contractor"));
		}
		if (Strings.isEmpty(totalEmployees)) {
			addActionError(getText("GetLcCorQuote.error.employees"));
		}
		
		if (provIndex == null || provIndex.length == 0) {
			addActionError(getText("GetLcCorQuote.error.province"));
		}
		
		if (getActionErrors().size() == 0) {
			String subject = "LC-COR Quote";
			if (id != 0) {
				findContractor();
				subject += " for PICS ID " + id;
			}
			String body = "";
			body += "Name:" + convertNullToEmptyString(user.getName()) + "\n\n";
			body += "Company:" + convertNullToEmptyString(contractor.getName()) + "\n\n";
			body += "Email:" + convertNullToEmptyString(user.getEmail()) + "\n\n";
			body += "Phone:" + convertNullToEmptyString(user.getPhone()) + "\n\n";
			body += "Total Employees:" + convertNullToEmptyString(totalEmployees) + "\n\n\n\n";
			for (int i=0; i< provIndex.length; i++) {
				int index = provIndex[i];
				body += provinces[index];
				if (!Strings.isEmpty(partners[index]) || !Strings.isEmpty(employees[index]))
					body += ": ";
				if (!Strings.isEmpty(partners[index]))
					body += partners[index];
				if (!Strings.isEmpty(employees[index]))
					body += " (" + employees[index] + ")";
				body += "\n\n";
			}

			emailSender.send("lccor@picsauditing.com", subject, body);
			
			if (contractor != null) {
				changeLcCorPhase(contractor, LcCorPhase.Done);
				contractorAccountDao.save(contractor);
			}
			
			if (id == 0)
				return setUrlForRedirect("GetLcCorQuote.action?confirmQuote=true");
			else
				return setUrlForRedirect("GetLcCorQuote.action?id=" + id + "&confirmQuote=true");
		}
		
		return SUCCESS;
	}
	
	private String convertNullToEmptyString(String s) {
		if (s==null)
			return "";
		return s;
	}

	public String goHome() throws Exception {
		if (id != 0) {
			findContractor();
			return setUrlForRedirect("ContractorView.action?id=" + contractor.getId());
		} else {
			return setUrlForRedirect("http://www.picsauditing.com/");
		}
	}

	protected void findContractor() {
		loadPermissions();
		if (permissions.isContractor())
			id = permissions.getAccountId();

		contractor = contractorAccountDao.find(id);
	}
	
	private void changeLcCorPhase(ContractorAccount contractor, LcCorPhase phase) {
		Calendar cal = Calendar.getInstance();
		boolean isSetToCorExpire = false;
		
		if (phase.equals(LcCorPhase.RemindMeLater)) {
			cal.add(Calendar.DATE, 14);
			contractor.setLcCorNotification(cal.getTime());
			if (contractor.getLcCorPhase().isAuditPhase())
				contractor.setLcCorPhase(LcCorPhase.RemindMeLaterAudit);
			else
				contractor.setLcCorPhase(LcCorPhase.RemindMeLater);
		} else if (phase.equals(LcCorPhase.NoThanks)) {
			if (contractor.getLcCorPhase().equals(LcCorPhase.RemindMeLater)) {
				cal.add(Calendar.MONTH, 3);
				contractor.setLcCorPhase(LcCorPhase.NoThanks);
				contractor.setLcCorNotification(cal.getTime());
			} else if (contractor.getLcCorPhase().equals(LcCorPhase.RemindMeLaterAudit)) {
				cal.add(Calendar.MONTH, 3);
				contractor.setLcCorPhase(LcCorPhase.NoThanksAudit);
				contractor.setLcCorNotification(cal.getTime());
			} else if (contractor.getLcCorPhase().equals(LcCorPhase.NoThanks)){
				contractor.setLcCorPhase(LcCorPhase.Done);
			} else {
				contractor.setLcCorPhase(LcCorPhase.Done);
				isSetToCorExpire = true;
			}
		} else if (phase.equals(LcCorPhase.Done)) {
			if (contractor.getLcCorPhase().isAuditPhase())
				isSetToCorExpire = true;
			contractor.setLcCorPhase(LcCorPhase.Done);
		}
		
		if (isSetToCorExpire) {
			ContractorAudit corAudit = null;
			for (ContractorAudit audit:contractor.getAudits()) {
				if (audit.getAuditType().getId() == AuditType.COR) {
					corAudit = audit;
					break;
				}
			}
			
			if (corAudit != null && corAudit.getExpiresDate() != null) {
				contractor.setLcCorNotification(corAudit.getExpiresDate());
			} else
				contractor.setLcCorNotification(cal.getTime());
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTotalEmployees() {
		return totalEmployees;
	}

	public void setTotalEmployees(String totalEmployees) {
		this.totalEmployees = totalEmployees;
	}
	
	public String[] getProvinces() {
		return provinces;
	}

	public void setProvinces(String[] provinces) {
		this.provinces = provinces;
	}

	public String[] getPartners() {
		return partners;
	}

	public void setPartners(String[] partners) {
		this.partners = partners;
	}

	public String[] getEmployees() {
		return employees;
	}

	public void setEmployees(String[] employees) {
		this.employees = employees;
	}

	public int[] getProvIndex() {
		return provIndex;
	}

	public void setProvIndex(int[] provIndex) {
		this.provIndex = provIndex;
	}

	public boolean isConfirmQuote() {
		return confirmQuote;
	}

	public void setConfirmQuote(boolean confirmQuote) {
		this.confirmQuote = confirmQuote;
	}
}
