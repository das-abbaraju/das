package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

@SuppressWarnings("serial")
public class ContractorView extends ContractorActionSupport {
	private AuditBuilder auditBuilder;
	private OperatorTagDAO operatorTagDAO;
	private ContractorTagDAO contractorTagDAO;
	private ContractorOperatorDAO contractorOperatorDAO;
	private InvoiceItemDAO invoiceItemDAO;
	public List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	public int tagId;

	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditBuilder auditBuilder,
			OperatorTagDAO operatorTagDAO, ContractorTagDAO contractorTagDAO,
			ContractorOperatorDAO contractorOperatorDAO, InvoiceItemDAO invoiceItemDAO) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
		this.operatorTagDAO = operatorTagDAO;
		this.contractorTagDAO = contractorTagDAO;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.invoiceItemDAO = invoiceItemDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		if ("AddTag".equals(button)) {
			if (tagId > 0) {
				ContractorTag cTag = new ContractorTag();
				cTag.setContractor(contractor);
				cTag.setTag(new OperatorTag());
				cTag.getTag().setId(tagId);
				cTag.setAuditColumns(permissions);
				contractor.getOperatorTags().add(cTag);
				accountDao.save(contractor);
			}

		}

		if ("RemoveTag".equals(button)) {
			contractorTagDAO.remove(tagId);
		}

		if ("Upgrade to Full Membership".equals(button)) {
			contractor.setAcceptsBids(false);
			contractor.setRenew(true);
			for (ContractorAudit cAudit : contractor.getAudits()) {
				if (cAudit.getAuditType().isPqf() && !cAudit.getAuditStatus().isPending()) {
					cAudit.changeStatus(AuditStatus.Pending, getUser());
					auditDao.save(cAudit);
					break;
				}
			}
			// Setting the payment Expires date to today
			for (Invoice invoice : contractor.getInvoices()) {
				for (InvoiceItem invoiceItem : invoice.getItems()) {
					if (invoiceItem.getInvoiceFee().getId() == InvoiceFee.BIDONLY) {
						invoiceItem.setPaymentExpires(new Date());
						invoiceItemDAO.save(invoiceItem);
					}
				}
			}

			if (permissions.isOperator()) {
				for (ContractorOperator cOperator : contractor.getNonCorporateOperators()) {
					if (cOperator.getOperatorAccount().getId() == permissions.getAccountId()) {
						cOperator.setWorkStatus("Y");
						cOperator.setAuditColumns(permissions);
						contractorOperatorDAO.save(cOperator);
						break;
					}
				}
			}

			contractor.incrementRecalculation();
			contractor.setAuditColumns(permissions);
			accountDao.save(contractor);

			addNote(contractor, "Upgraded the Bid Only Account to a full membership.", NoteCategory.General);

			// Sending a Email to the contractor for upgrade
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(73); // Trial Contractor Account Approval
			emailBuilder.setPermissions(permissions);
			emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
			emailBuilder.addToken("permissions", permissions);
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setPriority(60);
			emailQueue.setFromAddress("billing@picsauditing.com");
			EmailSender.send(emailQueue);

			if (permissions.isContractor()) {
				ServletActionContext.getResponse().sendRedirect(
						"BillingDetail.action?id=" + contractor.getId() + "&button=Create");
				return BLANK;
			}
		}

		if (permissions.isOperator()) {
			operatorTags = getOperatorTagNamesList();

			for (ContractorTag contractorTag : contractor.getOperatorTags()) {
				if (operatorTags.contains(contractorTag.getTag()))
					operatorTags.remove(contractorTag.getTag());
			}
		}
		if (contractor.getNonCorporateOperators().size() > 0) {
			auditBuilder.setUser(getUser());
			auditBuilder.buildAudits(this.contractor);
		}

		this.subHeading = "Contractor Details";

		return SUCCESS;
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
}
