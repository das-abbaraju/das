package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.hql.internal.ast.tree.FromClause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;

@SuppressWarnings("serial")
public class CopyContractorInfo extends AccountActionSupport {
	private static Logger logger = LoggerFactory.getLogger(DataConversionRequestAccount.class);

	private ContractorAccount fromRequestedContractor;
	private ContractorAccount toContractorAccount;

	public String execute() {
		try {
			copyOperators();
			copyUsers();
			copyNotes();
			copyTags();

			dao.save(toContractorAccount);
		} catch (Exception e) {
			logger.error("Error in contrctor copy", e);
			addActionError(e.getLocalizedMessage());

		}

		return SUCCESS;
	}

	private void copyOperators() {
		List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
		operators = fromRequestedContractor.getOperatorAccounts();

		for (OperatorAccount operator : operators) {
			if (!toContractorAccount.getOperatorAccounts().contains(operator)) {
				ContractorOperator conOp = new ContractorOperator();
				conOp.setContractorAccount(toContractorAccount);
				conOp.setOperatorAccount(operator);

				conOp.setFlagColor(FlagColor.Clear);
				conOp.setAuditColumns();

				conOp = (ContractorOperator) dao.save(conOp);
			}
		}
	}

	private void copyUsers() {
		List<User> users = new ArrayList<User>();
		users = fromRequestedContractor.getUsers();

		for (User user : users) {
			User newUser = new User();
			newUser.setName("COPIED-" + user.getName());
			newUser.setEmail(user.getEmail());
			newUser.setPhone(user.getPhone());
			newUser.setUsername("COPIED-" + user.getEmail());
			newUser.setAccount(toContractorAccount);
			newUser.setIsGroup(YesNo.No);
			newUser.setAuditColumns();

			newUser = userDAO.save(newUser);
		}
	}

	private void copyNotes() {
		List<Note> notes = new ArrayList<Note>();
		notes = noteDao.findWhere(fromRequestedContractor.getId(), "", 1000);

		for (Note note : notes) {
			Note newNote = addNote(toContractorAccount, "Copied From Requested Contractor", note.getNoteCategory(),
					note.getPriority(), note.isCanContractorView(), note.getViewableBy().getId(),
					dao.find(User.class, User.SYSTEM));
			newNote.setBody(note.getBody());
			dao.save(newNote);
		}
	}

	private void copyTags() {
		List<ContractorTag> tags = new ArrayList<ContractorTag>();
		tags = fromRequestedContractor.getOperatorTags();
		for (ContractorTag tag : tags) {
			if (!toContractorAccount.getOperatorTags().contains(tag)) {
				ContractorTag newTag = new ContractorTag();
				newTag.setContractor(toContractorAccount);
				newTag.setTag(tag.getTag());
				newTag.setAuditColumns(permissions);

				dao.save(newTag);
				toContractorAccount.getOperatorTags().add(newTag);
			}
		}
	}

	public ContractorAccount getFromRequestedContractor() {
		return fromRequestedContractor;
	}

	public void setFromRequestedContractor(ContractorAccount fromRequestedContractor) {
		this.fromRequestedContractor = fromRequestedContractor;
	}

	public ContractorAccount getToContractorAccount() {
		return toContractorAccount;
	}

	public void setToContractorAccount(ContractorAccount toContractorAccount) {
		this.toContractorAccount = toContractorAccount;
	}

}
