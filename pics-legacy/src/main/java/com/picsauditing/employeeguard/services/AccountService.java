package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.employeeguard.daos.AccountEmployeeGuardDAO;
import com.picsauditing.employeeguard.services.external.BillingService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.services.models.AccountType;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.provisioning.ProductSubscriptionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * This is a stand-in for a "remote" service call
 */
public class AccountService {

	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private AccountEmployeeGuardDAO accountEmployeeGuardDAO;
	@Autowired
	private OperatorAccountDAO operatorDAO;
	@Autowired
	private BillingService billingService;
	@Autowired
	private ProductSubscriptionService productSubscriptionService;

	public AccountModel getAccountById(int accountId) {
		Account account = accountDAO.find(accountId);
		return mapAccountToAccountModel(account);
	}

	public List<AccountModel> getAccountsByIds(Collection<Integer> accountIds) {
		List<Account> accounts = accountDAO.findByIds(accountIds);
		return mapAccountsToAccountModels(accounts);
	}

	public List<AccountModel> getTopmostCorporateAccounts(final int accountId) {
		if (accountId <= 0) {
			return Collections.emptyList();
		}

		List<OperatorAccount> employeeGUARDCorporates = getEmployeeGUARDCorporates(accountId);
		// We don't have a need to modify accounts, so we'll map these corporate accounts to AccountModels
		return mapAccountsToAccountModels(employeeGUARDCorporates);
	}

	public Map<Integer, AccountModel> getIdToAccountModelMap(final Collection<Integer> accountIds) {
		return Utilities.convertToMap(getAccountsByIds(accountIds), new Utilities.MapConvertable<Integer, AccountModel>() {

			@Override
			public Integer getKey(AccountModel accountModel) {
				return accountModel.getId();
			}
		});
	}

	public List<Integer> getTopmostCorporateAccountIds(final int accountId) {
		if (accountId <= 0) {
			return Collections.emptyList();
		}

		return extractIdFromAccountModel(getEmployeeGUARDCorporates(accountId));
	}

	private List<OperatorAccount> getEmployeeGUARDCorporates(int accountId) {
		OperatorAccount operator = operatorDAO.find(accountId);
		if (operator == null) {
			return Collections.emptyList();
		}

		ArrayList<OperatorAccount> topDogs = new ArrayList<>();
		ArrayList<Integer> visited = new ArrayList<>();

		List<OperatorAccount> topmostCorporates = getTopmostCorporates(operator, topDogs, visited);
		return billingService.filterEmployeeGUARDAccounts(topmostCorporates);
	}

	private List<OperatorAccount> getTopmostCorporates(OperatorAccount operator, List<OperatorAccount> topDogs, List<Integer> visited) {
		if (onlyHasPicsConsortiumOrNoParents(operator)) {
			visited.add(operator.getId());
			topDogs.add(operator);
		} else {
			for (OperatorAccount parent : operator.getParentOperators()) {
				if (!parent.isInPicsConsortium() && !visited.contains(parent.getId())) {
					visited.add(parent.getId());
					getTopmostCorporates(parent, topDogs, visited);
				}
			}
		}

		return topDogs;
	}

	private boolean onlyHasPicsConsortiumOrNoParents(OperatorAccount operator) {
		if (operator.getParentOperators().size() == 0) {
			return true;
		}

		for (OperatorAccount parent : operator.getParentOperators()) {
			if (!parent.isInPicsConsortium()) {
				return false;
			}
		}

		return true;
	}

	public List<AccountModel> getChildOperators(final int accountId) {
		return getChildOperators(Arrays.asList(accountId));
	}

	public List<AccountModel> getChildOperators(final List<Integer> accountIds) {
		List<OperatorAccount> operators = operatorDAO.findOperators(accountIds);
		if (CollectionUtils.isEmpty(operators)) {
			return Collections.emptyList();
		}

		Set<OperatorAccount> childAccounts = new HashSet<>();
		for (OperatorAccount corporate : operators) {
			List<OperatorAccount> childOperators = new ArrayList<>(corporate.getChildOperators());
			childAccounts.addAll(billingService.filterEmployeeGUARDAccounts(childOperators));
		}

		return mapAccountsToAccountModels(new ArrayList<>(childAccounts));
	}

