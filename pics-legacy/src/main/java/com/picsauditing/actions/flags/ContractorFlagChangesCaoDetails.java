package com.picsauditing.actions.flags;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;

@SuppressWarnings("serial")
public class ContractorFlagChangesCaoDetails extends ContractorActionSupport {
	@Autowired
	private ContractorAuditOperatorDAO caoDao;

	protected int previousID;
	private ContractorAuditOperator newCao;
	private ContractorAuditOperator oldCao;

	@Override
	public String execute() throws Exception {
		newCao = caoDao.find(id);
		oldCao = caoDao.find(previousID);

		return SUCCESS;
	}

	public String rollback() {
		newCao = caoDao.find(id);
		oldCao = caoDao.find(previousID);
		newCao.changeStatus(oldCao.getStatus(), permissions);
		caoDao.save(newCao);

		addActionMessage("Previous cao status reloaded");

		return BLANK;
	}

	public int getPreviousID() {
		return previousID;
	}

	public void setPreviousID(int previousID) {
		this.previousID = previousID;
	}

	public String getOldContractorAuditOperator() {
		return oldCao.getOperator().getName();
	}

	public AuditStatus getOldContractorAuditOperatorStatus() {
		return oldCao.getStatus();
	}

	public int getOldContractorAuditOperatorPercentComplete() {
		return oldCao.getPercentComplete();
	}

	public String getNewContractorAuditOperator() {
		return newCao.getOperator().getName();
	}

	public AuditStatus getNewContractorAuditOperatorStatus() {
		return newCao.getStatus();
	}

	public int getNewContractorAuditOperatorPercentComplete() {
		return newCao.getPercentComplete();
	}

	public ContractorAuditOperator getNewCao() {
		return newCao;
	}

	public void setNewCao(ContractorAuditOperator newCao) {
		this.newCao = newCao;
	}

	public ContractorAuditOperator getOldCao() {
		return oldCao;
	}

	public void setOldCao(ContractorAuditOperator oldCao) {
		this.oldCao = oldCao;
	}
}