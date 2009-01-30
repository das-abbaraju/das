package com.picsauditing.PICS;

import java.util.Iterator;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
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
	private AuditBuilder auditBuilder;

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private Permissions permissions;

	public FacilityChanger(ContractorAccountDAO contractorAccountDAO, OperatorAccountDAO operatorAccountDAO,
			ContractorOperatorDAO contractorOperatorDAO, AuditBuilder auditBuilder) {
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.auditBuilder = auditBuilder;
		this.contractorAccountDAO = contractorAccountDAO;
		this.operatorAccountDAO = operatorAccountDAO;
	}

	public void add() throws Exception {
		if (contractor == null || contractor.getId() == 0)
			throw new Exception("Please set contractor before calling add()");
		if (operator == null || operator.getId() == 0)
			throw new Exception("Please set operator before calling add()");

		for (ContractorOperator conOperator : contractor.getOperators()) {
			if (conOperator.getOperatorAccount().equals(operator))
				return;
		}
		// TODO: Start using SearchContractors.Edit instead
		// permissions.tryPermission(OpPerms.SearchContractors, OpType.Edit);

		ContractorOperator co = new ContractorOperator();
		co.setContractorAccount(contractor);
		co.setOperatorAccount(operator);
		co.setAuditColumns(new User(permissions.getUserId()));
		contractorOperatorDAO.save(co);
		contractor.getOperators().add(co);

		ContractorBean.addNote(contractor.getId(), permissions, "Added contractor to " + operator.getName());

		// Send the contractor an email that the operator added them
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(9); // Contractor Added
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor);
		emailBuilder.addToken("operator", operator);
		EmailQueue emailQueue = emailBuilder.build();
		emailQueue.setPriority(80);
		EmailSender.send(emailQueue);
		auditBuilder.buildAudits(contractor);
	}

	public boolean remove() throws Exception {
		if (contractor == null || contractor.getId() == 0)
			throw new Exception("Please set contractor before calling remove()");
		if (operator == null || operator.getId() == 0)
			throw new Exception("Please set operator before calling remove()");

		// TODO: Start using SearchContractors.Delete instead
		// permissions.tryPermission(OpPerms.SearchContractors, OpType.Delete);
		//if (!permissions.hasPermission(OpPerms.RemoveContractors))
		//	return false;
		Iterator<ContractorOperator> iterator = contractor.getOperators().iterator();
		while(iterator.hasNext()) {
			ContractorOperator co = iterator.next();
			if (co.getOperatorAccount().equals(operator)) {
				contractorOperatorDAO.remove(co);
				contractor.getOperators().remove(co);
				contractor.addNote(permissions, "Removed " + co.getContractorAccount().getName()
						+ " from " + co.getOperatorAccount().getName() + "'s db");
				auditBuilder.buildAudits(contractor);
				contractorAccountDAO.save(contractor);
				return true;
			}
		}

		return false;
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
	}

}
