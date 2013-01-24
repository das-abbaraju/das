package com.picsauditing.model.billing;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.data.DataEvent;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.PICS.data.InvoiceDataEvent.InvoiceEventType;
import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.PICS.data.PaymentDataEvent.PaymentEventType;
import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.dao.PaymentCommissionDAO;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.salecommission.invoice.strategy.CommissionAudit;
import com.picsauditing.util.Strings;

public class CommissionModel {

	@Autowired
	private InvoiceModel invoiceModel;
	@Autowired
	private DataObservable salesCommissionDataObservable;
	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDAO;
	@Autowired
	private PaymentCommissionDAO paymentCommissionDAO;

	public void processCommissionForInvoice(int invoiceId) {
		Invoice invoice = invoiceModel.findInvoiceById(invoiceId);
		if (invoice == null) {
			// if there is no invoice, no reason to continue
			throw new NoResultException("Invoice not found.");
		}

		deleteExistingPaymentCommissions(invoiceId);
		deleteExistingInvoiceCommissions(invoiceId);
		processInvoiceCommission(invoice);
		processPaymentCommission(invoice);
	}

	private void deleteExistingPaymentCommissions(int invoiceId) {
		List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findByInvoiceId(invoiceId);
		if (CollectionUtils.isEmpty(invoiceCommissions)) {
			return;
		}

		paymentCommissionDAO.deleteData("t.invoiceCommission.id IN (" + Strings.implodeIDs(invoiceCommissions) + ")");
	}

	private void deleteExistingInvoiceCommissions(int invoiceId) {
		invoiceCommissionDAO.deleteData("t.invoice.id = " + invoiceId);
	}

	private void processInvoiceCommission(Invoice invoice) {
		DataEvent<Invoice> dataEvent = new InvoiceDataEvent(invoice, InvoiceEventType.NEW);
		dataEvent.setFromApiForForceReload(true);
		salesCommissionDataObservable.setChanged();
		salesCommissionDataObservable.notifyObservers(dataEvent);
	}

	private void processPaymentCommission(Invoice invoice) {
		List<PaymentAppliedToInvoice> paymentsToInvoice = invoice.getPayments();
		if (CollectionUtils.isEmpty(paymentsToInvoice)) {
			return;
		}

		for (PaymentAppliedToInvoice paymentToInvoice : paymentsToInvoice) {
			if (paymentToInvoice == null || paymentToInvoice.getPayment() == null) {
				continue;
			}

			DataEvent<Payment> dataEvent = new PaymentDataEvent(paymentToInvoice.getPayment(), PaymentEventType.PAYMENT);
			dataEvent.setFromApiForForceReload(true);
			salesCommissionDataObservable.setChanged();
			salesCommissionDataObservable.notifyObservers(dataEvent);
		}
	}

	public StringBuilder buildCommissionAuditCsvFile(int invoiceId) {
		StringBuilder csvFileContents = new StringBuilder();

		List<CommissionAudit> commissionAudits = invoiceModel.findCommissionAudits(invoiceId);
		Map<FeeClass, Integer> serviceSummaryMap = invoiceModel.getNumberOfSitesUsingService(commissionAudits);
		Invoice invoice = invoiceModel.findInvoiceById(invoiceId);
		List<CommissionDetail> commissionDetails = invoiceModel.getCommissionDetails(invoice);

		csvFileContents.append("For Invoice ID = ").append(invoiceId).append(Strings.NEW_LINE).append(Strings.NEW_LINE);
		buildCommissionEligibleData(csvFileContents, invoice);
		buildClientSiteServiceLevels(csvFileContents, commissionDetails);
		buildServiceSummary(csvFileContents, serviceSummaryMap, invoice);
		buildMathForBreakdown(csvFileContents, commissionDetails, serviceSummaryMap, commissionAudits, invoice);

		return csvFileContents;
	}

	private void buildCommissionEligibleData(StringBuilder commissionEligibleData, Invoice invoice) {
		commissionEligibleData.append("Commission Eligible Data").append(Strings.NEW_LINE);
		commissionEligibleData.append("Fee,Amount").append(Strings.NEW_LINE);

		if (invoice != null) {
			Map<FeeClass, BigDecimal> commissionFees = invoice.getCommissionEligibleFees(true);
			for (Map.Entry<FeeClass, BigDecimal> entry : commissionFees.entrySet()) {
				if (entry != null) {
					commissionEligibleData.append(entry.getKey().name()).append(",");
					commissionEligibleData.append(entry.getValue().doubleValue());
					commissionEligibleData.append(Strings.NEW_LINE);
				}
			}
		}

		commissionEligibleData.append("Total").append(",");
		commissionEligibleData.append(invoice.getTotalCommissionEligibleInvoice(false));
		commissionEligibleData.append(Strings.NEW_LINE).append(Strings.NEW_LINE);
	}

