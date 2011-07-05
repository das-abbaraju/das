package com.picsauditing.actions.contractors;

import java.util.Map;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.FeeClass;

@SuppressWarnings("serial")
public class ContractorPricing extends ContractorActionSupport{
	private ContractorAccountDAO contractorDao;
	private com.picsauditing.jpa.entities.ContractorAccount con;
	private int id;
	private int listedNum;
	private int docuGUARDNum;
	private int auditGUARDNum;
	
	public ContractorPricing (ContractorAccountDAO contractorDao){
		this.contractorDao = contractorDao;
	}
	
	@Override
	public String execute() {
		if (!forceLogin())
			return LOGIN;
		
		con = contractorDao.find(id);
		
		Map<FeeClass, ContractorFee>fees = con.getFees();
		
		listedNum = fees.get(FeeClass.ListOnly).getCurrentLevel().getMinFacilities();
		docuGUARDNum = fees.get(FeeClass.DocuGUARD).getCurrentLevel().getMinFacilities();
		auditGUARDNum = fees.get(FeeClass.AuditGUARD).getCurrentLevel().getMinFacilities();
				
		return SUCCESS;
	}

	
	public String getCon() {
		return con.getName();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getListedNum() {
		return listedNum;
	}

	public int getDocuGUARDNum() {
		return docuGUARDNum;
	}

	public int getAuditGUARDNum() {
		return auditGUARDNum;
	}
	
	
}
