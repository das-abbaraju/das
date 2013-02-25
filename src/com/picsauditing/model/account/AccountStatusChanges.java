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
	// All of the following reasons are offered up in a drop-down presented to
	// the user by ReportFilterContractor
	// .getDeactivationReasons(). Some of them are also automatically set by the
	// system.
	public static final String BID_ONLY_ACCOUNT_REASON = "Bid Only Account";
	public static final String CHARGEBACK_REASON = "ChargeBack";
	public static final String DID_NOT_COMPLETE_PICS_PROCESS_REASON = "Did not Complete PICS process";
	public static final String DOES_NOT_WORK_FOR_OPERATOR_REASON = "Does not work for operator";
	public static final String DUPLICATE_MERGED_ACCOUNT_REASON = "Duplicate/Merged Account";
	public static final String OPERATOR_EXEMPTION_REASON = "Operator Exemption";
	public static final String PAYMENTS_NOT_CURRENT_REASON = "Payments not Current";

	// The following reasons are only automatically set by the system.
	public static final String DEACTIVATED_NON_RENEWAL_ACCOUNT_REASON = "Deactivated non-renewal account";
	public static final String DEACTIVATED_PENDING_ACCOUNT_REASON = "Deactivated pending account";
	public static final String OPERATOR_MANUALLY_DEACTIVATED_REASON = "Operator manually deactivated";
	public static final String ACCOUNT_EXPIRED_REASON = "Account expired";
	public static final String ACCOUNT_ABOUT_TO_EXPIRE_REASON = "Account about to expire";

	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private NoteDAO noteDAO;

	public void deactivateClientSite(OperatorAccount clientSite, Permissions permissions, String deactivationReason,
			String notation) {
		validate(clientSite, notation);

		setDeactivationStatus(clientSite, permissions, deactivationReason);
		noteDeactivation(clientSite, notation);
		accountDAO.save(clientSite);
	}

	public void deactivateContractor(ContractorAccount contractor, Permissions permissions, String deactivationReason,
			String notation) {
		validate(contractor, notation);

		setDeactivationStatus(contractor, permissions, deactivationReason);
		contractor.setRenew(false);
		noteDeactivation(contractor, notation);
		accountDAO.save(contractor);
	}

	private void validate(Account account, String notation) {
		if (account == null) {
			throw new IllegalArgumentException("Account is null. You must provide a valid Account to deactivate.");
		}

		if (Strings.isEmpty(notation)) {
			throw new IllegalArgumentException("A deactivation reason note must be provided to deactivate an account.");
		}
	}

	private void setDeactivationStatus(Account account, Permissions permissions, String deactivationReason) {
		account.setReason(deactivationReason);
		account.setStatus(AccountStatus.Deactivated);

		if (permissions == null)
			account.setDeactivatedBy(new User(User.SYSTEM));
		else
			account.setDeactivatedBy(new User(permissions.getUserId()));

		account.setDeactivationDate(new Date());
	}

	private void noteDeactivation(Account account, String notation) {
		Note note = new Note(account, new User(User.SYSTEM), notation);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

}
