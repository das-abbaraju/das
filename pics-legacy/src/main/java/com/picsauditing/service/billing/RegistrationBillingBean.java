package com.picsauditing.service.billing;

import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.*;

import java.util.Arrays;
import java.util.List;

public class RegistrationBillingBean {

    private final InvoiceFeeDAO invoiceFeeDAO;

    public RegistrationBillingBean(InvoiceFeeDAO dao) {
        this.invoiceFeeDAO = dao;
    }

    public ContractorAccount assessInitialFees(ContractorAccount registrant) {

        // Default their current membership to 0
		List<FeeClass> feeClasses = Arrays.asList(FeeClass.BidOnly, FeeClass.ListOnly, FeeClass.DocuGUARD,
                FeeClass.AuditGUARD, FeeClass.InsureGUARD, FeeClass.EmployeeGUARD);
		for (FeeClass feeClass : feeClasses) {
			ContractorFee newConFee = new ContractorFee();
			newConFee.setAuditColumns(new User(User.CONTRACTOR));
			newConFee.setContractor(registrant);

			InvoiceFee currentFee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(feeClass, 0);
			newConFee.setCurrentLevel(currentFee);
			newConFee.setNewLevel(currentFee);
			newConFee.setFeeClass(feeClass);
			registrant.getFees().put(feeClass, newConFee);
		}

        return registrant;
    }
}
