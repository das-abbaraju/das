package com.picsauditing.actions.audits;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;

/**
 * Class used to edit a ContractorAudit record with virtually no restrictions
 * 
 * @author Trevor
 * 
 */
public class PolicySave extends AuditActionSupport {

	protected String policyStatus;
	protected String redirectOptions;

	public PolicySave(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, ContractorAuditDAO contractorAuditDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findConAudit();
		if ("Verify".equals(policyStatus)) {
			conAudit.changeStatus(AuditStatus.Active, getUser());
		}
		if ("Reject".equals(policyStatus)) {
			conAudit.changeStatus(AuditStatus.Pending, getUser());
		}

		auditDao.save(conAudit);

		if ("oldestPolicy".equals(redirectOptions)) {
			ServletActionContext
					.getResponse()
					.sendRedirect(
							"PolicyVerification.action?filter.visible=Y&filter.auditStatus=Submitted&filter.auditStatus=Resubmitted&button=getFirst");
			return BLANK;
		}
		if ("nextPolicyForContractor".equals(redirectOptions)) {
			ContractorAudit contractorAudit = findNextRequiredPolicyForVerification(conAudit);
			if (contractorAudit != null) {
				ServletActionContext.getResponse().sendRedirect(
						"AuditCat.action?auditID=" + contractorAudit.getId() + "&catDataID="
								+ contractorAudit.getCategories().get(0).getId());
			} else {
				ServletActionContext.getResponse().sendRedirect(
						"AuditCat.action?auditID=" + conAudit.getId() + "&catDataID="
								+ catDataDao.findByAudit(conAudit, permissions).get(0).getId());
			}
			return BLANK;
		}
		if ("backToReport".equals(redirectOptions)) {
			ServletActionContext
					.getResponse()
					.sendRedirect(
							"PolicyVerification.action?filter.visible=Y&filter.auditStatus=Submitted&filter.auditStatus=Resubmitted&filter.auditStatus=Active");
			return BLANK;
		}
		if ("stay".equals(redirectOptions)) {
			ServletActionContext.getResponse().sendRedirect(
					"AuditCat.action?auditID=" + conAudit.getId() + "&catDataID="
							+ catDataDao.findByAudit(conAudit, permissions).get(0).getId());
		}

		return SUCCESS;
	}

	public String getRedirectOptions() {
		return redirectOptions;
	}

	public void setRedirectOptions(String redirectOptions) {
		this.redirectOptions = redirectOptions;
	}

	public String getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(String policyStatus) {
		this.policyStatus = policyStatus;
	}
}
