package com.picsauditing.PICS;

import java.util.Date;
import java.util.Iterator;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

/**
 * Adds and removed contractors from operator accounts
 * 
 * @author Trevor
 */
public class FacilityChanger {
	private ContractorOperatorDAO contractorOperatorDAO;
	private ContractorAccountDAO contractorAccountDAO;
	private OperatorAccountDAO operatorAccountDAO;
	private NoteDAO noteDAO;
	private AuditBuilder auditBuilder;

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private Permissions permissions;
	private User user;

	public FacilityChanger(ContractorAccountDAO contractorAccountDAO, OperatorAccountDAO operatorAccountDAO,
			ContractorOperatorDAO contractorOperatorDAO, NoteDAO noteDAO, AuditBuilder auditBuilder) {
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.contractorAccountDAO = contractorAccountDAO;
		this.operatorAccountDAO = operatorAccountDAO;
		this.noteDAO = noteDAO;
		this.auditBuilder = auditBuilder;
	}

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
			EmailSender.send(emailQueue);
		}

		if (!contractor.isAcceptsBids()) {
			if (permissions.isContractor()) {
				// If the contractor logs in and adds a facility,
				// then let's assume they want to be part of PICS
				contractor.setRenew(true);
			}
			if (contractor.getStatus().isActiveDemo()) {
				for (ContractorAudit cAudit : contractor.getAudits()) {
					if (cAudit.getAuditType().isPqf()) {
						auditBuilder.fillAuditCategories(cAudit, true);
					}
				}
			}
		}

		contractor.setLastUpgradeDate(new Date());

		contractor.setNeedsRecalculation(1);

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

					contractor.setNeedsRecalculation(1);

					contractorAccountDAO.save(contractor);

					if (!contractor.isAcceptsBids() && contractor.getStatus().isActiveDemo()) {
						for (ContractorAudit cAudit : contractor.getAudits()) {
							if (cAudit.getAuditType().isPqf()) {
								auditBuilder.fillAuditCategories(cAudit, true);
							}
						}
					}
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
}
