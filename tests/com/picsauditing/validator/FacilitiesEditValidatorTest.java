package com.picsauditing.validator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.UserAccountRole;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

public class FacilitiesEditValidatorTest {

	private FacilitiesEditValidator facilitiesEditValidator;

	// list of all AccountUsers for an operator account
	private List<AccountUser> accountUsersForOperator;

	@Before
	public void setUp() throws Exception {
		accountUsersForOperator = new ArrayList<AccountUser>();
		facilitiesEditValidator = new FacilitiesEditValidator();
	}

	@Test
	public void testDefaultOwnershipIs100() {
		AccountUser accountUser = new AccountUser();

		assertEquals(100, accountUser.getOwnerPercent());
	}

	@Test
	public void testValidateOwnershipPercentage_ActiveAccountManagerNoSalesRepresentative() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator);

		assertEquals(Strings.EMPTY_STRING, validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_ActiveAccountManagerMoreThan100NoSalesRepresentative() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(101, UserAccountRole.PICSAccountRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator);

		assertEquals("All AccountManagers must have ownership greater than 0%, but but not more than 100%.",
				validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_ActiveAccountManagerLessThan100NoSalesRepresentative() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(99, UserAccountRole.PICSAccountRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator);

		assertEquals("All active Account Managers must have a total ownership of 100%.", validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_MultipleActiveAccountManagersTotal100NoSalesRepresentative() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(75, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(25, UserAccountRole.PICSAccountRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator);

		assertEquals(Strings.EMPTY_STRING, validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_ActiveAccountManagerAndSalesRepresentative() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(50, UserAccountRole.PICSSalesRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator);

		assertEquals(Strings.EMPTY_STRING, validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_ActiveAccountUserButTheOnlySalesRepHasZero() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(0, UserAccountRole.PICSSalesRep));
		accountUsersForOperator.add(buildFakeInactiveAccountUser(100, UserAccountRole.PICSSalesRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator);

		assertEquals("All Sales Representatives must have ownership greater than 0%, but not more than 100%.",
				validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_ActiveAccountUserButOneSalesRepHasZero() {
		accountUsersForOperator.add(buildFakeInactiveAccountUser(100, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSSalesRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(0, UserAccountRole.PICSSalesRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator);

		assertEquals("All Sales Representatives must have ownership greater than 0%, but not more than 100%.",
				validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_AddAnotherSalesRepFor200TotalOwnership() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSSalesRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator,
				buildFakeActiveAccountUser(100, UserAccountRole.PICSSalesRep));

		assertEquals(Strings.EMPTY_STRING, validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_AddAnotherSalesRepThatHasZeroOwnership() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSSalesRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator,
				buildFakeActiveAccountUser(0, UserAccountRole.PICSSalesRep));

		assertEquals("Account Representative must have an ownership greater than 0%, but not more than 100%.",
				validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_AddAnotherSalesRepForTotal100Ownership() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(50, UserAccountRole.PICSSalesRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator,
				buildFakeActiveAccountUser(50, UserAccountRole.PICSSalesRep));

		assertEquals(Strings.EMPTY_STRING, validationMessage);
	}

	@Ignore("This test is specifically being ignored because we are not allowing this to occur until we fix the underlying UI problem")
	@Test
	public void testValidateOwnershipPercentage_AddAnotherAccountManagerForTotalOver100OwnershipValidationError() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSSalesRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator,
				buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));

		assertEquals("All active Account Managers must have a total ownership of 100%.", validationMessage);
	}

	/**
	 * This is testing that functionality is misbehaving in the expected way.
	 * See note in Ignored test above.
	 */
	@Test
	@Deprecated
	public void testValidateOwnershipPercentage_AddAnotherAccountManagerForTotalOver100OwnershipNoValidationError() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSSalesRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator,
				buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));

		assertEquals(Strings.EMPTY_STRING, validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_RemoveOnlyActiveAccountManager() {
		AccountUser accountManagerToRemove = buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep);
		accountUsersForOperator.add(accountManagerToRemove);
		accountUsersForOperator.add(buildFakeInactiveAccountUser(100, UserAccountRole.PICSAccountRep));

		String validationMessage = facilitiesEditValidator.validateRemoveAccountUser(accountUsersForOperator,
				accountManagerToRemove);

		assertEquals("Active accounts are required to have at least one active Account Manager.", validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_RemoveOnlySalesRepresentative() {
		AccountUser salesRepresentativeToRemove = buildFakeActiveAccountUser(100, UserAccountRole.PICSSalesRep);
		accountUsersForOperator.add(buildFakeActiveAccountUser(100, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeInactiveAccountUser(100, UserAccountRole.PICSAccountRep));
		accountUsersForOperator.add(buildFakeInactiveAccountUser(100, UserAccountRole.PICSSalesRep));
		accountUsersForOperator.add(salesRepresentativeToRemove);

		String validationMessage = facilitiesEditValidator.validateRemoveAccountUser(accountUsersForOperator,
				salesRepresentativeToRemove);

		assertEquals(Strings.EMPTY_STRING, validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_AddSalesRepresentativeWithNoAccountManager() {
		AccountUser salesRepresentativeToAdd = buildFakeActiveAccountUser(50, UserAccountRole.PICSSalesRep);

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(null, salesRepresentativeToAdd);

		assertEquals(Strings.EMPTY_STRING, validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_SaveSalesRepresentativeWithNoAccountManager() {
		accountUsersForOperator.add(buildFakeActiveAccountUser(50, UserAccountRole.PICSSalesRep));
		accountUsersForOperator.add(buildFakeActiveAccountUser(50, UserAccountRole.PICSSalesRep));

		String validationMessage = facilitiesEditValidator.validateOwnershipPercentage(accountUsersForOperator);

		assertEquals(Strings.EMPTY_STRING, validationMessage);
	}

	@Test
	public void testValidateOwnershipPercentage_RemoveSalesRepresentativeWithNoAccountManager() {
		AccountUser accountUserToRemove = buildFakeActiveAccountUser(50, UserAccountRole.PICSSalesRep);
		accountUsersForOperator.add(buildFakeActiveAccountUser(50, UserAccountRole.PICSSalesRep));
		accountUsersForOperator.add(accountUserToRemove);

		String validationMessage = facilitiesEditValidator.validateRemoveAccountUser(accountUsersForOperator, accountUserToRemove);

		assertEquals(Strings.EMPTY_STRING, validationMessage);
	}

	private AccountUser buildFakeActiveAccountUser(int ownershipPercent, UserAccountRole role) {
		AccountUser accountUser = buildFakeAccountUser(ownershipPercent, role);
		accountUser.setStartDate(DateBean.addDays(new Date(), -1));
		accountUser.setEndDate(DateBean.addDays(new Date(), 1));
		accountUser.setUser(new User(123));

		Account account = new Account();
		account.setId(1);
		accountUser.setAccount(account);

		return accountUser;
	}

	@SuppressWarnings("deprecation")
	private AccountUser buildFakeInactiveAccountUser(int ownershipPercent, UserAccountRole role) {
		AccountUser accountUser = buildFakeAccountUser(ownershipPercent, role);
		accountUser.setStartDate(new Date("12/1/2011"));
		accountUser.setEndDate(new Date("12/1/2012"));
		return accountUser;
	}

	private AccountUser buildFakeAccountUser(int ownershipPercent, UserAccountRole role) {
		AccountUser accountUser = new AccountUser();
		accountUser.setOwnerPercent(ownershipPercent);
		accountUser.setRole(role);
		return accountUser;
	}

}
