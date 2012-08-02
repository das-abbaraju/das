package com.picsauditing.util.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.util.braintree.BrainTreeService;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.util.SpringUtils;

public class ContractorUtil {
	
	/**
	 * Enforce singleton nature of this class
	 */
	private ContractorUtil() { }
	
	public static float calculateWeightedIndustryAverage(final ContractorAccount contractor) {
		float sum = 0;
		int activitySum = 0;

		if (CollectionUtils.isNotEmpty(contractor.getTrades())) {
			if (contractor.hasSelfPerformedTrades()) {
				for (ContractorTrade trade : contractor.getTrades()) {
					if (trade.isSelfPerformed()) {
						sum += trade.getActivityPercent() * trade.getTrade().getNaicsTRIRI();
						activitySum += trade.getActivityPercent();
					}
				}
			} else {
				for (ContractorTrade trade : contractor.getTrades()) {
					sum += trade.getActivityPercent() * trade.getTrade().getNaicsTRIRI();
					activitySum += trade.getActivityPercent();
				}
			}
			return sum / activitySum;
		} else
			return 0;
	}
	
	public static String getCcNumber(final ContractorAccount contractor) {
		BrainTreeService brainTreeService = SpringUtils.getBean("BrainTreeService");
		String cardNumber = brainTreeService.getCreditCard(contractor).getCardNumber();
		return cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
	}
	
	public static List<Invoice> getSortedInvoices(final ContractorAccount contractor) {
		List<Invoice> sortedInvoiceList = new ArrayList<Invoice>(contractor.getInvoices());
		
		Collections.sort(sortedInvoiceList, new Comparator<Invoice>() {
			
			public int compare(Invoice invoiceOne, Invoice invoiceTwo) {
				return invoiceTwo.getCreationDate().compareTo(invoiceOne.getCreationDate());
			}
			
		});
		
		return sortedInvoiceList;
	} 

}
