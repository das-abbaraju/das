package com.picsauditing.actions.report;

import java.util.Iterator;

import com.picsauditing.audits.AuditBuilderFactory;
import com.picsauditing.service.contractor.ContractorOperatorService;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportBiddingContractors extends ReportAccount {
	
	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	protected ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	protected InvoiceItemDAO invoiceItemDAO;
	@Autowired
	protected OperatorAccountDAO operatorAccountDAO;
	@Autowired
	protected ContractorAuditDAO contractorAuditDAO;
	@Autowired
	protected ContractorAuditOperatorDAO contractorAuditOperatorDAO;
	@Autowired
	protected AuditBuilderFactory auditBuilderFactory;
	@Autowired
	private EmailSender emailSender;
    @Autowired
    protected ContractorOperatorService contractorOperatorService;


    protected ContractorAccount contractor;
	protected String operatorNotes;

	@Override
	protected void buildQuery() {
		skipPermissions = true;
		super.buildQuery();

		filteredDefault = true;
		getFilter().setShowConWithPendingAudits(false);

		// Anytime we query contractor accounts as an operator,
		// get the flag color/status at the same time
		if (permissions.isOperatorCorporate()) {
			sql.addJoin("JOIN contractor_operator co ON co.conID = a.id AND co.opID = " + permissions.getAccountId());
			sql.addField("co.workStatus");
			sql.addField("co.waitingOn");
			sql.addWhere("co.opID = " + permissions.getAccountId());
		}
		
		sql.addWhere("c.accountLevel = 'BidOnly'");

		PermissionQueryBuilder qb = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.SQL);
		qb.setWorkingFacilities(false);
		sql.addWhere("1 " + qb.toString());
	}

	public String upgrade() throws Exception {
		contractor.setAccountLevel(AccountLevel.Full);
		contractor.setRenew(true);

		auditBuilderFactory.buildAudits(contractor);

		for (ContractorAudit cAudit : contractor.getAudits()) {
			if (cAudit.getAuditType().isPicsPqf()) {
				for (ContractorAuditOperator cao : cAudit.getOperators()) {
					if (cao.getStatus().after(AuditStatus.Pending)) {
						cao.changeStatus(AuditStatus.Pending, permissions);
						contractorAuditOperatorDAO.save(cao);
					}
				}

				auditBuilderFactory.recalculateCategories(cAudit);
                auditBuilderFactory.recalcAllAuditCatDatas(cAudit);
                auditBuilderFactory.percentCalculateComplete(cAudit);
				contractorAuditDAO.save(cAudit);
			}
		}

		if (permissions.isOperator() && permissions.isApprovesRelationships()) {
			approveContractor(contractor, permissions.getAccountId());
		}

		if (permissions.isCorporate()) {
			OperatorAccount corporate = operatorAccountDAO.find(permissions.getAccountId());
			for (Facility facility : corporate.getOperatorFacilities()) {
				if (!facility.getOperator().isAutoApproveRelationships()) {
					approveContractor(contractor, facility.getOperator().getId());
				}
			}
		}

		// Trial Contractor Account Approval
		return saveContractor("Upgraded and Approved the Bid Only Account for " + permissions.getAccountName()
				+ " and notified contractor via email.", 73);
	}

	public String reject() throws Exception {
		contractor.setRenew(false);
		Iterator<ContractorOperator> cIterator = contractor.getNonCorporateOperators().iterator();
		while (cIterator.hasNext()) {
			ContractorOperator co = cIterator.next();
			if (co.getOperatorAccount().getId() == permissions.getAccountId()) {
				contractorOperatorDAO.remove(co);
				contractor.getOperators().remove(co);
				break;
			}
		}

		// Trial Contractor Account Rejection
		return saveContractor("Rejected Contractor for the Bid Only Account for " + permissions.getAccountName(), 75);
	}

	private String saveContractor(String summary, int templateId) throws Exception {
		contractor.incrementRecalculation();
		contractor.setAuditColumns(permissions);
		contractorAccountDAO.save(contractor);

		Note note = new Note(contractor, getUser(), summary);
		if (!Strings.isEmpty(operatorNotes)) {
			note.setBody(operatorNotes);
		}
		note.setNoteCategory(NoteCategory.OperatorChanges);
		note.setCanContractorView(true);
		note.setViewableById(permissions.getAccountId());
		noteDAO.save(note);

		if (templateId > 0) {
			try {
				// Sending a Email to the contractor for upgrade/rejection
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setTemplate(templateId);
				emailBuilder.setPermissions(permissions);
				emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
				emailBuilder.addToken("permissions", permissions);
				EmailQueue emailQueue = emailBuilder.build();
				emailQueue.setCriticalPriority();
//				emailQueue.setFromAddress((templateId == 73) ? "PICS Billing <"+EmailAddressUtils.getBillingEmail(contractor.getCurrency())+">"
//						: EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS_WITH_NAME);
				emailQueue.setFromAddress((templateId == 73) ? EmailAddressUtils.getBillingEmail(contractor.getCurrency())
						: EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS_WITH_NAME);
				emailQueue.setSubjectViewableById(permissions.getTopAccountID());
				emailQueue.setBodyViewableById(permissions.getTopAccountID());
				emailSender.send(emailQueue);
				addActionMessage(summary);
			} catch (Exception e) {
				addActionError(e.getLocalizedMessage());
			}
		}

		operatorNotes = Strings.EMPTY_STRING;

		return super.execute();
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public String getOperatorNotes() {
		return operatorNotes;
	}

	public void setOperatorNotes(String operatorNotes) {
		this.operatorNotes = operatorNotes;
	}

	public void approveContractor(ContractorAccount cAccount, int operatorID) {
		for (ContractorOperator cOperator : cAccount.getNonCorporateOperators()) {
			if (cOperator.getOperatorAccount().getId() == operatorID) {
				cOperator.setWorkStatus(ApprovalStatus.Y);
                contractorOperatorService.cascadeWorkStatusToParent(cOperator);
				cOperator.setAuditColumns(permissions);
				contractorOperatorDAO.save(cOperator);
				break;
			}
		}
	}
}
