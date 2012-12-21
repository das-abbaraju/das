package com.picsauditing.salecommission.invoice.strategy;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.util.Strings;

public class ContractorInvoiceStateBuilder {
	
	@Autowired
	private BasicDAO dao;
	
	private static Set<String> UPGRADE_DESCRIPTIONS = null;
	
	public void init() {
		UPGRADE_DESCRIPTIONS = Collections.unmodifiableSet(getAllUpgradeDescriptions());
	}
	
	private Set<String> getAllUpgradeDescriptions() {
		List<AppTranslation> translations = dao.findWhere(AppTranslation.class, "t.key IN ('Invoice.UpgradingFrom', 'Invoice.UpgradingTo')");
		if (CollectionUtils.isEmpty(translations)) {
			return Collections.emptySet();
		}
		
		Set<String> upgradeDescriptions = new HashSet<String>();
		for (AppTranslation translation : translations) {
			String translationValue = translation.getValue();
			if (Strings.isNotEmpty(translationValue)) {
				upgradeDescriptions.add(stripOutStringFormatting(translationValue));
			}
		}
		
		return upgradeDescriptions;		
	}
	
	private String stripOutStringFormatting(String s) {
		int index = s.indexOf("{");
		if (index > 0) {
			return s.substring(0, index);
		}
		
		return s;
	}
	
	public ContractorInvoiceState buildCommandObject(Invoice invoice) {
		ContractorInvoiceState contractorState = new ContractorInvoiceState();		
		if (invoice == null || CollectionUtils.isEmpty(invoice.getItems())) {
			return contractorState;
		}
		
		contractorState.setInvoice(invoice);
		for (InvoiceItem invoiceItem : invoice.getItems()) {
			if (invoiceItem == null || invoiceItem.getInvoiceFee() == null) {
				continue;
			}
			
			if (invoiceItem.getInvoiceFee().isActivation()) {
				contractorState.setActivation(true);
			} else if (isUpgradeInvoiceItem(invoiceItem)) {
				contractorState.setUpgrade(true);
			} else if (invoiceItem.getInvoiceFee().isReactivation()) {
				contractorState.setReactivation(true);
			}
		}
		
		contractorState.setRenewal(isContractorRenewing(contractorState));
		
		if (contractorState.isRenewal() || contractorState.isUpgrade() || contractorState.isReactivation()) {
			contractorState.setPaymentExpiresDate(findPaymentExpiresDate(invoice));
		}
		
		return contractorState;
	}
	
	private Date findPaymentExpiresDate(Invoice invoice) {
		return DateBean.addDays(new Date(), -410);
//		ContractorAccount contractor = (ContractorAccount) invoice.getAccount();
//		if (contractor == null) {
//			return null;
//		}
//		
//		String query = "t.account.id = " + contractor.getId() + " AND t.status = 'Paid' ORDER BY t.creationDate DESC";
//		List<Invoice> invoices = dao.findWhere(Invoice.class, query, 1);
//		if (CollectionUtils.isEmpty(invoices)) {
//			return null;
//		}
//
//		for (Invoice previousInvoice : invoices) {
//			if (previousInvoice != null && previousInvoice.getId() != invoice.getId()) {
//				Date expiresDate = findInvoiceItemWithPaymentExpiresDate(previousInvoice);
//				if (expiresDate != null)  {
//					return expiresDate;
//				}
//			}
//		}
//		
//		return null;
	}
	
	private boolean isUpgradeInvoiceItem(InvoiceItem invoiceItem) {
		Set<String> upgradeDescriptions = UPGRADE_DESCRIPTIONS;
		if (CollectionUtils.isEmpty(upgradeDescriptions) || Strings.isEmpty(invoiceItem.getDescription())) {
			return false;
		}
		
		for (String upgradeDescription : upgradeDescriptions) {
			if (invoiceItem.getDescription().startsWith(upgradeDescription)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isContractorRenewing(ContractorInvoiceState contractorState) {
		ContractorAccount contractor = (ContractorAccount) contractorState.getInvoice().getAccount();
		if (contractor.getStatus().isActive() && !contractorState.isActivation() && !contractorState.isReactivation()
				&& !contractorState.isUpgrade()) {
			return true;
		}
		
		return false;
	}
	
	public void resetContractor(ContractorAccount contractor, ContractorInvoiceState contractorState) {
		resetForActivation(contractor, contractorState);
		resetForRenewal(contractor, contractorState);
		resetForUpgrade(contractor, contractorState);
	}
	
	private void resetForActivation(ContractorAccount contractor, ContractorInvoiceState contractorState) {
		if (contractorState.isActivation()) {
			contractor.setMembershipDate(null);
			contractor.setPaymentExpires(null);
		}
	}
	
	/*
	 * I am currently leaving the two methods, even though they both do the same thing, because
	 * we may determine through testing that more needs to be done to the contractor account
	 * for these two cases.
	 */
	
	private void resetForRenewal(ContractorAccount contractor, ContractorInvoiceState contractorState) {
		if (contractorState.isRenewal()) {
			contractor.setPaymentExpires(contractorState.getPaymentExpiresDate());
			contractor.setRenew(true);
		}
	}
	
	private void resetForUpgrade(ContractorAccount contractor, ContractorInvoiceState contractorState) {
		if (contractorState.isUpgrade()) {
			contractor.setPaymentExpires(contractorState.getPaymentExpiresDate());
		}
	}
	
	private Date findInvoiceItemWithPaymentExpiresDate(Invoice invoice) {
		if (CollectionUtils.isEmpty(invoice.getItems())) {
			return null;
		}
		
		for (InvoiceItem invoiceItem : invoice.getItems()) {
			if (invoiceItem.getPaymentExpires() != null) {
				return invoiceItem.getPaymentExpires();
			}
		}
		
		return null;
	}

}
