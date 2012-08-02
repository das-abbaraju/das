package com.picsauditing.salecommission.service.strategy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

public class ActivateInvoiceCommissionStrategy extends AbstractInvoiceCommissionStrategy {

//	private static final ContractorAccountDAO contractorAccountDAO = SpringUtils.getBean("ContractorAccountDAO");	
//	private static final BillingCalculatorSingle billingService = SpringUtils.getBean("BillingCalculatorSingle");
	
//	@Autowired
//	private ContractorAccountDAO contractorAccountDAO;
//	@Autowired
//	private BillingCalculatorSingle billingService;
	
	final static Logger logger = LoggerFactory.getLogger(ActivateInvoiceCommissionStrategy.class); 

	private void something() {
		/**
		 * 1) Get all Operator Sites
		 * 2) Get all Account Users
		 * 3) Remove inactive Account Users
		 * 4) Get Factors for Account Users
		 * 5) Calculate Revenue Percentage
		 * 6) Build Invoice Commissions
		 *    - for each iteration, calculate revenue percentage and points  
		 */
	}
	
	@Override
	public List<InvoiceCommission> calculateInvoiceCommission(Invoice invoice) {
		logger.trace("Entered DefaultInvoiceCommissionStrategy.calculateInvoiceCommission()");

		if(invoice == null) {
			logger.warn("Can't calculate null invoice!");
			return Collections.emptyList();
		}
		
		// find account users
		List<AccountUser> accountUsers = new ArrayList<AccountUser>();
		ContractorAccount contractor = (ContractorAccount) invoice.getAccount();
		List<OperatorAccount> operators = contractor.getOperatorAccounts();   // client sites
        		
		for(OperatorAccount op : operators) {
			accountUsers.addAll(op.getAccountUsers());
		}
		
		removeInactiveAccountUsers(accountUsers);
		
		int numberAMs = countNumberOfAccountManagers(accountUsers);
		int numberSRs = countNumberOfSalesRepresentatives(numberAMs, accountUsers == null ? 0 : accountUsers.size());
		
		float accountManagerFactor = calculateFactor(numberAMs);
		float salesRepresentativeFactor = calculateFactor(numberSRs);
		
		// Count number of AMs and SRs
//		int numberAMs = 0;
//		int numberSRs = 0;
//		for(AccountUser accountUser : accountUsers) {
//			if(accountUser.getRole().isAccountManager()) {
//				numberAMs++;
//			}
//			else {
//				numberSRs++;
//			}
//		}
//		
//		float accountManagerFactor = 0;
//		float salesRepresentativeFactor = 0;
//		try {
//			accountManagerFactor = 1.0f / numberAMs;
//			salesRepresentativeFactor = 1.0f / numberSRs;
//		}
//		catch (Exception e) {
//			logger.error("Divided by zero exception", e);
//		}
		
		return buildInvoiceCommissions(invoice, accountUsers, accountManagerFactor, salesRepresentativeFactor);
	}

	private List<InvoiceCommission> buildInvoiceCommissions(Invoice invoice, List<AccountUser> accountUsers, float accountManagerFactor, float salesRepresentativeFactor) {
		List<InvoiceCommission> invoiceCommissions = new ArrayList<InvoiceCommission>(accountUsers.size());
		InvoiceCommission invoiceCommission = null;
		Map<User, Float> revenuePercentage = calculateRevenuePercent(invoice, buildUserSetFromAccountUserList(accountUsers));
		
		for(AccountUser accountUser : accountUsers) {
			invoiceCommission = new InvoiceCommission();
			invoiceCommission.setInvoice(invoice);
			invoiceCommission.setUser(accountUser.getUser());
			
			float points = calculatePoints(accountUser, accountManagerFactor, salesRepresentativeFactor);			
			invoiceCommission.setPoints(points);
			
			invoiceCommission.setRevenuePercent(getRevenuePercentage(revenuePercentage, accountUser));
						
			invoiceCommissions.add(invoiceCommission);
		}
		
		return invoiceCommissions;
	}
	
	private float getRevenuePercentage(Map<User, Float> revenuePercentage, AccountUser accountUser) {
		if (MapUtils.isEmpty(revenuePercentage) || !revenuePercentage.containsKey(accountUser.getUser())) {
			return 0.0f;
		}
		
		return revenuePercentage.get(accountUser.getUser());			
	}
	
	private float calculateFactor(int numberOfAccountUserType) {
		if (numberOfAccountUserType < 0) {
			throw new IllegalArgumentException("Argument 'numberOfAccountUserType' cannot be less than 0.");
		} else if (numberOfAccountUserType == 0) {
			return 0;
		}
		
		return (1.0f / numberOfAccountUserType);
	}
	
	private int countNumberOfAccountManagers(List<AccountUser> accountUsers) {
		if (CollectionUtils.isEmpty(accountUsers)) {
			return 0;
		}
		
		int total = 0;		
		for (AccountUser accountUser : accountUsers) {
			if (isAccountManager(accountUser)) {
				total++;
			}
		}
		
		return total;
	}

	private boolean isAccountManager(AccountUser accountUser) {
		return (accountUser != null && accountUser.getRole() != null && accountUser.getRole().isAccountManager());
	}
	
