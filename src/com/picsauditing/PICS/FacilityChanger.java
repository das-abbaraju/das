package com.picsauditing.PICS;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
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
import com.picsauditing.mail.EmailSenderSpring;

import edu.emory.mathcs.backport.java.util.Collections;

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
	private EmailSenderSpring emailSender;
	@Autowired
	protected AuditBuilder auditBuilder = null;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;


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

		if (permissions.isOperatorCorporate()) {
			// This could be controversial, but we're going to always approve if
			// the operator adds them
			co.setWorkStatus("Y");
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
			emailBuilder.setFromAddress("\"PICS Customer Service\"<info@picsauditing.com>");
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setPriority(60);
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

		// Need to upgrade this contractor if operator being added does not
		// accept bid only contractors or list only contractors
		if ((contractor.getAccountLevel().isBidOnly() && !operator.isAcceptsBids())
				|| (contractor.getAccountLevel().isListOnly() && !operator.isAcceptsList())) {
			contractor.setAccountLevel(AccountLevel.Full);
			contractor.setRenew(true);
			
			for (ContractorAudit cAudit : contractor.getAudits()) {
				if (cAudit.getAuditType().isPqf()) {
					for (ContractorAuditOperator cao : cAudit.getOperators()) {
						if (cao.getStatus().after(AuditStatus.Pending)) {
							cao.changeStatus(AuditStatus.Pending, permissions);
							auditDataDAO.save(cao);
						}
					}

					auditBuilder.recalculateCategories(cAudit);
					auditPercentCalculator.recalcAllAuditCatDatas(cAudit);
					auditPercentCalculator.percentCalculateComplete(cAudit);
					auditDataDAO.save(cAudit);
				}
			}
		}

		contractor.setLastUpgradeDate(new Date());
		setListOnly();
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

	public boolean remove() throws Exception {
		if (contractor == null || contractor.getId() == 0)
			throw new Exception("Please set contractor before calling remove()");
		if (operator == null || operator.getId() == 0)
			throw new Exception("Please set operator before calling remove()");

		// TODO: Start using SearchContractors.Delete instead
		// permissions.tryPermission(OpPerms.SearchContractors, OpType.Delete);
		// if (!permissions.hasPermission(OpPerms.RemoveContractors))
		// return false;
		Iterator<ContractorOperator> iterator = contractor.getNonCorporateOperators().iterator();
		while (iterator.hasNext()) {
			ContractorOperator co = iterator.next();
			if (!co.getOperatorAccount().isCorporate()) {
				if (co.getOperatorAccount().equals(operator)) {
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
						emailBuilder.setFromAddress("\"IT\"<tbaker@picsauditing.com>");
						emailBuilder.setToAddresses("billing@picsauditing.com");

						EmailQueue emailQueue = emailBuilder.build();
						emailQueue.setPriority(60);
						emailSender.send(emailQueue);
					}

					checkOQ();
					if (contractor.getNeedsRecalculation() < 20)
						contractor.incrementRecalculation(5);

					setListOnly();

					billingService.calculateAnnualFees(contractor);

					// adjusting requested by to earliest added operator
					if (contractor.getRequestedBy().equals(operator)) {
						contractor.setRequestedBy(findEarliestAddedOperator());
					}

					contractorAccountDAO.save(contractor);
					return true;
				}
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

	private void setListOnly() {
		if (contractor.isListOnlyEligible() && contractor.getStatus().isPending()
				&& contractor.getAccountLevel().isFull()) {
			boolean canBeListed = true;
			for (ContractorOperator conOp : contractor.getNonCorporateOperators()) {
				if (!conOp.getOperatorAccount().isAcceptsList())
					canBeListed = false;
			}
			if (canBeListed)
				contractor.setAccountLevel(AccountLevel.ListOnly);
		}
	}

	/**
	 * @return Returns the earliest added OperatorAccount or null if no operators are present.
	 */
	private OperatorAccount findEarliestAddedOperator() {
		LinkedList<OperatorAccount> creationDateQueue = new LinkedList<OperatorAccount>(contractor
				.getOperatorAccounts());
		Collections.sort(creationDateQueue, new Comparator<OperatorAccount>() {
			@Override
			public int compare(OperatorAccount o1, OperatorAccount o2) {
				return o1.getCreationDate().compareTo(o2.getCreationDate());
			}
		});

		return creationDateQueue.peekFirst();
	}

}
