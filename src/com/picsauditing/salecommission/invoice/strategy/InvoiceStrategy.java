package com.picsauditing.salecommission.invoice.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import com.picsauditing.model.billing.helper.ContractorInvoiceState;
import com.picsauditing.model.billing.helper.ContractorResetter;
import com.picsauditing.model.billing.helper.InvoiceHelper;
import com.picsauditing.search.CommissionAuditQueryMapper;
import com.picsauditing.search.Database;

public class InvoiceStrategy extends AbstractInvoiceCommissionStrategy {

	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDAO;

	private static final Logger logger = LoggerFactory.getLogger(InvoiceStrategy.class);

	@Override
	protected boolean hasStrategyAlreadyProcessed(Invoice invoice) {
		List<InvoiceCommission> invoiceCommission = invoiceCommissionDAO.findByInvoiceId(invoice.getId());
		return CollectionUtils.isNotEmpty(invoiceCommission);
	}

	/**
	 * Steps:
	 *
	 * (1) Calculate the service level for each Client Site (2) Calculate the
	 * total number of sites that use each service level (3) Determine total
	 * dollar amount for each service level (4) Calculate the Client Site
	 * Revenue weight
	 */
	@Override
	public void buildInvoiceCommissions(Invoice invoice) {
		ContractorAccount contractor = (ContractorAccount) invoice.getAccount();
		List<ClientSiteServiceLevel> clientSiteServiceLevels = calculateServiceForEachClientSite(contractor, invoice);
		Map<FeeClass, Integer> totalSites = getTotalSitesForService(clientSiteServiceLevels, invoice.getId());
		Map<FeeClass, BigDecimal> fees = invoice.getCommissionEligibleFees(false);
		Map<ContractorOperator, Double> clientRevenueWeights = calculateAllClientRevenueWeights(invoice,
				clientSiteServiceLevels, totalSites, fees);
		generateInvoiceCommissions(invoice, clientRevenueWeights);
	}

	private void generateInvoiceCommissions(Invoice invoice, Map<ContractorOperator, Double> clientRevenueWeights) {
		for (Map.Entry<ContractorOperator, Double> individualClientRevenueWeight : clientRevenueWeights.entrySet()) {
			List<AccountUser> accountUsers = getActiveAccountUsersForClientSite(individualClientRevenueWeight.getKey()
					.getOperatorAccount(), invoice.getCreationDate());
			for (AccountUser accountUser : accountUsers) {
				InvoiceCommission invoiceCommission = new InvoiceCommission();
				invoiceCommission.setAccountUser(accountUser);
				invoiceCommission.setAuditColumns(invoice.getUpdatedBy());
				invoiceCommission.setInvoice(invoice);

				BigDecimal revenuePercent = calculateRevenueSplit(accountUser, individualClientRevenueWeight.getValue());

				invoiceCommission.setPoints((isActivationInvoice(invoice)) ? revenuePercent : BigDecimal.ZERO);
				invoiceCommission.setRevenuePercent(revenuePercent);
				invoiceCommissionDAO.save(invoiceCommission);
			}
		}
	}

	private BigDecimal calculateRevenueSplit(AccountUser accountUser, double weight) {
		BigDecimal result = BigDecimal.valueOf(accountUser.getOwnerPercent() / 100.0);
		return result.multiply(BigDecimal.valueOf(weight));
	}

	private boolean isActivationInvoice(Invoice invoice) {
		if (invoice == null || CollectionUtils.isEmpty(invoice.getItems())) {
			return false;
		}

		for (InvoiceItem invoiceItem : invoice.getItems()) {
			if (invoiceItem.getInvoiceFee().isActivation()) {
				return true;
			}
		}

		return false;
	}

	private Map<ContractorOperator, Double> calculateAllClientRevenueWeights(Invoice invoice,
			List<ClientSiteServiceLevel> clientSiteServiceLevels, Map<FeeClass, Integer> totalSites,
			Map<FeeClass, BigDecimal> fees) {
		if (CollectionUtils.isEmpty(clientSiteServiceLevels) || MapUtils.isEmpty(fees) || MapUtils.isEmpty(totalSites)) {
			return Collections.emptyMap();
		}

		Map<ContractorOperator, Double> revenueWeights = new HashMap<ContractorOperator, Double>();
		for (ClientSiteServiceLevel clientSiteServiceLevel : clientSiteServiceLevels) {
			revenueWeights.put(clientSiteServiceLevel.getClientSite(),
					calculateClientRevenueWeight(invoice, clientSiteServiceLevel, totalSites, fees));
		}

		return revenueWeights;
	}