	private void buildClientSiteServiceLevels(StringBuilder clientSiteServiceLevels,
			List<CommissionDetail> commissionDetails) {
		clientSiteServiceLevels.append("Client Site Service Levels").append(Strings.NEW_LINE);
		clientSiteServiceLevels.append("Client Site Name").append(",").append("Services").append(Strings.NEW_LINE);

		// TODO: clean this up
		Set<Integer> clientSitesOutput = new HashSet<Integer>();
		for (CommissionDetail commissionDetail : commissionDetails) {
			if (clientSitesOutput.contains(commissionDetail.getClientSiteId())) {
				continue;
			}

			clientSiteServiceLevels.append("\"").append(commissionDetail.getClientSite()).append("\"").append(",");
			clientSiteServiceLevels.append("\"").append(commissionDetail.getServiceLevels()).append("\"")
					.append(Strings.NEW_LINE);

			clientSitesOutput.add(commissionDetail.getClientSiteId());
		}

		clientSiteServiceLevels.append(Strings.NEW_LINE);
	}

	private void buildServiceSummary(StringBuilder serviceSummary, Map<FeeClass, Integer> serviceSummaryMap,
			Invoice invoice) {
		serviceSummary.append("Service Summary").append(Strings.NEW_LINE);
		serviceSummary.append("Service").append(",").append("Total Sites Using Service").append(",")
				.append("Commission Eligible").append(",").append("Total Amount from Invoice").append(Strings.NEW_LINE);

		Map<FeeClass, BigDecimal> feeTotals = invoice.getCommissionEligibleFees(false);
		if (MapUtils.isEmpty(serviceSummaryMap) || MapUtils.isEmpty(feeTotals)) {
			return;
		}

		for (Map.Entry<FeeClass, Integer> entry : serviceSummaryMap.entrySet()) {
			serviceSummary.append(entry.getKey().name()).append(",");
			serviceSummary.append(entry.getValue()).append(",");
			serviceSummary.append(feeTotals.containsKey(entry.getKey()) ? "Y" : "N").append(",");
			serviceSummary.append(
					feeTotals.get(entry.getKey()) == null ? "0" : feeTotals.get(entry.getKey()).doubleValue()).append(
					",");
			serviceSummary.append(Strings.NEW_LINE);
		}

		serviceSummary.append(Strings.NEW_LINE);
	}

