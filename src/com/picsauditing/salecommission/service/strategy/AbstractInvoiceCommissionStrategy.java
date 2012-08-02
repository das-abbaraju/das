package com.picsauditing.salecommission.service.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.User;

public abstract class AbstractInvoiceCommissionStrategy implements InvoiceCommissionStrategy<Invoice> {
	
	@Autowired 
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected BillingCalculatorSingle billingService;
	
	public abstract List<InvoiceCommission> calculateInvoiceCommission(Invoice invoice);
	
	/**
	 * List of all the Users
	 * 
	 * @param accountUsers
	 * @return
	 */
	protected Set<User> buildUserSetFromAccountUserList(List<AccountUser> accountUsers) {
		Set<User> users = new HashSet<User>();
		for (AccountUser accountUser : accountUsers) {
			User user = accountUser.getUser();
			if (user != null) {
				users.add(user);
			}
		}
		
		return users;
	}
	
	/**
	 * 
	 * 
	 * @param invoice
	 * @param users - Set of Users who are active SRs or AMs
	 * @return
	 */
	protected Map<User, Float> calculateRevenuePercent(Invoice invoice, Set<User> users) {
		ContractorAccount contractor = (ContractorAccount) invoice.getAccount();
		Map<ContractorOperator, BigDecimal> revenuePerClientSite = calculateRevenueForEachClientSite(contractor);
		if (MapUtils.isEmpty(revenuePerClientSite) || CollectionUtils.isEmpty(users)) {
			return Collections.emptyMap();
		}
		
		Map<User, Float> invoicePercentage = new HashMap<User, Float>();		
		for (ContractorOperator clientSite : revenuePerClientSite.keySet()) {
			List<AccountUser> accountUsersForOperator = null;
			if (clientSite != null && clientSite.getOperatorAccount() != null) {
				accountUsersForOperator = clientSite.getOperatorAccount().getAccountUsers();
			}
			
			removeInactiveAccountUsers(accountUsersForOperator);			
			if (CollectionUtils.isEmpty(accountUsersForOperator)) {
				return invoicePercentage;
			}
			
			invoicePercentage = something(revenuePerClientSite, users, accountUsersForOperator, invoice, clientSite);
//			for (User user : users) {
//				AccountUser accountUser = findAccountUser(accountUsersForOperator, user);
//				if (accountUser != null) {
//					if (!invoicePercentage.containsKey(user)) {
//						invoicePercentage.put(user, revenuePerClientSite.get(clientSite).divide(invoice.getTotalAmount()).floatValue());
//					} else {
//						float value = invoicePercentage.get(user) 
//								+ revenuePerClientSite.get(clientSite).divide(invoice.getTotalAmount()).floatValue();
//						invoicePercentage.put(user, value);
//					}
//				}
//			}			
		}			
		
		return invoicePercentage;
	}
	
	protected void removeInactiveAccountUsers(List<AccountUser> accountUsers) {
		if (CollectionUtils.isEmpty(accountUsers)) {
			return;
		}
		
		CollectionUtils.filter(accountUsers, new Predicate() {

			@Override
			public boolean evaluate(Object object) {
				AccountUser accountUser = (AccountUser) object;
				return accountUser.isCurrent();
			}
			
		});
	}
	
	// TODO: Clean up and rename this method
	private Map<User, Float> something(Map<ContractorOperator, BigDecimal> revenuePerClientSite, Set<User> users, 
			List<AccountUser> accountUsersForOperator, Invoice invoice, ContractorOperator clientSite) {
		
		Map<User, Float> invoicePercentage = new HashMap<User, Float>();	
		for (User user : users) {
			AccountUser accountUser = findAccountUser(accountUsersForOperator, user);
			if (accountUser != null) {
				if (!invoicePercentage.containsKey(user)) {
					invoicePercentage.put(user, calculateRevenuePercentage(accountUser, invoice.getTotalAmount(), revenuePerClientSite.get(clientSite)));
				} else {
					float value = invoicePercentage.get(user) 
							+ calculateRevenuePercentage(accountUser, invoice.getTotalAmount(), revenuePerClientSite.get(clientSite));
					invoicePercentage.put(user, value);
				}
			}
		}
		
		return invoicePercentage;
	}
	
	private float calculateRevenuePercentage(AccountUser accountUser, BigDecimal invoiceTotal, BigDecimal revenueForClientSite) {
		float accountUserOwnership = accountUser.getOwnerPercent() / 100f;
		float clientSiteValueRatioOnInvoice = invoiceTotal.divide(invoiceTotal).floatValue();
		return accountUserOwnership * clientSiteValueRatioOnInvoice;
	}
	
	private AccountUser findAccountUser(List<AccountUser> accountUsers, User user) {
		for (AccountUser accountUser : accountUsers) {
			if (user.equals(accountUser.getUser())) {
				return accountUser;
			}
		}
		
		return null;
	}
	
	private Map<ContractorOperator, BigDecimal> calculateRevenueForEachClientSite(ContractorAccount contractor) {
		List<ContractorOperator> clientSites = getListOfAllOperatorSites(contractor);
		if (CollectionUtils.isEmpty(clientSites)) {
			return Collections.emptyMap();
		}
		
		User user = findOneActiveBillingUser(contractor);
		
		contractor.setMembershipDate(null);
		
		Map<ContractorOperator, BigDecimal> revenuePerClientSite = new HashMap<ContractorOperator, BigDecimal>();
		for (ContractorOperator clientSite : clientSites) {
			List<ContractorOperator> oneClientSite = new ArrayList<ContractorOperator>(Arrays.asList(clientSite));
			contractor.setOperators(oneClientSite);
			Invoice invoice = billingService.createInvoiceWithoutSave(contractor, user);
			revenuePerClientSite.put(clientSite, invoice.getTotalAmount());
		}
		
		contractorAccountDAO.refresh(contractor);
		
		return revenuePerClientSite;
	}
	
	private User findOneActiveBillingUser(ContractorAccount contractorAccount) {
		User user = contractorAccount.getPrimaryContact();
		if (user == null || !user.isActiveB()) {
			throw new IllegalStateException("Unknown state to determine active user on account.");
		}
		
		return user;
	}
	
	private List<ContractorOperator> getListOfAllOperatorSites(ContractorAccount contractor) {
		if (CollectionUtils.isEmpty(contractor.getNonCorporateOperators())) {
			return Collections.emptyList();
		}
		
		List<ContractorOperator> clientSites = new ArrayList<ContractorOperator>();
		for (ContractorOperator clientSite : contractor.getNonCorporateOperators()) {
			String doContractorsPay = clientSite.getOperatorAccount().getDoContractorsPay();
			if ("Yes".equals(doContractorsPay) || !"Multiple".equals(doContractorsPay)) {
				clientSites.add(clientSite);
			}
		}		
		
		return clientSites;
	}

}
