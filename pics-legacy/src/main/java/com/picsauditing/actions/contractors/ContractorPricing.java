package com.picsauditing.actions.contractors;

import com.picsauditing.access.Anonymous;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.contractor.PricingTier;
import com.picsauditing.service.contractor.PricingTiersBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ContractorPricing extends ContractorActionSupport {

	private ContractorAccount con;
	private int id;

	@Autowired
	private PricingTiersBuilder pricingTiersBuilder;

    private List<PricingTier> pricingTiers;

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

        List<FeeClass> applicableFeeClasses = findApplicableFeeClasses(contractorFeeMap);

        pricingTiers = pricingTiersBuilder.buildPricingTiersForCountry(country, applicableFeeClasses, con.getOperatorAccounts().size());

		return SUCCESS;
	}

    private List<FeeClass> findApplicableFeeClasses(Map<FeeClass, ContractorFee> contractorFeeMap) {
        List<FeeClass> applicableFeeClasses = new ArrayList<>();

        if (contractorFeeMap.get(FeeClass.DocuGUARD).getNewLevel().getMinFacilities() >= 1) {
            applicableFeeClasses.add(FeeClass.DocuGUARD);
        }
        if (contractorFeeMap.get(FeeClass.InsureGUARD).getNewLevel().getMinFacilities() >= 1) {
            applicableFeeClasses.add(FeeClass.InsureGUARD);
        }
        if (contractorFeeMap.get(FeeClass.AuditGUARD).getNewLevel().getMinFacilities() >= 1) {
            applicableFeeClasses.add(FeeClass.AuditGUARD);
        }
        if (contractorFeeMap.get(FeeClass.EmployeeGUARD).getNewLevel().getMinFacilities() >= 1) {
            applicableFeeClasses.add(FeeClass.EmployeeGUARD);
        }

        return applicableFeeClasses;
    }

    public List<OperatorAccount> getClients() {
        return con.getOperatorAccounts();
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

	public void setId(int id) {
		this.id = id;
	}

    public List<PricingTier> getPricingTiers() {
        return pricingTiers;
    }

    public void setPricingTiers(List<PricingTier> pricingTiers) {
        this.pricingTiers = pricingTiers;
    }

}
