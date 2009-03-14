package com.picsauditing.actions.audits;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;

@SuppressWarnings("serial")
public class ManageCao extends ContractorActionSupport implements Preparable {

	protected ContractorAuditOperator cao = null;
	protected CaoStatus caoBefore = null;

	protected ContractorAuditOperatorDAO caoDao = null;
	public ManageCao(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, ContractorAuditOperatorDAO caoDao) {
		super(accountDao, auditDao);
		this.caoDao = caoDao;
	}

	@Override
	public void prepare() throws Exception {

		int id = this.getParameter("cao.id");
		if (id > 0) {
			this.cao = caoDao.find(id);
			if (this.cao != null)
				this.caoBefore = cao.getStatus();
		}
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (cao == null || cao.getId() == 0)
			throw new Exception("Missing cao");
		
		this.id = cao.getAudit().getContractorAccount().getId();
		findContractor();
		
		if (button != null) {
			if (button.equalsIgnoreCase("save")) {
				// TODO figure out how to set the inherit flag
				cao.setAuditColumns(getUser());
				cao = caoDao.save(cao);

				if (caoBefore != cao.getStatus())
					ContractorAuditOperatorDAO.saveNoteAndEmail(cao, permissions);

				contractor.setNeedsRecalculation(true);
				accountDao.save(contractor);
				
				redirect("AuditCat.action?auditID=" + cao.getAudit().getId());
				return SUCCESS;
			}
		}

		return SUCCESS;
	}

	public ContractorAuditOperator getCao() {
		return cao;
	}

	public void setCao(ContractorAuditOperator cao) {
		this.cao = cao;
	}
}
