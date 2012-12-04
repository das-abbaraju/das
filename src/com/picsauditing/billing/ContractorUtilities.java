package com.picsauditing.billing;

import java.math.BigDecimal;

import javax.persistence.Transient;

import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.util.SpringUtils;

public class ContractorUtilities {

	private ContractorUtilities() { }
	
	public static void clearNewFee(ContractorAccount contractor, FeeClass feeClass) {
		InvoiceFeeDAO feeDAO = SpringUtils.getBean("InvoiceFeeDAO");
		InvoiceFee invoiceFee = feeDAO.findByNumberOfOperatorsAndClass(feeClass, 0);
		
		contractor.getFees().get(feeClass).setNewLevel(invoiceFee);
		contractor.getFees().get(feeClass).setNewAmount(BigDecimal.ZERO);
	}
	
	public void setNewFee(ContractorAccount contractor, InvoiceFee fee, BigDecimal amount) {
		contractor.getFees().get(fee.getFeeClass()).setNewLevel(fee);
		contractor.getFees().get(fee.getFeeClass()).setNewAmount(amount);
	}
	
}