	/**
	 *
	 *
	 * @param clientSiteServiceLevel
	 *            Contains the clientSite we are calculating the
	 * @param totalSites
	 * @param fees
	 * @return
	 */
	private double calculateClientRevenueWeight(Invoice invoice, ClientSiteServiceLevel clientSiteServiceLevel,
			Map<FeeClass, Integer> totalSites, Map<FeeClass, BigDecimal> fees) {
		double result = 0;
		// Getting the list of service types (DG, IG, etc), for this client site
		for (FeeClass feeClass : clientSiteServiceLevel.getServiceLevels()) {
			// I am now looking at a specific service level
			int totalSitesWithService = totalSites.get(feeClass);
			BigDecimal invoiceRevenueForService = fees.get(feeClass);
			if (invoiceRevenueForService != null && totalSitesWithService > 0) {
				result += BigDecimal.valueOf(invoiceRevenueForService.doubleValue() / totalSitesWithService)
						.doubleValue();
			}
		}

		double totalCommissionEligible = invoice.getTotalCommissionEligibleInvoice(true).doubleValue();
		if (totalCommissionEligible > 0) {
			result /= invoice.getTotalCommissionEligibleInvoice(true).doubleValue();
		}

		return result;
	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	private List<ClientSiteServiceLevel> calculateServiceForEachClientSite(ContractorAccount contractor, Invoice invoice) {
		List<ContractorOperator> clientSites = getListOfAllOperatorSites(contractor);
		if (CollectionUtils.isEmpty(clientSites)) {
			return Collections.emptyList();
		}

		ContractorInvoiceState contractorState = InvoiceHelper.buildContractorInvoiceState(invoice);

		List<ClientSiteServiceLevel> clientSiteServiceLevels = new ArrayList<ClientSiteServiceLevel>();
		try {
			for (ContractorOperator clientSite : clientSites) {
				List<ContractorOperator> oneClientSite = new ArrayList<ContractorOperator>(Arrays.asList(clientSite));
				contractor.setOperators(oneClientSite);

				ContractorResetter.resetContractor(contractor, contractorState);

				billingService.calculateContractorInvoiceFees(contractor);
				List<InvoiceItem> invoiceItems = billingService.createInvoiceItems(contractor, invoice.getCreatedBy());

				clientSiteServiceLevels.add(buildFromContractorFees(invoiceItems, clientSite));
			}
		} finally {
			refreshContractorFromDatabase(contractor);
		}

		return clientSiteServiceLevels;
	}

	private void refreshContractorFromDatabase(ContractorAccount contractor) {
		try {
			contractorAccountDAO.refresh(contractor);
		} catch (Exception nothingWeCanDo) {
			logger.error("An error occurred while refreshing contractor id = {}", contractor.getId(), nothingWeCanDo);
		}
	}

	private ClientSiteServiceLevel buildFromContractorFees(List<InvoiceItem> invoiceItems, ContractorOperator clientSite) {
		ClientSiteServiceLevel clientSiteServiceLevel = new ClientSiteServiceLevel();
		clientSiteServiceLevel.setServiceLevels(getFeesForInvoiceItems(invoiceItems));
		clientSiteServiceLevel.setClientSite(clientSite);

		return clientSiteServiceLevel;
	}

	private Set<FeeClass> getFeesForInvoiceItems(List<InvoiceItem> invoiceItems) {
		if (CollectionUtils.isEmpty(invoiceItems)) {
			return Collections.emptySet();
		}

		Set<FeeClass> invoiceFees = new HashSet<FeeClass>();
		for (InvoiceItem invoiceItem : invoiceItems) {
			InvoiceFee invoiceFee = invoiceItem.getInvoiceFee();
			if (invoiceFee != null && invoiceFee.isCommissionEligible()) {
				invoiceFees.add(invoiceFee.getFeeClass());
			}
		}

		return invoiceFees;
	}

	private Map<FeeClass, Integer> getTotalSitesForService(List<ClientSiteServiceLevel> clientSiteServiceLevels,
			int invoiceId) {
		if (CollectionUtils.isEmpty(clientSiteServiceLevels)) {
			return Collections.emptyMap();
		}

		List<CommissionAudit> commissionAudits = new ArrayList<CommissionAudit>();
		Map<FeeClass, Integer> totalSites = new HashMap<FeeClass, Integer>();
		for (ClientSiteServiceLevel clientSiteServiceLevel : clientSiteServiceLevels) {
			for (FeeClass feeClass : clientSiteServiceLevel.getServiceLevels()) {
				if (totalSites.containsKey(feeClass)) {
					int count = totalSites.get(feeClass);
					totalSites.put(feeClass, ++count);
				} else {
					totalSites.put(feeClass, 1);
				}

				CommissionAudit commissionAudit = buildCommissionAudit(invoiceId, clientSiteServiceLevel
						.getClientSite().getOperatorAccount().getId(), feeClass);
				commissionAudits.add(commissionAudit);
			}
		}

		saveClientSiteServices(commissionAudits);

		return totalSites;
	}

	private CommissionAudit buildCommissionAudit(int invoiceId, int clientSiteId, FeeClass feeClass) {
		CommissionAudit clientSiteServices = new CommissionAudit();
		clientSiteServices.setInvoiceId(invoiceId);
		clientSiteServices.setClientSiteId(clientSiteId);
		clientSiteServices.setFeeClass(feeClass);

		return clientSiteServices;
	}

	private void saveClientSiteServices(List<CommissionAudit> clientSiteServices) {
		String sql = "INSERT INTO commission_audit (invoiceID, clientSiteID, feeClass) values (?, ?, ?)";

		try {
			Database.executeBatch(sql, clientSiteServices, new CommissionAuditQueryMapper());
		} catch (Exception e) {
			logger.error("An error occurred while performing a batch insert for invoice commissions", e);
		}
	}

	private List<AccountUser> getActiveAccountUsersForClientSite(OperatorAccount clientSite, Date effectiveDate) {
		if (clientSite == null) {
			return Collections.emptyList();
		}

		List<AccountUser> accountUsers = new ArrayList<AccountUser>();
		for (AccountUser accountUser : clientSite.getAccountUsers()) {
			if (isAccountUserCurrent(accountUser, effectiveDate)) {
				accountUsers.add(accountUser);
			}
		}

		return accountUsers;
	}

	private boolean isAccountUserCurrent(AccountUser accountUser, Date effectiveDate) {
		if (accountUser.getStartDate() != null && accountUser.getStartDate().after(effectiveDate)) {
			return false; // This hasn't started yet
		}

		if (accountUser.getEndDate() != null && accountUser.getEndDate().before(effectiveDate)) {
			return false; // This already ended
		}

		return true;
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