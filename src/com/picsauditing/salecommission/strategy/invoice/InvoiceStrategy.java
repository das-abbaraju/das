package com.picsauditing.salecommission.strategy.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

public class InvoiceStrategy extends AbstractInvoiceCommissionStrategy {

	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDAO;

	@Override
	protected boolean hasStrategyAlreadyProcessed(Invoice invoice) {
		List<InvoiceCommission> invoiceCommission = invoiceCommissionDAO.findByInvoiceId(invoice.getId());
		return CollectionUtils.isNotEmpty(invoiceCommission);
	}

	/**
	 * Steps:
	 * 
	 * 	(1) Calculate the service level for each Client Site
	 *  (2) Calculate the total number of sites that use each service level
	 *  (3) Determine total dollar amount for each service level
	 *  (4) Calculate the Client Site Revenue weight 
	 */
	@Override
	public void buildInvoiceCommissions(Invoice invoice) {
		ContractorAccount contractor = (ContractorAccount) invoice.getAccount();
		List<ClientSiteServiceLevel> clientSiteServiceLevels = calculateServiceForEachClientSite(contractor);
		Map<FeeClass, Integer> totalSites = getTotalSitesForService(clientSiteServiceLevels);
		Map<FeeClass, BigDecimal> fees = invoice.getCommissionEligibleFees(false);
		Map<ContractorOperator, Double> clientRevenueWeights = calculateAllClientRevenueWeights(invoice, clientSiteServiceLevels, totalSites, fees);
		
		
	}
	
	private void generateInvoiceCommissions(Invoice invoice, Map<ContractorOperator, Double> clientRevenueWeights) {
		for (Map.Entry<ContractorOperator, Double> individualClientRevenueWeight : clientRevenueWeights.entrySet()) {
			List<AccountUser> accountUsers = getActiveAccountUsersForClientSite(individualClientRevenueWeight.getKey().getOperatorAccount());
			for (AccountUser accountUser : accountUsers) {
				InvoiceCommission invoiceCommission = new InvoiceCommission();
				invoiceCommission.setAuditColumns(invoice.getUpdatedBy());
				invoiceCommission.setInvoice(invoice);
				invoiceCommission.setPoints(calculatePoints(invoice, accountUser, individualClientRevenueWeight.getValue()));
				invoiceCommission.setRevenuePercent(calculateRevenueSplit(accountUser, individualClientRevenueWeight.getValue()));
			}
		}
	}
	
	private double calculatePoints(Invoice invoice, AccountUser accountUser, double weight) {
		if (isActivationInvoice(invoice)) {
			return calculateRevenueSplit(accountUser, weight);
		}
		
		return 0;
	}
	
	private double calculateRevenueSplit(AccountUser accountUser, double weight) {
		return (accountUser.getOwnerPercent() / 100) * weight;
	}
	
	private boolean isActivationInvoice(Invoice invoice) {
		return false;
	}
	
	private Map<ContractorOperator, Double> calculateAllClientRevenueWeights(Invoice invoice, List<ClientSiteServiceLevel> clientSiteServiceLevels, Map<FeeClass, Integer> totalSites,
			Map<FeeClass, BigDecimal> fees) {
		if (CollectionUtils.isEmpty(clientSiteServiceLevels) || MapUtils.isEmpty(fees) || MapUtils.isEmpty(totalSites)) {
			return Collections.emptyMap();
		}
		
		Map<ContractorOperator, Double> revenueWeights = new HashMap<ContractorOperator, Double>(); 
		for (ClientSiteServiceLevel clientSiteServiceLevel : clientSiteServiceLevels) {
			revenueWeights.put(clientSiteServiceLevel.getClientSite(), calculateClientRevenueWeight(invoice, clientSiteServiceLevel, totalSites, fees));
		}
		
		return revenueWeights;
	}
	
	/**
	 * 
	 * 
	 * @param clientSiteServiceLevel Contains the clientSite we are calculating the 
	 * @param totalSites 
	 * @param fees
	 * @return
	 */
	private double calculateClientRevenueWeight(Invoice invoice, ClientSiteServiceLevel clientSiteServiceLevel, Map<FeeClass, Integer> totalSites, Map<FeeClass, BigDecimal> fees) {
		double result = 0;
		// Getting the list of service types (DG, IG, etc), for this client site
		for (FeeClass feeClass : clientSiteServiceLevel.getServiceLevels()) {
			// I am now looking at a specific service level
			int totalSitesWithService = totalSites.get(feeClass);
			BigDecimal invoiceRevenueForService = fees.get(feeClass);
			result += invoiceRevenueForService.divide(new BigDecimal(totalSitesWithService)).doubleValue();
		}
		
		result /= invoice.getTotalCommissionEligibleInvoice(false).doubleValue();
		
		return result;
	}
	
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	private List<ClientSiteServiceLevel> calculateServiceForEachClientSite(ContractorAccount contractor) {
		List<ContractorOperator> clientSites = getListOfAllOperatorSites(contractor);
		if (CollectionUtils.isEmpty(clientSites)) {
			return Collections.emptyList();
		}

		List<ClientSiteServiceLevel> clientSiteServiceLevels = new ArrayList<ClientSiteServiceLevel>();
		for (ContractorOperator clientSite : clientSites) {
			List<ContractorOperator> oneClientSite = new ArrayList<ContractorOperator>(Arrays.asList(clientSite));
			contractor.setOperators(oneClientSite);
			
			billingService.calculateContractorInvoiceFees(contractor);
			buildFromContractorFees(contractor, clientSite);		
		}

		return clientSiteServiceLevels;
	}
	