	public List<Integer> getChildOperatorIds(final int accountId) {
		return extractIdFromAccountModel(getChildOperators(accountId).toArray(new AccountModel[0]));
	}

	public List<Integer> getChildOperatorIds(final List<Integer> accountIds) {
		return extractIdFromAccountModel(getChildOperators(accountIds).toArray(new AccountModel[0]));
	}

	public AccountType getAccountTypeByUserID(int userID) {
		return getAccountByUserID(userID).getAccountType();
	}

	public AccountModel getAccountByUserID(int userID) {
		return getAccountById(accountDAO.findByUserID(userID));
	}

	public List<AccountModel> getContractors(final int accountId) {
		OperatorAccount operator = operatorDAO.find(accountId);
		if (operator == null) {
			return Collections.emptyList();
		}

		List<ContractorAccount> contractors = new ArrayList<>();
		addContractorsFromOperator(operator, contractors);

		if (operator.isCorporate()) {
			for (OperatorAccount site : operator.getChildOperators()) {
				addContractorsFromOperator(site, contractors);
			}
		}

		List<ContractorAccount> accounts = billingService.filterEmployeeGUARDAccounts(contractors);

		return mapAccountsToAccountModels(accounts);
	}

	public List<Integer> getContractorIds(final int accountId) {
		return extractIdFromAccountModel(getContractors(accountId).toArray(new AccountModel[0]));
	}

	private void addContractorsFromOperator(OperatorAccount operator, List<ContractorAccount> contractors) {
		for (ContractorOperator contractorOperator : operator.getContractorOperators()) {
			contractors.add(contractorOperator.getContractorAccount());
		}
	}

	private <E extends Account> List<AccountModel> mapAccountsToAccountModels(List<E> accounts) {
		if (CollectionUtils.isEmpty(accounts)) {
			return Collections.emptyList();
		}

		List<AccountModel> accountModels = new ArrayList<>(accounts.size());
		for (Account account : accounts) {
			accountModels.add(mapAccountToAccountModel(account));
		}

		return accountModels;
	}

	private AccountModel mapAccountToAccountModel(Account account) {
		return new AccountModel.Builder().accountType(getAccountTypeForAccount(account)).id(account.getId())
				.name(account.getName()).build();
	}

	private AccountType getAccountTypeForAccount(Account account) {
		switch (account.getType()) {
			case "Admin":
				return AccountType.ADMIN_ACCOUNT;

			case "Assessment":
				return AccountType.ASSESSMENT;

			case "Contractor":
				return AccountType.CONTRACTOR;

			case "Corporate":
				return AccountType.CORPORATE;

			case "Operator":
				return AccountType.OPERATOR;

			default:
				throw new IllegalArgumentException("Invalid account type " + account.getType());
		}
	}

	public List<Integer> extractIdFromAccountModel(AccountModel... accountModels) {
		if (ArrayUtils.isEmpty(accountModels)) {
			return Collections.emptyList();
		}

		List<Integer> ids = new ArrayList<>();
		for (AccountModel accountModel : accountModels) {
			ids.add(accountModel.getId());
		}

		return ids;
	}

	private List<Integer> extractIdFromAccountModel(List<? extends Account> accounts) {
		if (CollectionUtils.isEmpty(accounts)) {
			return Collections.emptyList();
		}

		List<Integer> ids = new ArrayList<>();
		for (Account account : accounts) {
			ids.add(account.getId());
		}

		return ids;
	}

	public boolean doesContractorStillNeedEmployeeGuard(final int contractorID) {
		return doesContractorStillNeedEmployeeGuard(accountDAO.find(contractorID));
	}

	private boolean doesContractorStillNeedEmployeeGuard(final Account account) {
		return productSubscriptionService.hasEmployeeGUARD(account);
	}
}
