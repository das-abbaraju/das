package com.picsauditing.actions.contractors;

import java.util.Map;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.FeeClass;

@SuppressWarnings("serial")
public class ContractorPricing extends ContractorActionSupport{
	private ContractorAccount con;
	private int id;
	private int employeeGUARDNum;
	private int docuGUARDNum;
	private int auditGUARDNum;
	
	@Override
	public String execute() {
		Map<FeeClass, ContractorFee>fees = con.getFees();
		
		
		docuGUARDNum = fees.get(FeeClass.DocuGUARD).getCurrentLevel().getMinFacilities();
		auditGUARDNum = fees.get(FeeClass.AuditGUARD).getCurrentLevel().getMinFacilities();
		employeeGUARDNum = fees.get(FeeClass.EmployeeGUARD).getCurrentLevel().getMinFacilities();
		
		return SUCCESS;
	}

	
	public ContractorAccount getCon() {
		return con;
	}
	
	public void setCon(ContractorAccount con) {
		this.con = con;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getEmployeeGUARDNum() {
		return employeeGUARDNum;
	}

	public int getDocuGUARDNum() {
		return docuGUARDNum;
	}

	public int getAuditGUARDNum() {
		return auditGUARDNum;
	}
	
	
}
