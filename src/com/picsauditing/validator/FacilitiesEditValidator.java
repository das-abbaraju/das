package com.picsauditing.validator;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.opensymphony.xwork2.validator.ValidationException;
import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.util.Strings;

public class FacilitiesEditValidator {

	/**
	 * Called only when we want to validate all the AccountUsers for an Operator
	 * Account.
	 *
	 * Returns either a validation message or an Empty String when validation is
	 * successful.
	 */
	public String validateOwnershipPercentage(List<AccountUser> accountUsers) {
		try {
			int totalOwnershipForAccountManager = 0;
			for (AccountUser accountUser : accountUsers) {
				if (isActiveAccountManager(accountUser)) {
					validateIndividualAccountManager(accountUser);
					totalOwnershipForAccountManager += accountUser.getOwnerPercent();
				}

				if (isActiveSalesRepresentative(accountUser)) {
					validateIndividualSalesRepresentative(accountUser);
				}
			}

			if (totalOwnershipForAccountManager != 100) {
				return "All active Account Managers must have a total ownership of 100%.";
			}

		} catch (ValidationException ve) {
			return ve.getMessage();
		}

		return Strings.EMPTY_STRING;
	}

	private void validateIndividualSalesRepresentative(AccountUser salesRepresentative) throws ValidationException {
		if (salesRepresentative.getOwnerPercent() <= 0 || salesRepresentative.getOwnerPercent() > 100) {
			throw new ValidationException(
					"All Sales Representatives must have ownership greater than 0%, but not more than 100%.");
		}
	}

	private void validateIndividualAccountManager(AccountUser accountManager) throws ValidationException {
		if (accountManager.getOwnerPercent() <= 0 || accountManager.getOwnerPercent() > 100) {
			throw new ValidationException(
					"All AccountManagers must have ownership greater than 0%, but but not more than 100%.");
		}
	}

	/**
	 * Used when adding a new AccountUser to an existing account.
	 *
	 * @param accountUsers
	 *            All the existing account users
	 * @param newAccountUser
	 *            The new AccountUser
	 * @return validation message or an empty string if validation was
	 *         successful.
	 */
	public String validateOwnershipPercentage(List<AccountUser> accountUsers, AccountUser newAccountUser) {
		try {
			validateAccountUser(newAccountUser);
			validateActiveAccountUserPercentage(accountUsers, newAccountUser);
		} catch (ValidationException ve) {
			return ve.getMessage();
		}

		return Strings.EMPTY_STRING;
	}

	private void validateActiveAccountUserPercentage(List<AccountUser> accountUsers, AccountUser newAccountUser)
			throws ValidationException {
		if (newAccountUser.getRole() == UserAccountRole.PICSSalesRep) {
			return;
		} else if (CollectionUtils.isEmpty(accountUsers) && newAccountUser.getRole() == UserAccountRole.PICSAccountRep
				&& newAccountUser.getOwnerPercent() != 100) {
			throw new ValidationException("All active Account Managers must have a total ownership of 100%.");
		}

		@SuppressWarnings("unused")
		int totalOwnershipForAccountManager = newAccountUser.getOwnerPercent();
		for (AccountUser accountUser : accountUsers) {
			if (isActiveAccountManager(accountUser)) {
				totalOwnershipForAccountManager += accountUser.getOwnerPercent();
			}
		}

		// FIXME: purposely commented out, but we should be validating this way
		// once the UI is fixed
		// if (totalOwnershipForAccountManager != 100) {
		// throw new
		// ValidationException("All active Account Managers must have a total ownership of 100%.");
		// }
	}

	/**
	 * Called when removing an AccountUser.
	 *
	 * @param accountUsers
	 *            The existing List of AccountUser (including the one to
	 *            remove).
	 * @param accountUserToRemove
	 *            The AccountUser to remove
	 * @return A validation error message or an Empty String if validation is
	 *         successful.
	 */
	public String validateRemoveAccountUser(List<AccountUser> accountUsers, AccountUser accountUserToRemove) {
		if (accountUserToRemove == null) {
			return Strings.EMPTY_STRING;
		}

		try {
			validateAnotherActiveAccountManager(accountUsers, accountUserToRemove);
		} catch (ValidationException ve) {
			return ve.getMessage();
		}

		return Strings.EMPTY_STRING;
	}

	private void validateAnotherActiveAccountManager(List<AccountUser> accountUsers, AccountUser accountUserToRemove)
			throws ValidationException {
		if (accountUserToRemove.getAccount() != null && accountUserToRemove.getAccount().getStatus() != null
				&& accountUserToRemove.getAccount().getStatus().isActiveOrDemo()
				&& CollectionUtils.isEmpty(accountUsers)) {
			throw new ValidationException("Active accounts are required to have at least one active Account Manager.");
		}

		if (!hasAnotherActiveAccountUser(accountUsers, accountUserToRemove)) {
			throw new ValidationException("Active accounts are required to have at least one active Account Manager.");
		}
	}

	private boolean hasAnotherActiveAccountUser(List<AccountUser> accountUsers, AccountUser accountUserToRemove) {
		for (AccountUser existingAccountUser : accountUsers) {
			if (isActiveAccountManager(existingAccountUser)) {
				if (!existingAccountUser.equals(accountUserToRemove)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isActiveAccountManager(AccountUser accountUser) {
		return (accountUser != null && accountUser.getRole() != null
				&& accountUser.getRole() == UserAccountRole.PICSAccountRep && accountUser.isCurrent());
	}

	private boolean isActiveSalesRepresentative(AccountUser accountUser) {
		return (accountUser != null && accountUser.getRole() != null
				&& accountUser.getRole() == UserAccountRole.PICSSalesRep && accountUser.isCurrent());
	}

	private void validateAccountUser(AccountUser accountUser) throws ValidationException {
		if (accountUser == null) {
			throw new ValidationException("AccountUser must be entered.");
		}

		if (accountUser.getRole() == null) {
			throw new ValidationException("Role is missing.");
		}

		if (accountUser.getOwnerPercent() <= 0 || accountUser.getOwnerPercent() > 100) {
			throw new ValidationException("Account Representative must have an ownership greater than 0%, "
					+ "but not more than 100%.");
		}
	}

}
