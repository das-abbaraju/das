package com.picsauditing.PICS;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorRelationshipType;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.EmailAddressUtils;

/**
 * Adds and removed contractors from operator accounts
 */
public class FacilityChanger {
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private BillingCalculatorSingle billingService;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	protected AuditBuilder auditBuilder = null;
	@Autowired
	private AccountLevelAdjuster accountLevelAdjuster;

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private Permissions permissions;
	private User user;

	public void add() throws Exception {
		if (contractor == null || contractor.getId() == 0)
			throw new Exception("Please set contractor before calling add()");
		if (operator == null || operator.getId() == 0)
			throw new Exception("Please set operator before calling add()");

		for (ContractorOperator conOperator : contractor.getNonCorporateOperators()) {
			if (conOperator.getOperatorAccount().equals(operator))
				return;
		}
		// TODO: Start using SearchContractors.Edit instead
		// permissions.tryPermission(OpPerms.SearchContractors, OpType.Edit);

		ContractorOperator co = new ContractorOperator();
		co.setContractorAccount(contractor);
		co.setOperatorAccount(operator);
		co.setType(ContractorOperatorRelationshipType.ContractorOperator);

		if (addedByOperatorCorporate() || operator.isAutoApproveRelationships()) {
			// This could be controversial, but we're going to always approve if
			// the operator adds them
			co.setWorkStatus(ApprovalStatus.Y);
		}

		if (operator.isGeneralContractor()) {
			if (contractor.isAutoApproveRelationships()) {
				co.setWorkStatus(ApprovalStatus.Y);
			} else {
				co.setWorkStatus(ApprovalStatus.C);
				sendGCApprovalEmailToContractor();
			}
		}

		co.setAuditColumns(permissions);
		co.setWaitingOn(WaitingOn.Contractor);
		co.setFlagColor(FlagColor.Red);
		contractorOperatorDAO.save(co);
		contractor.getOperators().add(co);

		addNote("Linked contractor to " + operator.getName());

		if (!permissions.isContractor()) {
			// Send the contractor an email that the operator added them
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(9); // Contractor Added
			emailBuilder.setPermissions(permissions);
			emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
			emailBuilder.addToken("operator", operator);
			emailBuilder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setHighPriority();
			emailQueue.setViewableBy(operator.getTopAccount());
			emailSender.send(emailQueue);
		}

		if (contractor.getAccountLevel().isFull()) {
			if (permissions.isContractor()) {
				// If the contractor logs in and adds a facility,
				// then let's assume they want to be part of PICS
				contractor.setRenew(true);
			}
		}

		// Note: operator field is the operator being added
		if ((contractor.getAccountLevel().isBidOnly() && !operator.isAcceptsBids())
				|| (contractor.getAccountLevel().isListOnly() && !operator.isAcceptsList())) {
			accountLevelAdjuster.upgradeToFullAccount(contractor, permissions);
		}

		contractor.setLastUpgradeDate(new Date());
		accountLevelAdjuster.setListOnlyIfPossible(contractor);
		contractorAccountDAO.save(contractor);

		checkOQ();

		if (contractor.getNeedsRecalculation() < 20)
			contractor.incrementRecalculation(5);
		if (contractor.getRequestedBy() == null) {
			contractor.setRequestedBy(findEarliestAddedOperator());
		}

		billingService.calculateAnnualFees(contractor);
		contractorAccountDAO.save(contractor);
	}

