package com.picsauditing.access;

import java.util.ArrayList;

public class Menu {
	private String prompt;
	private ArrayList<MenuItem> items = new ArrayList<MenuItem>();
	private ArrayList<Menu> subMenus = new ArrayList<Menu>();
	private Permissions permissions;
	
	/**
	 * Only return items for which this person has permission to see
	 * @return
	 */
	public ArrayList<MenuItem> getValidItems() {
		ArrayList<MenuItem> validItems = new ArrayList<MenuItem>();
		if (permissions == null) return validItems;
		
		for(MenuItem item : items)
			if (item.canSee(permissions))
				validItems.add(item);
		return validItems;
	}
	////////////////////////
	// Getters and Setters
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public ArrayList<MenuItem> getItems() {
		return items;
	}
	public void setItems(ArrayList<MenuItem> items) {
		this.items = items;
	}
	public ArrayList<Menu> getSubMenus() {
		return subMenus;
	}
	public void setSubMenus(ArrayList<Menu> subMenus) {
		this.subMenus = subMenus;
	}
	
	private boolean addItem(String url, String prompt, OpPerms perm) {
		return this.items.add(new MenuItem(url, prompt, perm));
	}
	private boolean addItem(String url, String prompt, int inGroup) {
		return this.items.add(new MenuItem(url, prompt, inGroup));
	}
	private boolean addItem(String url, String prompt) {
		return this.items.add(new MenuItem(url, prompt));
	}
	
	public Permissions getPermissions() {
		return permissions;
	}
	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}
	
	public void fillPicsMenu(Permissions perm) {
		this.items.clear();
		this.setPermissions(perm);

		// TODO Create admin permissions for each of these reports
		int adminGroup = 10;
		this.addItem("report_activation.jsp?changed=1", "Activation Report", adminGroup);
		this.addItem("report_annualUpdate.jsp?changed=1", "Annual Update Report", adminGroup);
		this.addItem("report_audits.jsp?changed=1", "Audit Dates Report", adminGroup);
		this.addItem("report_operatorContractor.jsp?changed=1", "Contractor Assignments", adminGroup);
		this.addItem("report_contactInfo.jsp?changed=1", "Contractor Contact Info");
		this.addItem("contractorsSearch.jsp", "Contractor Search", OpPerms.SearchContractors);
		// I'm not sure if OpPerms.SearchContractors on the next item is correct
		this.addItem("report_operatorContractor.jsp?searchCorporate=Y", "Corporate Contractors Report", OpPerms.SearchContractors);
		this.addItem("report_EMRRates.jsp?changed=1", "EMR Rates Report");
		this.addItem("report_expiredAudits.jsp?changed=1", "Expired Audits Report");
		this.addItem("report_expiredCertificates.jsp?changed=1", "Expired Insurance Certificates", OpPerms.InsuranceCerts);
		this.addItem("report_fatalities.jsp?changed=1", "Fatalities Report");
		this.addItem("report_incidenceRates.jsp?changed=1", "Incidence Rates Report");
		this.addItem("report_incompleteAudits.jsp?incompleteAfter=3&changed=1", "Incomplete Audits Report");
		this.addItem("report_certificates.jsp?changed=1", "Insurance Certificates", OpPerms.InsuranceCerts);
		this.addItem("verify_insurance.jsp", "Insurance Verification", OpPerms.InsuranceVerification);
		this.addItem("report_ncms.jsp", "NCMS Data Report", OpPerms.NCMS );
		this.addItem("audit_calendar.jsp?changed=1", "Office Audit Calendar", OpPerms.OfficeAuditCalendar);
		this.addItem("report_payment.jsp?changed=1", "Payment Report", adminGroup);
		this.addItem("op_editFlagCriteria.jsp", "Red Flag Criteria", OpPerms.EditFlagCriteria);
		this.addItem("report_scheduleAudits.jsp?changed=1&which="+com.picsauditing.PICS.SearchBean.RESCHEDULE_AUDITS, "Reschedule Audits", adminGroup);
		this.addItem("report_daAudit.jsp", "Schedule Drug &amp; Alcohol Audits", OpPerms.AssignAudits);
		this.addItem("report_desktop.jsp", "Schedule Desktop Audits", OpPerms.AssignAudits);
		this.addItem("report_scheduleAudits.jsp?changed=1", "Schedule Office Audits", OpPerms.OfficeAuditCalendar);
		this.addItem("report_upgradePayment.jsp?changed=1", "Upgrade Payment Report", adminGroup);
		this.addItem("users_manage.jsp", "Manage Users", OpPerms.EditUsers);
		this.addItem("faces/administration/index.xhtml", "Administration Dashboard", adminGroup);
	}
}
