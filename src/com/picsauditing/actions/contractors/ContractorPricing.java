package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.Anonymous;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;

@SuppressWarnings("serial")
public class ContractorPricing extends ContractorActionSupport {
	private ContractorAccount con;
	private int id;
	private int employeeGUARDNum;
	private int docuGUARDNum;
	private int insureGUARDNum;
	private int auditGUARDNum;
	private Map<FeeClass, ContractorFee> fees;

	private Map<String, BigDecimal> prices = new HashMap<String, BigDecimal>();

	@Override
	@Anonymous
	public String execute() {
		if (con != null) {
			fees = con.getFees();
			docuGUARDNum = fees.get(FeeClass.DocuGUARD).getNewLevel().getMinFacilities();
			insureGUARDNum = fees.get(FeeClass.InsureGUARD).getNewLevel().getMinFacilities();
			auditGUARDNum = fees.get(FeeClass.AuditGUARD).getNewLevel().getMinFacilities();
			employeeGUARDNum = fees.get(FeeClass.EmployeeGUARD).getNewLevel().getMinFacilities();
		}

		@SuppressWarnings("unchecked")
		List<InvoiceFee> list = (List<InvoiceFee>) dao.findWhere(InvoiceFee.class,
				"visible=1 and feeClass in ('AuditGUARD','InsureGUARD','DocuGUARD','EmployeeGUARD', 'Activation')", 0);
		for (InvoiceFee fee : ((List<InvoiceFee>) list)) {
			prices.put("" + fee.getMinFacilities() + fee.getFeeClass(), con.getCountry().getAmount(fee));
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
		if (amount == null)
			amount = new BigDecimal(0);

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
