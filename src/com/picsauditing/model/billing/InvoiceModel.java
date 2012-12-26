package com.picsauditing.model.billing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.salecommission.invoice.strategy.CommissionAudit;
import com.picsauditing.search.CommissionAuditRowMapper;
import com.picsauditing.search.Database;
import com.picsauditing.search.IntegerQueryMapper;
import com.picsauditing.util.Strings;


public final class InvoiceModel {
	
	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDAO;
	
	private static final Logger logger = LoggerFactory.getLogger(InvoiceModel.class);
	
	public Invoice findInvoiceById(int invoiceId) {
		return invoiceDAO.find(invoiceId);
	}
	
	public List<CommissionDetail> getCommissionDetails(Invoice invoice) {
		List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findByInvoiceId(invoice.getId());
		if (CollectionUtils.isEmpty(invoiceCommissions)) {
			return Collections.emptyList();
		}
		
		List<CommissionAudit> commissionAudits = findCommissionAudits(invoice.getId());
		Map<Integer, List<FeeClass>> clientServices = getClientSiteServiceLevels(commissionAudits);
		
		List<CommissionDetail> commissionDetails = new ArrayList<CommissionDetail>();
		for (InvoiceCommission invoiceCommission : invoiceCommissions) {
			CommissionDetail commissionDetail = buildCommissionDetail(invoice, invoiceCommission, clientServices);			
			commissionDetails.add(commissionDetail);
		}
		
		sortCommissionDetailByRole(commissionDetails);
		
		return commissionDetails;
	}
	
	private CommissionDetail buildCommissionDetail(Invoice invoice, InvoiceCommission invoiceCommission, 
			Map<Integer, List<FeeClass>> clientServices) {
		
		AccountUser accountUser = invoiceCommission.getAccountUser();
		
		CommissionDetail commissionDetail = new CommissionDetail();
		commissionDetail.setAccountRepresentativeName(accountUser.getUser().getName());
		commissionDetail.setClientSite(accountUser.getAccount().getName());
		commissionDetail.setClientSiteId(accountUser.getAccount().getId());
		commissionDetail.setPoints(invoiceCommission.getPoints().doubleValue());
		commissionDetail.setRevenue(invoiceCommission.getRevenuePercent().doubleValue() * invoice.getTotalCommissionEligibleInvoice(false).doubleValue());
		commissionDetail.setRole(invoiceCommission.getAccountUser().getRole());
		commissionDetail.setServiceLevels(getServiceLevels(clientServices, invoiceCommission));					
		commissionDetail.setWeight(invoiceCommission.getAccountUser().getOwnerPercent());
		
		return commissionDetail;
	}
	
	private String getServiceLevels(Map<Integer, List<FeeClass>> clientServices, InvoiceCommission invoiceCommission) {
		if (MapUtils.isEmpty(clientServices)) {
			return Strings.EMPTY_STRING;
		}
		
		List<FeeClass> feeClasses = clientServices.get(invoiceCommission.getAccountUser().getAccount().getId());
		return Strings.implode(feeClasses, ", ");
	}
	
	private void sortCommissionDetailByRole(final List<CommissionDetail> commissionDetails) {
		Collections.sort(commissionDetails, new Comparator<CommissionDetail>() {			

			@Override
			public int compare(CommissionDetail o1, CommissionDetail o2) {
				if (o1 != null && o2 != null && o1.getRole() != null && o2.getRole() != null) {
					return o1.getRole().getDescription().compareToIgnoreCase(o2.getRole().getDescription());
				}
				
				return 0;
			}
			
		});
	}
	
	public Map<Integer, List<FeeClass>> getClientSiteServiceLevels(List<CommissionAudit> commissionAudits) {
		Map<Integer, List<FeeClass>> clientSiteServiceLevels = new HashMap<Integer, List<FeeClass>>();
		
		if (CollectionUtils.isEmpty(commissionAudits)) {
			return clientSiteServiceLevels;
		}
		
		for (CommissionAudit commissionAudit : commissionAudits) {
			Integer key = commissionAudit.getClientSiteId();
			List<FeeClass> feeClasses = clientSiteServiceLevels.get(key);
			if (CollectionUtils.isEmpty(feeClasses)) {
				feeClasses = new ArrayList<FeeClass>(Arrays.asList(commissionAudit.getFeeClass()));
				clientSiteServiceLevels.put(key, feeClasses);
			} else {
				feeClasses.add(commissionAudit.getFeeClass());
			}
		}
		
		return clientSiteServiceLevels;
	}
	
	public Map<FeeClass, Integer> getNumberOfSitesUsingService(List<CommissionAudit> commissionAudits) {
		Map<FeeClass, Integer> sitesUsingService = new HashMap<FeeClass, Integer>();		
		if (CollectionUtils.isEmpty(commissionAudits)) {
			return sitesUsingService;
		}
		
		for (CommissionAudit commissionAudit : commissionAudits) {
			FeeClass feeClass = commissionAudit.getFeeClass();
			if (sitesUsingService.containsKey(feeClass)) {
				int value = sitesUsingService.get(feeClass);
				sitesUsingService.put(feeClass, ++value);
			} else {
				sitesUsingService.put(feeClass, 1);
			}
		}
		
		return sitesUsingService;
	}
	
	public List<CommissionAudit> findCommissionAudits(int invoiceId) {
		List<CommissionAudit> commissionAudits = null;
		try {
			commissionAudits = Database.select("SELECT * FROM commission_audit ca WHERE ca.invoiceID = ?", invoiceId, 
					new IntegerQueryMapper(), new CommissionAuditRowMapper());
		} catch (Exception e) {
			logger.error("An error occurred while searching for clientSiteServiceLevels.", e);
		}
		
		return commissionAudits;
	}
	
	/**
	 * Returns the list of all available fees for an invoice.
	 * 
	 * @return
	 */
	public List<InvoiceFee> getFeeList() {
		List<InvoiceFee> invoiceFees = Collections.emptyList();
		try {
			invoiceFees= invoiceFeeDAO.findWhere(InvoiceFee.class, "t.visible = true", 100);
		} catch (Exception e) {
			logger.error("Error while retrieving the invoiceFeeList", e);
		}

		return invoiceFees;
	}

}
