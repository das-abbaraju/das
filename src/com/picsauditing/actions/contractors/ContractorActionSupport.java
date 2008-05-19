package com.picsauditing.actions.contractors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.MenuItem;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.YesNo;

public class ContractorActionSupport extends PicsActionSupport {
	protected int id = 0;
	protected ContractorAccount contractor;
	@Autowired(required=true)
	protected ContractorAccountDAO accountDao;
	protected ContractorAuditDAO auditDao;
	private List<ContractorOperator> operators;
	
	protected String subHeading;

	public ContractorActionSupport(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		this.accountDao = accountDao;
		this.auditDao = auditDao;
	}
	
	protected void findContractor() throws Exception {
		loadPermissions();
		if (permissions.isContractor())
			id = permissions.getAccountId();
		
		
		contractor = accountDao.find(id);
		if (contractor.getId() == 0)
			throw new Exception("Contractor " + this.id + " not found");
		
		// TODO Check permissions to view this contractor
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

	public List<ContractorAudit> getActiveAudits() {
		return auditDao.findNonExpiredByContractor(contractor.getId());
	}
	
	public List<ContractorOperator> getOperators() {
		if (operators == null)
			operators = accountDao.findOperators(contractor, permissions);
		return operators;
	}

	public String getSubHeading() {
		return subHeading;
	}

	public void setSubHeading(String subHeading) {
		this.subHeading = subHeading;
	}
	/**
	 * Only show the insurance link for contractors who are linked 
	 * to an operator that collects insurance data. Also, don't show 
	 * the link to users who don't have the InsuranceCerts permission.
	 * 
	 */
	public boolean isHasInsurance() {
		if (!permissions.isContractor() && !permissions.hasPermission(OpPerms.InsuranceCerts))
			return false;
		
		for(ContractorOperator insurContractors : getOperators())
	  		if(insurContractors.getOperatorAccount().getCanSeeInsurance().equals(YesNo.Yes))
	  			return true;

  		return false;	
	}
}