	private ClientSiteServiceLevel buildFromContractorFees(ContractorAccount contractor, ContractorOperator clientSite) {
		ClientSiteServiceLevel clientSiteServiceLevel = new ClientSiteServiceLevel();
		clientSiteServiceLevel.setServiceLevels(contractor.getFees().keySet());
		clientSiteServiceLevel.setClientSite(clientSite);
		
		return clientSiteServiceLevel;
	}
	
	private Map<FeeClass, Integer> getTotalSitesForService(List<ClientSiteServiceLevel> clientSiteServiceLevels) {
		if (CollectionUtils.isEmpty(clientSiteServiceLevels)) {
			return Collections.emptyMap();
		}
		
		Map<FeeClass, Integer> totalSites = new HashMap<FeeClass, Integer>();
		for (ClientSiteServiceLevel clientSiteServiceLevel : clientSiteServiceLevels) {
			for (FeeClass feeClass : clientSiteServiceLevel.getServiceLevels()) {
				if (totalSites.containsKey(feeClass)) {
					Integer count = totalSites.get(feeClass);
					count++;
				} else {
					totalSites.put(feeClass, 1);
				}
			}
		}
		
		return totalSites;
	}
	
	private List<AccountUser> getActiveAccountUsersForClientSite(OperatorAccount clientSite) {
		if (clientSite == null) {
			return Collections.emptyList();
		}
		
		List<AccountUser> accountUsers = new ArrayList<AccountUser>();
		for (AccountUser accountUser : clientSite.getAccountUsers()) {
			if (accountUser.isCurrent()) {
				accountUsers.add(accountUser);
			}
		}
		
		return accountUsers;
	}
	

	
// ==========================================================	
	
	

//	@Override
//	protected void buildInvoiceCommissions(Invoice invoice) {
//		// find account users
//		List<AccountUser> accountUsers = new ArrayList<AccountUser>();
//		ContractorAccount contractor = (ContractorAccount) invoice.getAccount();
//
//		// Client Sites
//		List<OperatorAccount> operators = contractor.getOperatorAccounts();
//
//		for (OperatorAccount op : operators) {
//			accountUsers.addAll(op.getAccountUsers());
//		}
//
//		removeInactiveAccountUsers(accountUsers);
//
//		int numberAMs = countNumberOfAccountManagers(accountUsers);
//		int numberSRs = countNumberOfSalesRepresentatives(numberAMs, accountUsers == null ? 0 : accountUsers.size());
//
//		float accountManagerFactor = calculateFactor(numberAMs);
//		float salesRepresentativeFactor = calculateFactor(numberSRs);
//
//		buildInvoiceCommissions(invoice, accountUsers, accountManagerFactor, salesRepresentativeFactor);
//	}

	private void buildInvoiceCommissions(Invoice invoice, List<AccountUser> accountUsers,
			float accountManagerFactor, float salesRepresentativeFactor) {
		
		InvoiceCommission invoiceCommission = null;
		Map<User, Float> revenuePercentage = calculateRevenuePercent(invoice,
				buildUserSetFromAccountUserList(accountUsers));

		for (AccountUser accountUser : accountUsers) {
			invoiceCommission = new InvoiceCommission();
			invoiceCommission.setInvoice(invoice);
			invoiceCommission.setUser(accountUser.getUser());
			invoiceCommission.setPoints(0);
			invoiceCommission.setRevenuePercent(getRevenuePercentage(revenuePercentage, accountUser));
			invoiceCommission.setAuditColumns(invoice.getUpdatedBy());
			
			invoiceCommissionDAO.save(invoiceCommission);
		}
	}

	protected float getRevenuePercentage(Map<User, Float> revenuePercentage, AccountUser accountUser) {
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
	 * This method determine the number of Sales Representatives based on the
	 * number of account managers, because the UserAccountRole Enum has only 2
	 * values, and if you know how many you have of Account Managers then you
	 * can determine the number of Sales Representatives through subtraction.
	 * 
	 * Includes runtime exceptions in the case where the UserAccountRole changes
	 * (someone adds in a third Role).
	 */
	private int countNumberOfSalesRepresentatives(int numberOfAccountManagers, int totalActiveAccountUsers) {
		if (UserAccountRole.values().length != 2) {
			throw new IllegalStateException("The UserAccountRole Enum type is expected to have only 2 values for "
					+ "this method to execute properly (Sales Representative and Account Manager).");
		}

		if (numberOfAccountManagers < 0 || numberOfAccountManagers < 0) {
			throw new IllegalArgumentException("Invalid input to method.");
		}

		return (totalActiveAccountUsers - numberOfAccountManagers);
	}


}