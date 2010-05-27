package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ConCorAuditList extends ContractorActionSupport {
	private String auditFor;

	public List<ContractorAudit> upComingAudits = new ArrayList<ContractorAudit>();
	public List<ContractorAudit> currentAudits = new ArrayList<ContractorAudit>();
	public List<ContractorAudit> expiredAudits = new ArrayList<ContractorAudit>();

	public ConCorAuditList(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

		for (ContractorAudit contractorAudit : getAudits()) {
			// Only show COR Audits
			if (contractorAudit.getAuditType().getId() == AuditType.COR) {
				if (contractorAudit.getAuditStatus().isPendingSubmitted()
						|| contractorAudit.getAuditStatus().isIncomplete())
					upComingAudits.add(contractorAudit);
				else if (contractorAudit.getAuditStatus().isActiveResubmittedExempt())
					currentAudits.add(contractorAudit);
				else if (contractorAudit.getAuditStatus().equals(AuditStatus.Expired))
					expiredAudits.add(contractorAudit);
				else {
					// There shouldn't be any others
				}
			}
		}

		if (button != null && button.equals("Add")) {
			if (!Strings.isEmpty(auditFor)) {
				boolean alreadyExists = false;

				for (ContractorAudit conAudit : contractor.getAudits()) {
					if (conAudit.getAuditType().getId() == AuditType.COR && !conAudit.getAuditStatus().isExpired()
							&& conAudit.getAuditFor().equals(auditFor)) {
						alreadyExists = true;
						break;
					}
				}

				if (alreadyExists) {
					addActionError("Audit for " + auditFor + " already exists");
				} else {
					ContractorAudit conAudit = new ContractorAudit();
					conAudit.setAuditType(new AuditType(72));
					conAudit.setAuditFor(this.auditFor);
					conAudit.setContractorAccount(contractor);
					conAudit.changeStatus(AuditStatus.Pending, getUser());
					conAudit.setPercentComplete(0);
					conAudit.setPercentVerified(0);
					conAudit.setManuallyAdded(true);
					conAudit = auditDao.save(conAudit);

					addNote(conAudit.getContractorAccount(), "Added COR for " + auditFor + " manually",
							NoteCategory.Audits, getViewableByAccount(conAudit.getAuditType().getAccount()));

					return "saved";
				}
			}
		}

		return SUCCESS;
	}

	public List<State> getProvinceList() {
		return getStateDAO().findByCountry("CA");
	}

	public boolean isManuallyAddAudit() {
		if (permissions.isContractor() || permissions.isAdmin()) {
			return true;
		}
		return false;
	}

	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}
}
