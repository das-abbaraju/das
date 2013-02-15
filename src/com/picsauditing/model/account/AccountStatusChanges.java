package com.picsauditing.model.account;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

public class AccountStatusChanges {

	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private NoteDAO noteDAO;

	public void deactivateClientSite(OperatorAccount clientSite, Permissions permissions, String deactivationReason) {
		validation(clientSite, deactivationReason);

		setDeactivationStatus(clientSite, permissions);
		noteDeactivation(clientSite, deactivationReason);
		accountDAO.save(clientSite);
	}

	public void deactivateContractor(ContractorAccount contractor, Permissions permissions, String deactivationReason) {
		validation(contractor, deactivationReason);

		setDeactivationStatus(contractor, permissions);
		contractor.setRenew(false);
		noteDeactivation(contractor, deactivationReason);
		accountDAO.save(contractor);
	}

	private void validation(Account account, String deactivationReason) {
		if (account == null) {
			throw new IllegalArgumentException("Account is null. You must provide a valid Account to deactivate.");
		}

		if (Strings.isEmpty(deactivationReason)) {
			throw new IllegalArgumentException("Deactivation reason must be provided to deactivate an account.");
		}
	}

	private void setDeactivationStatus(Account account, Permissions permissions) {
		account.setStatus(AccountStatus.Deactivated);
		account.setDeactivatedBy(new User(permissions.getUserId()));
		account.setDeactivationDate(new Date());
	}

	private void noteDeactivation(Account account, String deactivationReason) {
		Note note = new Note(account, new User(User.SYSTEM), deactivationReason);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

}
