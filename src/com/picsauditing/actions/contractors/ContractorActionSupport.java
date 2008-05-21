package com.picsauditing.actions.contractors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.OperatorBean;
import com.picsauditing.access.MenuItem;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.YesNo;

public class ContractorActionSupport extends PicsActionSupport {
	protected int id = 0;
	protected ContractorAccount contractor;
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
		if (contractor == null)
			throw new Exception("Contractor " + this.id + " not found");
		
		checkPermissionToView();
	}
	
	protected void checkPermissionToView() throws NoRightsException {
		// TODO Check permissions to view this contractor
		// ContractorBean.canView
		// Throw out limited operator users
		// throw exception
		if (checkPermissionToView("summary")) return;
		throw new NoRightsException("Contractor");
	}

	public boolean checkPermissionToView(String what) {
		if (permissions.hasPermission(OpPerms.AllContractors)) return true;
		
		// OR
		if (permissions.isContractor()) {
			return permissions.getAccountIdString().equals(Integer.toString(this.id));
		}
		
		if(permissions.hasPermission(OpPerms.StatusOnly)) {
			return false;
		}
		if (permissions.isOperator() || permissions.isCorporate()) {
			// I don't really like this way. It's a bit confusing
			// Basically, if all we're doing is searching for contractors
			// and looking at their summary page, then it's OK
			// If we want to look at their detail, like PQF data
			// Then we have to add them first (generalContractors).
//			if ("summary".equals(what)) {
//				// Until we figure out Contractor viewing permissions better, this will have to do
//				return true;
//			}
			if (permissions.isCorporate()) {
				OperatorBean operator = new OperatorBean();
				try {
					operator.isCorporate = true;
					operator.setFromDB(permissions.getAccountIdString());
					// if any of this corporate operators can see this contractor, 
					// then the corporate users can see them too
					for (String id : operator.facilitiesAL) {
						for(ContractorOperator corporate : getOperators())
							if (corporate.getOperatorAccount().getIdString().equals(id))
							return true;
					}
				} catch (Exception e) {}
				return false;
			}
			// To see anything other than the summary, you need to be on their list
			for(ContractorOperator operator : getOperators())
				if(operator.getOperatorAccount().getIdString().equals(permissions.getAccountIdString()))
			return true;
		}
		
		for(ContractorAudit audit : getActiveAudits()) {
			if (audit.getAuditor().getId() == permissions.getUserId()
					&& (audit.getAuditStatus().equals(AuditStatus.Pending)
						|| audit.getAuditStatus().equals(AuditStatus.Submitted))
				) return true;
		}
		
		return false;
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
