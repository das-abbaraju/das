package com.picsauditing.actions.contractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.dao.TransactionDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.Transaction;
import com.picsauditing.util.Images;

@SuppressWarnings("serial")
public class ContractorDetails extends ContractorActionSupport {
	private AuditBuilder auditBuilder;
	private OperatorTagDAO operatorTagDAO;
	private TransactionDAO transactionDAO;
	public List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	public int tagId;

	private int logoWidth = 0;

	private List<ContractorAudit> auditList = new ArrayList<ContractorAudit>();

	public ContractorDetails(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditBuilder auditBuilder,
			OperatorTagDAO operatorTagDAO, TransactionDAO transactionDAO) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
		this.operatorTagDAO = operatorTagDAO;
		this.transactionDAO = transactionDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		if (button != null) {
			if (button.equals("upcoming")) {
				for (ContractorAudit contractorAudit : getAudits()) {
					// Only show Insurance policies or all of them
					if (!contractorAudit.getAuditType().isAnnualAddendum()
							&& !contractorAudit.getAuditType().getClassType().isPolicy()) {
						if (contractorAudit.getAuditStatus().isPendingSubmitted()
								|| contractorAudit.getAuditStatus().isIncomplete())
							auditList.add(contractorAudit);
					}
				}
				return "audits";
			}
			if (button.equals("current")) {
				for (ContractorAudit contractorAudit : getAudits()) {
					// Only show Insurance policies or all of them
					if (!contractorAudit.getAuditType().isAnnualAddendum()
							&& !contractorAudit.getAuditType().getClassType().isPolicy()) {
						if (contractorAudit.getAuditStatus().isActiveResubmittedExempt())
							auditList.add(contractorAudit);
					}
				}
				return "audits";
			}
			return SUCCESS;
		}

		if (contractor.getNonCorporateOperators() != null && contractor.getNonCorporateOperators().size() > 0) {
			auditBuilder.setUser(getUser());
			auditBuilder.buildAudits(this.contractor);
		}

		this.subHeading = "Contractor Details";

		return SUCCESS;
	}

	public int getLogoWidth() {
		if (contractor.getLogoFile() == null)
			return 0;
		if (contractor.getLogoFile().equals("No"))
			return 0;

		if (logoWidth == 0) {
			String filename = getFtpDir() + "/logos/" + contractor.getLogoFile();
			try {
				logoWidth = Images.getWidth(filename);
			} catch (IOException e) {
				System.out.println("failed to get logo width of " + filename + ": " + e.getMessage());
			}
			if (logoWidth > 300)
				logoWidth = 300;
		}
		return logoWidth;
	}

	public List<OperatorTag> getOperatorTagNamesList() throws Exception {
		if (operatorTags != null && operatorTags.size() > 0)
			return operatorTags;

		return operatorTagDAO.findByOperator(permissions.getAccountId(), true);
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public List<OperatorTag> getOperatorTags() {
		return operatorTags;
	}

	public void setOperatorTags(List<OperatorTag> operatorTags) {
		this.operatorTags = operatorTags;
	}

	public boolean isCanUpgrade() {
		if (permissions.isContractor())
			return true;
		if (permissions.seesAllContractors())
			return true;
		if (permissions.isOperator() && permissions.hasPermission(OpPerms.ViewTrialAccounts, OpType.Edit))
			return true;

		return false;
	}

	public List<ContractorAudit> getAuditList() {
		return auditList;
	}

	public void setAuditList(List<ContractorAudit> audits) {
		this.auditList = audits;
	}

	public List<Transaction> getTransactions() {
		List<Transaction> transactionList = transactionDAO.findWhere("t.account.id = " + contractor.getId());
		if (transactionList == null)
			return new ArrayList<Transaction>();
		return transactionList;
	}

}
