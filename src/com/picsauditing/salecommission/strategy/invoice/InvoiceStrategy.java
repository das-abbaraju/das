package com.picsauditing.salecommission.strategy.invoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
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

	@Override
	protected void buildInvoiceCommissions(Invoice invoice) {
		// find account users
		List<AccountUser> accountUsers = new ArrayList<AccountUser>();
		ContractorAccount contractor = (ContractorAccount) invoice.getAccount();

		// Client Sites
		List<OperatorAccount> operators = contractor.getOperatorAccounts();

		for (OperatorAccount op : operators) {
			accountUsers.addAll(op.getAccountUsers());
		}

		removeInactiveAccountUsers(accountUsers);

		int numberAMs = countNumberOfAccountManagers(accountUsers);
		int numberSRs = countNumberOfSalesRepresentatives(numberAMs, accountUsers == null ? 0 : accountUsers.size());

		float accountManagerFactor = calculateFactor(numberAMs);
		float salesRepresentativeFactor = calculateFactor(numberSRs);

		buildInvoiceCommissions(invoice, accountUsers, accountManagerFactor, salesRepresentativeFactor);
	}

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