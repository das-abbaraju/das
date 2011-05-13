package com.picsauditing.jpa.entities;

public enum ContractorRegistrationStep {
	Register, EditAccount, Trades, Risk, Facilities, Payment, Confirmation, Done;

	// private String url;

	static public ContractorRegistrationStep getStep(
			ContractorAccount contractor) {
		ContractorRegistrationStep step = EditAccount;
		
		if (contractor == null || contractor.getId() == 0)
			return Register;
        //if (!permissions.isLoggedIn()) return Register;
        
        if (contractor.getId() != 0) step = Trades;
        if (contractor.getTradesUpdated() != null) step = Risk;
        if (contractor.getSafetyRisk() != null) step = Facilities;
        
        if (!contractor.isMaterialSupplier() 
				  && !contractor.isOnsiteServices() 
				  && !contractor.isOffsiteServices()) step = Facilities;
        
        
		return Done;
	}
}
