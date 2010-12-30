package com.picsauditing.actions.flags;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorOperator;

@SuppressWarnings("serial")
public class ContractorFlagChanges extends ContractorActionSupport {

	protected int conID = 0;
	protected List<ContractorOperator> conOpList = new ArrayList<ContractorOperator>();
	protected Set<Integer> conOpSave = new HashSet<Integer>();
	protected ContractorOperatorDAO contractorOperatorDao;

	public ContractorFlagChanges(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDao) {
		super(accountDao, auditDao);
		this.contractorOperatorDao = contractorOperatorDao;

	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		if (conID > 0) {
			List<ContractorOperator> tempConOps = contractorOperatorDao.findByContractor(conID, permissions);
			for (ContractorOperator co : tempConOps) {
				if (co.getBaselineFlag()!=null && !co.getFlagColor().equals(co.getBaselineFlag()))
					conOpList.add(co);
			}
		}
		if ("save".equals(button)) {
			for(Iterator<ContractorOperator> it = conOpList.iterator(); it.hasNext();){
				ContractorOperator co = it.next();
				if(conOpSave.contains(co.getId())){
					co.setBaselineFlag(co.getFlagColor());
					co.setBaselineApprover(permissions.getUserId());
					co.setBaselineApproved(new Date());
					contractorOperatorDao.save(co);
					it.remove();
				}
			}
		}
		return SUCCESS;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public List<ContractorOperator> getConOpList() {
		return conOpList;
	}

	public Set<Integer> getConOpSave() {
		return conOpSave;
	}

	public void setConOpSave(Set<Integer> conOpSave) {
		this.conOpSave = conOpSave;
	}

}
