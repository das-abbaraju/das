package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.InvoiceFeeCountryDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFeeCountry;

@SuppressWarnings("serial")
public class ContractorPricing extends ContractorActionSupport {

	private ContractorAccount con;
	private int id;
	private int employeeGUARDNum;
	private int docuGUARDNum;
	private int insureGUARDNum;
	private int auditGUARDNum;

	@Autowired
	private InvoiceFeeCountryDAO invoiceFeeCountryDAO;

	private Map<String, BigDecimal> prices = new HashMap<String, BigDecimal>();

	@Override
	@Anonymous
	public String execute() {
		if (con == null) {
			addActionError(getText("RequestNewContractor.error.RequestedContractorNotFound"));
			return ERROR;
		}

		Map<FeeClass, ContractorFee> contractorFeeMap = con.getFees();
		if (contractorFeeMap == null) {
			addActionError(getText("Error.Contractor.NoFees"));
			return ERROR;
		}

		Country country = con.getCountry();
		if (country == null) {
			addActionError(getText("Error.Contractor.NoCountry"));
			return ERROR;
		}

		docuGUARDNum = contractorFeeMap.get(FeeClass.DocuGUARD).getNewLevel().getMinFacilities();
		insureGUARDNum = contractorFeeMap.get(FeeClass.InsureGUARD).getNewLevel().getMinFacilities();
		auditGUARDNum = contractorFeeMap.get(FeeClass.AuditGUARD).getNewLevel().getMinFacilities();
		employeeGUARDNum = contractorFeeMap.get(FeeClass.EmployeeGUARD).getNewLevel().getMinFacilities();

		Set<FeeClass> feeTypes = FeeClass.getContractorPriceTableFeeTypes();

		// Look for the specific country
		List<InvoiceFeeCountry> countryFees = invoiceFeeCountryDAO.findVisibleByCountryAndFeeClassList(country, feeTypes);

		// If that wasn't found, look up US as default
		if (CollectionUtils.isEmpty(countryFees)) {
			countryFees = invoiceFeeCountryDAO.findVisibleByCountryAndFeeClassList(new Country("US"), feeTypes);
		}

		for (InvoiceFeeCountry fee : countryFees) {
			prices.put("" + fee.getInvoiceFee().getMinFacilities() + fee.getInvoiceFee().getFeeClass(), fee.getAmount());
		}

		return SUCCESS;
	}

	public ContractorAccount getCon() {
		return con;
	}

	public void setCon(ContractorAccount con) {
		this.con = con;
	}

	public int getId() {
		return id;
	}

	public String getPrice(String priceId) {
		BigDecimal amount = prices.get(priceId);
		if (amount == null) {
			amount = new BigDecimal(0);
		}

		return amount.toString();
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEmployeeGUARDNum() {
		return employeeGUARDNum;
	}

	public int getDocuGUARDNum() {
		return docuGUARDNum;
	}

	public int getAuditGUARDNum() {
		return auditGUARDNum;
	}

	public int getInsureGUARDNum() {
		return insureGUARDNum;
	}

	public void setInsureGUARDNum(int insureGUARDNum) {
		this.insureGUARDNum = insureGUARDNum;
	}

}
