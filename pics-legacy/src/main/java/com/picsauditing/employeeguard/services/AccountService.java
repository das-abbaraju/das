package com.picsauditing.employeeguard.services;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.external.BillingService;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.AccountType;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
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
	private ContractorAccountDAO contractorDAO;
	@Autowired
	private OperatorAccountDAO operatorDAO;
	@Autowired
	private BillingService billingService;

	public AccountModel getAccountById(int accountId) {
		Account account = accountDAO.find(accountId);
		return mapAccountToAccountModel(account);
	}

	public List<AccountModel> getAccountsByIds(Collection<Integer> accountIds) {
		List<Account> accounts = accountDAO.findByIds(accountIds);
		return mapAccountsToAccountModels(accounts);
	}

  public Collection<Integer> extractParentAccountIds(final int accountId) {
    List<AccountModel> accountModels = this.extractParentAccounts(accountId);
    Collection<Integer> parentSiteIds=PicsCollectionUtil.getIdsFromCollection(accountModels, new PicsCollectionUtil.Identitifable<AccountModel, Integer>() {
      @Override
      public Integer getId(AccountModel accountModel) {
        return accountModel.getId();
      }
    });

    return parentSiteIds;
  }

	public List<AccountModel> extractParentAccounts(final int accountId) {
		if (accountId <= 0) {
			throw new IllegalArgumentException("Invalid account ID: " + accountId);
		}

		List<OperatorAccount> employeeGUARDCorporates = getEmployeeGUARDCorporates(Arrays.asList(accountId));
		// We don't have a need to modify accounts, so we'll map these corporate accounts to AccountModels
		return mapAccountsToAccountModels(employeeGUARDCorporates);
	}

	public Map<Integer, AccountModel> getIdToAccountModelMap(final Collection<Integer> accountIds) {
		return PicsCollectionUtil.convertToMap(getAccountsByIds(accountIds), new PicsCollectionUtil.MapConvertable<Integer, AccountModel>() {

			@Override
			public Integer getKey(AccountModel accountModel) {
				return accountModel.getId();
			}
		});
	}

	public List<Integer> getTopmostCorporateAccountIds(final Collection<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		return extractIdFromAccountModel(getEmployeeGUARDCorporates(accountIds));
	}

	public List<Integer> getTopmostCorporateAccountIds(final int accountId) {
		if (accountId <= 0) {
			throw new IllegalArgumentException("Invalid account ID: " + accountId);
		}

		return extractIdFromAccountModel(getEmployeeGUARDCorporates(Arrays.asList(accountId)));
	}

	private List<OperatorAccount> getEmployeeGUARDCorporates(Collection<Integer> accountIds) {
		List<OperatorAccount> operators = operatorDAO.findOperators(new ArrayList<>(accountIds));

		ArrayList<Integer> visited = new ArrayList<>();

		Set<OperatorAccount> corporates = new HashSet<>();
		for (OperatorAccount operator : operators) {
			List<OperatorAccount> topmostCorporates = getTopmostCorporates(operator, visited);
			corporates.addAll(billingService.filterEmployeeGUARDAccounts(topmostCorporates));
		}

		return new ArrayList<>(corporates);
	}

	public Map<Integer, Set<Integer>> getSiteToCorporatesMap(final Collection<Integer> siteIds) {
		if (CollectionUtils.isEmpty(siteIds)) {
			return Collections.emptyMap();
		}

		List<OperatorAccount> sites = operatorDAO.findOperators(new ArrayList<>(siteIds));

		Map<Integer, Set<Integer>> siteToCorporates = new HashMap<>();
		for (OperatorAccount site : sites) {
			ArrayList<Integer> visited = new ArrayList<>();
			List<OperatorAccount> topmostCorporates = getTopmostCorporates(site, visited);

			siteToCorporates.put(site.getId(), new HashSet<Integer>());

			for (OperatorAccount corporate : topmostCorporates) {
				siteToCorporates.get(site.getId()).add(corporate.getId());
			}
		}

		return siteToCorporates;
	}

	private List<OperatorAccount> getTopmostCorporates(OperatorAccount operator, List<Integer> visited) {
		List<OperatorAccount> corporates = new ArrayList<>();
		if (onlyHasPicsConsortiumOrNoParents(operator)) {
			visited.add(operator.getId());
			corporates.add(operator);
		} else {
			for (OperatorAccount parent : operator.getParentOperators()) {
				if (!parent.isInPicsConsortium() && !visited.contains(parent.getId())) {
					visited.add(parent.getId());
					corporates.addAll(getTopmostCorporates(parent, visited));
				}
			}
		}

		return corporates;
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
		List<OperatorAccount> corporates = operatorDAO.findOperators(accountIds);
		if (CollectionUtils.isEmpty(corporates)) {
			return Collections.emptyList();
		}

		Set<OperatorAccount> childAccounts = new HashSet<>();
		for (OperatorAccount corporate : corporates) {
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

	public AccountModel getAccountByUserID(int userID) {
		return getAccountById(accountDAO.findByUserID(userID));
	}

	public Map<Integer, AccountModel> getContractorMapForSite(final int siteId) {
		return PicsCollectionUtil.convertToMap(getContractors(siteId), new PicsCollectionUtil.MapConvertable<Integer, AccountModel>() {
			@Override
			public Integer getKey(AccountModel accountModel) {
				return accountModel.getId();
			}
		});
	}

	/**
	 * Retrieve the list of Contractor IDs that work under this Operator/Corporate hierarchy and have EmployeeGUARD.
	 *
	 * @param siteId The ID of the Operator Facility/Site or the ID of the Corporation
	 * @return
	 */
	public List<Integer> getContractorIds(final int siteId) {
		return extractIdFromAccountModel(getContractors(siteId).toArray(new AccountModel[0]));
	}

	public List<Integer> getContractorIds(final Collection<Integer> siteIds) {
		if (CollectionUtils.isEmpty(siteIds)) {
			return Collections.emptyList();
		}

		return contractorDAO.findAllContractorIdsForOperatorIds(siteIds);
	}

	/**
	 * Retrieve the list of Contractors that work under this Operator/Corporate hierarchy and have EmployeeGUARD.
	 *
	 * @param siteId The ID of the Operator Facility/Site or the ID of the Corporation
	 * @return
	 */
	public List<AccountModel> getContractors(final int siteId) {
		OperatorAccount operator = operatorDAO.find(siteId);
		if (operator == null) {
			return Collections.emptyList();
		}

		List<ContractorAccount> contractors = new ArrayList<>();
		if (!operator.isCorporate()) {
			contractors = contractorDAO.findAllContractorsForOperator(operator);
		} else {
			for (OperatorAccount site : operator.getChildOperators()) {
				addContractorsFromOperator(site, contractors);
			}
		}

		List<ContractorAccount> accounts = billingService.filterEmployeeGUARDAccounts(contractors);

		return mapAccountsToAccountModels(accounts);
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

	public List<Integer> extractIdFromAccountModel(final AccountModel... accountModels) {
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

	public Map<Integer, AccountModel> getContractorsForEmployee(final Employee employee) {
		if (employee == null) {
			return Collections.emptyMap();
		}

		if (employee.getProfile() != null) {
			return getContractorMapForProfile(employee.getProfile());
		}

		return getIdToAccountModelMap(Arrays.asList(employee.getAccountId()));
	}

	public Collection<AccountModel> getContractorsForProfile(final Profile profile) {
		if (profile == null) {
			return Collections.emptyList();
		}

		List<Integer> contractorIds = ExtractorUtil.extractList(profile.getEmployees(),
				new Extractor<Employee, Integer>() {
					@Override
					public Integer extract(Employee employee) {
						return employee.getAccountId();
					}
				});

		return getAccountsByIds(contractorIds);
	}

	public Map<Integer, AccountModel> getContractorMapForProfile(final Profile profile) {
		if (profile == null) {
			return Collections.emptyMap();
		}

		List<Integer> contractorIds = ExtractorUtil.extractList(profile.getEmployees(),
				new Extractor<Employee, Integer>() {
					@Override
					public Integer extract(Employee employee) {
						return employee.getAccountId();
					}
				});

		return getIdToAccountModelMap(contractorIds);
	}

	public Map<Integer, AccountModel> getContractorsForEmployeesMap(final List<Employee> employees) {
		return getIdToAccountModelMap(getAccountIds(employees));
	}

	private List<Integer> getAccountIds(final List<Employee> employees) {
		List<Integer> accountIds = new ArrayList<>();
		for (Employee employee : employees) {
			accountIds.add(employee.getAccountId());
		}

		return accountIds;
	}

	public Map<Integer, AccountModel> getOperatorMapForContractors(final Collection<Integer> contractorIds) {
		return getIdToAccountModelMap(getOperatorIdsForContractors(contractorIds));
	}

	public List<AccountModel> getOperatorsForContractors(final Collection<Integer> contractorIds) {
		return getAccountsByIds(getOperatorIdsForContractors(contractorIds));
	}

	public List<Integer> getOperatorIdsForContractors(final Collection<Integer> contractorIds) {
		List<ContractorAccount> contractors = contractorDAO.findByIDs(ContractorAccount.class, contractorIds);
		if (CollectionUtils.isEmpty(contractors)) {
			return Collections.emptyList();
		}

		return operatorDAO.findAllOperatorsForContractors(contractors);
	}

	public List<Integer> getOperatorIdsForContractor(final int contractorId) {
		ContractorAccount contractor = contractorDAO.find(contractorId);
		if (contractor == null) {
			return Collections.emptyList();
		}

		return operatorDAO.findAllOperatorsForContractor(contractor);
	}
}