	/**
	 * This method determine the number of Sales Representatives based on the number of account managers,
	 * because the UserAccountRole Enum has only 2 values, and if you know how many you have of Account Managers
	 * then you can determine the number of Sales Representatives through subtraction.
	 * 
	 * Includes runtime exceptions in the case where the UserAccountRole changes (someone adds in a third Role).
	 */
	private int countNumberOfSalesRepresentatives(int numberOfAccountManagers, int totalActiveAccountUsers) {
		if (UserAccountRole.values().length != 2) {
			throw new IllegalStateException("The UserAccountRole Enum type is expected to have only 2 values for " +
					"this method to execute properly (Sales Representative and Account Manager).");
		}
		
		if (numberOfAccountManagers < 0 || numberOfAccountManagers < 0) {
			throw new IllegalArgumentException("Invalid input to method.");
		}
		
		return (totalActiveAccountUsers - numberOfAccountManagers);
	}

	private float calculatePoints(AccountUser accountUser, float accountManagerFactor, float salesRepresentativeFactor) {
		if(accountUser.getRole().isAccountManager())
			return calculatePoints(accountUser, accountManagerFactor);
		
		return calculatePoints(accountUser, salesRepresentativeFactor);
	}
	
	private float calculatePoints(AccountUser accountUser, float factor) {
		float ownerPercent = accountUser.getOwnerPercent() / 100f;
		return (ownerPercent * factor);
	}

//	private void removeInactiveAccountUsers(List<AccountUser> accountUsers) {
//		if (CollectionUtils.isEmpty(accountUsers)) {
//			return;
//		}
//		
//		CollectionUtils.filter(accountUsers, new Predicate() {
//
//			@Override
//			public boolean evaluate(Object object) {
//				AccountUser accountUser = (AccountUser) object;
//				return accountUser.isCurrent();
//			}
//			
//		});
//	}
//	
//	private Map<AccountUser, Float> calculateRevenuePercent(Invoice invoice, List<AccountUser> accountUsers) {
//		ContractorAccount contractor = (ContractorAccount) invoice.getAccount();
//		Map<ContractorOperator, BigDecimal> revenuePerClientSite = calculateRevenueForEachClientSite(contractor);
//		if (MapUtils.isEmpty(revenuePerClientSite) || CollectionUtils.isEmpty(accountUsers)) {
//			return Collections.emptyMap();
//		}
//		
//		Map<AccountUser, Float> invoicePercentage = new HashMap<AccountUser, Float>();		
//		for (ContractorOperator clientSite : revenuePerClientSite.keySet()) {
//			List<AccountUser> accountUsersForOperator = null;
//			if (clientSite != null && clientSite.getOperatorAccount() != null) {
//				accountUsersForOperator = clientSite.getOperatorAccount().getAccountUsers();
//			}
//			
//			removeInactiveAccountUsers(accountUsersForOperator);
//			
//			if (CollectionUtils.isNotEmpty(accountUsersForOperator)) {
//				for (AccountUser accountUser : accountUsersForOperator) {
//					if (accountUsers.contains(accountUser)) {
//						if (!invoicePercentage.containsKey(accountUser)) {
//							invoicePercentage.put(accountUser, revenuePerClientSite.get(clientSite).divide(invoice.getTotalAmount()).floatValue());
//						} else {
//							float value = invoicePercentage.get(accountUser) 
//									+ revenuePerClientSite.get(clientSite).divide(invoice.getTotalAmount()).floatValue();
//							invoicePercentage.put(accountUser, value);
//						}
//					}
//				}
//			}
//		}			
//		
//		return invoicePercentage;
//	}
//	
//	private Map<ContractorOperator, BigDecimal> calculateRevenueForEachClientSite(ContractorAccount contractor) {
//		List<ContractorOperator> clientSites = getListOfAllOperatorSites(contractor);
//		if (CollectionUtils.isEmpty(clientSites)) {
//			return Collections.emptyMap();
//		}
//		
//		User user = findOneActiveBillingUser(contractor);
//		
//		contractor.setMembershipDate(null);
//		
//		Map<ContractorOperator, BigDecimal> revenuePerClientSite = new HashMap<ContractorOperator, BigDecimal>();
//		for (ContractorOperator clientSite : clientSites) {
//			List<ContractorOperator> oneClientSite = new ArrayList<ContractorOperator>(Arrays.asList(clientSite));
//			contractor.setOperators(oneClientSite);
//			Invoice invoice = billingService.createInvoiceWithoutSave(contractor, user);
//			revenuePerClientSite.put(clientSite, invoice.getTotalAmount());
//		}
//		
//		contractorAccountDAO.refresh(contractor);
//		
//		return revenuePerClientSite;
//	}
//	
//	private User findOneActiveBillingUser(ContractorAccount contractorAccount) {
//		User user = contractorAccount.getPrimaryContact();
//		if (user == null || !user.isActiveB()) {
//			throw new IllegalStateException("Unknown state to determine active user on account.");
//		}
//		
//		return user;
//	}
//	
//	private List<ContractorOperator> getListOfAllOperatorSites(ContractorAccount contractor) {
//		if (CollectionUtils.isEmpty(contractor.getNonCorporateOperators())) {
//			return Collections.emptyList();
//		}
//		
//		List<ContractorOperator> clientSites = new ArrayList<ContractorOperator>();
//		for (ContractorOperator clientSite : contractor.getNonCorporateOperators()) {
//			String doContractorsPay = clientSite.getOperatorAccount().getDoContractorsPay();
//			if ("Yes".equals(doContractorsPay) || !"Multiple".equals(doContractorsPay)) {
//				clientSites.add(clientSite);
//			}
//		}		
//		
//		return clientSites;
//	}

}