	private void buildMathForBreakdown(StringBuilder mathForBreakdown, List<CommissionDetail> commissionDetails,
			Map<FeeClass, Integer> serviceSummary, List<CommissionAudit> commissionAudits, Invoice invoice) {
		// We should build the menu header even if there are no services
		Set<FeeClass> allClientServices = Collections.emptySet();
		if (MapUtils.isNotEmpty(serviceSummary)) {
			allClientServices = serviceSummary.keySet();
		}

		buildMathForBreakdownHeader(mathForBreakdown, allClientServices);
		Map<Integer, List<FeeClass>> clientServiceLevels = invoiceModel.getClientSiteServiceLevels(commissionAudits);

		if (CollectionUtils.isEmpty(commissionDetails) || MapUtils.isEmpty(serviceSummary)
				|| MapUtils.isEmpty(clientServiceLevels) || invoice == null) {
			return;
		}

		BigDecimal totalCommissionEligible = invoice.getTotalCommissionEligibleInvoice(false);
		Map<FeeClass, BigDecimal> commissionEligibleFees = invoice.getCommissionEligibleFees(false);

		for (CommissionDetail commissionDetail : commissionDetails) {
			mathForBreakdown.append("\"").append(commissionDetail.getClientSite()).append("\"").append(",");
			mathForBreakdown.append("\"").append(commissionDetail.getAccountRepresentativeName()).append("\"")
					.append(",");
			mathForBreakdown.append(commissionDetail.getRole()).append(",");

			int ownershipPercent = commissionDetail.getWeight();
			mathForBreakdown.append(ownershipPercent).append(",");

			BigDecimal commissionEligibleSum = BigDecimal.ZERO;
			for (FeeClass serviceLevel : allClientServices) {
				if (!clientSiteHasService(clientServiceLevels, serviceLevel, commissionDetail.getClientSiteId())) {
					mathForBreakdown.append("0,0,0,");
					continue;
				}

				BigDecimal serviceFeeOnInvoice = commissionEligibleFees.get(serviceLevel);
				int totalSitesUsingService = serviceSummary.get(serviceLevel);

				BigDecimal weightedValue = BigDecimal.ZERO;
				if (totalSitesUsingService != 0) {
					weightedValue = calculateWeightedValue(serviceFeeOnInvoice, totalSitesUsingService);
				}

				commissionEligibleSum = commissionEligibleSum.add(weightedValue);

				mathForBreakdown.append(serviceFeeOnInvoice.doubleValue()).append(",");
				mathForBreakdown.append(totalSitesUsingService).append(",");
				mathForBreakdown.append(weightedValue.doubleValue()).append(",");
			}

			mathForBreakdown.append(commissionEligibleSum.doubleValue()).append(",");
			mathForBreakdown.append(totalCommissionEligible.doubleValue()).append(",");

			BigDecimal aggregatedWeight = calculateAggregatedWeight(totalCommissionEligible, commissionEligibleSum);
			mathForBreakdown.append(aggregatedWeight.doubleValue()).append(",");

			BigDecimal points = calculatePoints(aggregatedWeight, ownershipPercent);
			mathForBreakdown.append(points.doubleValue()).append(",");

			BigDecimal revenueCredit = points.multiply(totalCommissionEligible).multiply(
					BigDecimal.valueOf(ownershipPercent / 100));
			mathForBreakdown.append(revenueCredit.doubleValue()).append(",").append(Strings.NEW_LINE);
		}
	}

	private BigDecimal calculateWeightedValue(BigDecimal serviceFeeOnInvoice, int totalSitesUsingService) {
		return BigDecimal.valueOf(serviceFeeOnInvoice.doubleValue() / totalSitesUsingService);
	}

	private boolean clientSiteHasService(Map<Integer, List<FeeClass>> clientServiceLevels, FeeClass serviceLevel,
			int clientSiteId) {
		if (clientServiceLevels.containsKey(clientSiteId)) {
			List<FeeClass> services = clientServiceLevels.get(clientSiteId);
			if (CollectionUtils.isNotEmpty(services)) {
				return services.contains(serviceLevel);
			}
		}

		return false;
	}

	private BigDecimal calculateAggregatedWeight(BigDecimal totalCommissionEligible, BigDecimal commissionEligibleSum) {
		if (totalCommissionEligible.equals(BigDecimal.ZERO)) {
			return BigDecimal.ZERO;
		}

		return BigDecimal.valueOf(commissionEligibleSum.doubleValue() / totalCommissionEligible.doubleValue());
	}

	private BigDecimal calculatePoints(BigDecimal aggregatedWeight, int ownershipPercent) {
		return aggregatedWeight.multiply(BigDecimal.valueOf(ownershipPercent / 100));
	}

	private void buildMathForBreakdownHeader(StringBuilder mathForBreakdown, Set<FeeClass> services) {
		mathForBreakdown.append("Math for Breakdown").append(Strings.NEW_LINE);
		mathForBreakdown.append("Client Site").append(",");
		mathForBreakdown.append("Name").append(",");
		mathForBreakdown.append("Role").append(",");
		mathForBreakdown.append("Ownership (%)").append(",");

		for (FeeClass feeClass : services) {
			mathForBreakdown.append(feeClass.name()).append(" (total amt from invoice)").append(",");
			mathForBreakdown.append(feeClass.name()).append(" (total sites using service)").append(",");
			mathForBreakdown.append("Weighted ").append(feeClass.name())
					.append(" (total amount from invoice / total sites using service)").append(",");
		}

		mathForBreakdown.append("Sum of Commission Eligible Services for Client Site").append(",");
		mathForBreakdown.append("Total Commission Eligible Services").append(",");
		mathForBreakdown.append(
				"Aggregated Weight (sum of per client site weighted services / Total Commission Eligible Services)")
				.append(",");
		mathForBreakdown.append("Points (Aggregated Weight * Account User's Ownership)").append(",");
		mathForBreakdown.append("Revenue Credit (Points * Total Commission Eligible Services * Ownership)");

		mathForBreakdown.append(Strings.NEW_LINE);
	}

}