	private void sendGCApprovalEmailToContractor() throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(238); // Contractor needs to Approve GC
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
		emailBuilder.addToken("operator", operator);
		emailBuilder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
		EmailQueue emailQueue = emailBuilder.build();
		emailQueue.setPriority(60);
		emailQueue.setViewableBy(operator.getTopAccount());
		emailSender.send(emailQueue);
	}

	public boolean remove() throws Exception {
		if (contractor == null || contractor.getId() == 0)
			throw new Exception("Please set contractor before calling remove()");
		if (operator == null || operator.getId() == 0)
			throw new Exception("Please set operator before calling remove()");
		if (permissions.isContractor() && !permissions.getAccountStatus().equals(AccountStatus.Pending))
			throw new Exception("Only pending contractors can remove()");

		// TODO: Start using SearchContractors.Delete instead
		// permissions.tryPermission(OpPerms.SearchContractors, OpType.Delete);
		// if (!permissions.hasPermission(OpPerms.RemoveContractors))
		// return false;
		Iterator<ContractorOperator> iterator = contractor.getOperators().iterator();
		while (iterator.hasNext()) {
			ContractorOperator co = iterator.next();
			if (!co.getOperatorAccount().isCorporate() && co.getOperatorAccount().equals(operator)) {
				contractorOperatorDAO.remove(co);
				contractor.getOperators().remove(co);

				addNote("Unlinked " + co.getContractorAccount().getName() + " from "
						+ co.getOperatorAccount().getName() + "'s db");

				// If user is a non-billing user, notify billing to
				// adjust invoice
				if (!permissions.isContractor() && !permissions.hasGroup(958)
						&& !co.getContractorAccount().getAccountLevel().isBidOnly()) { // Billing/Accounting
					EmailBuilder emailBuilder = new EmailBuilder();
					emailBuilder.setTemplate(47); // Notice of Facility Rem
					emailBuilder.setPermissions(permissions);
					emailBuilder.setContractor(co.getContractorAccount(), OpPerms.ContractorAdmin);
					emailBuilder.addToken("operator", co.getOperatorAccount());
					emailBuilder.setFromAddress(EmailAddressUtils.PICS_ERROR_EMAIL_ADDRESS_WITH_NAME);
					emailBuilder.setToAddresses(EmailAddressUtils.getBillingEmail(contractor.getCurrency()));

					EmailQueue emailQueue = emailBuilder.build();
					emailQueue.setHighPriority();
					emailSender.send(emailQueue);
				}

				checkOQ();
				if (contractor.getNeedsRecalculation() < 20)
					contractor.incrementRecalculation(5);

				accountLevelAdjuster.setListOnlyIfPossible(contractor);

				billingService.calculateAnnualFees(contractor);

				// adjusting requested by to earliest added operator
				if (contractor.getRequestedBy() == null || contractor.getRequestedBy().equals(operator)) {
					contractor.setRequestedBy(findEarliestAddedOperator());
				}

				contractorAccountDAO.save(contractor);
				return true;
			}
		}

		return false;
	}

	private void addNote(String summary) {
		// TODO I think we should add a new column on
		// operator table that is the viewableBy default
		OperatorAccount viewableBy = operator;
		for (Facility f : operator.getCorporateFacilities()) {
			viewableBy = f.getCorporate();
		}

		Note note = new Note(contractor, user, summary);
		note.setNoteCategory(NoteCategory.OperatorChanges);
		note.setCanContractorView(true);
		note.setViewableBy(viewableBy);
		noteDAO.save(note);

	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public void setContractor(int id) {
		this.contractor = contractorAccountDAO.find(id);
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public void setOperator(int id) {
		this.operator = operatorAccountDAO.find(id);
	}

	public Permissions getPermissions() {
		return permissions;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
		user = new User(permissions.getUserId());
	}

	private void checkOQ() {
		boolean requiresOQ = false;
		boolean requiresCompetency = false;
		for (ContractorOperator co1 : contractor.getOperators()) {
			if (co1.getOperatorAccount().isRequiresOQ())
				requiresOQ = true;
			if (co1.getOperatorAccount().isRequiresCompetencyReview())
				requiresCompetency = true;
		}

		contractor.setRequiresOQ(false);
		if (requiresOQ) {
			AuditData oqAuditData = auditDataDAO
					.findAnswerByConQuestion(contractor.getId(), AuditQuestion.OQ_EMPLOYEES);
			contractor.setRequiresOQ(oqAuditData == null || oqAuditData.getAnswer() == null
					|| oqAuditData.getAnswer().equals("Yes"));
		}

		contractor.setRequiresCompetencyReview(false);
		if (requiresCompetency) {
			for (ContractorTag tag : contractor.getOperatorTags()) {
				if (tag.getTag().getId() == OperatorTag.SHELL_COMPETENCY_REVIEW)
					contractor.setRequiresCompetencyReview(true);
			}
		}
	}

	/**
	 * @return Returns the earliest added OperatorAccount or null if no
	 *         operators are present.
	 */
	private OperatorAccount findEarliestAddedOperator() {
		LinkedList<OperatorAccount> creationDateQueue = new LinkedList<OperatorAccount>(
				contractor.getOperatorAccounts());
		Collections.sort(creationDateQueue, new Comparator<OperatorAccount>() {
			@Override
			public int compare(OperatorAccount o1, OperatorAccount o2) {
				return o1.getCreationDate().compareTo(o2.getCreationDate());
			}
		});

		return creationDateQueue.peekFirst();
	}

	private boolean addedByOperatorCorporate() {
		if (permissions.isOperator() && permissions.getAccountId() == operator.getId())
			return true;

		if (permissions.isCorporate() && permissions.getOperatorChildren().contains(operator.getId()))
			return true;

		return false;
	}

	public boolean requiresPopUp() {
		if (operator.isGeneralContractor() && operator.getLinkedClientSites().size() > 0) {
			return true;
		}
		if (!operator.isGeneralContractor() && operator.getLinkedGeneralContractorOperatorAccounts().size() > 0) {
			return true;
		}

		return false;
	}

	public List<OperatorAccount> getPopUpContent() {
		if (operator.isGeneralContractor()) {
			return operator.getLinkedClientSites();
		} else {
			return operator.getLinkedGeneralContractorOperatorAccounts();
		}
	}
}
