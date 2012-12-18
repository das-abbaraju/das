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
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.salecommission.invoice.strategy.CommissionAudit;
import com.picsauditing.search.CommissionAuditRowMapper;
import com.picsauditing.search.Database;
import com.picsauditing.search.IntegerQueryMapper;
import com.picsauditing.util.Strings;


public final class InvoiceModel {
	
	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDAO;
	
	private static final Logger logger = LoggerFactory.getLogger(InvoiceModel.class);
	
	public List<CommissionDetail> getCommissionDetails(Invoice invoice) {
		List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findByInvoiceId(invoice.getId());
		if (CollectionUtils.isEmpty(invoiceCommissions)) {
			return Collections.emptyList();
		}
		
		Map<Integer, List<FeeClass>> clientServices = getClientSiteServiceLevels(invoice.getId());
		
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
		commissionDetail.setPoints(invoiceCommission.getPoints());
		commissionDetail.setRevenue(invoiceCommission.getRevenuePercent() * invoice.getTotalCommissionEligibleInvoice(false).doubleValue());
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
	
	private Map<Integer, List<FeeClass>> getClientSiteServiceLevels(int invoiceId) {
		Map<Integer, List<FeeClass>> clientSiteServiceLevels = new HashMap<Integer, List<FeeClass>>();
		
		List<CommissionAudit> commissionAudits = findCommissionAudits(invoiceId);
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
	
	private List<CommissionAudit> findCommissionAudits(int invoiceId) {
		List<CommissionAudit> commissionAudits = null;
		try {
			commissionAudits = Database.select("SELECT * FROM commission_audit ca WHERE ca.invoiceID = ?", invoiceId, 
					new IntegerQueryMapper(), new CommissionAuditRowMapper());
		} catch (Exception e) {
			logger.error("An error occurred while searching for clientSiteServiceLevels.", e);
		}
		
		return commissionAudits;
	}

}
