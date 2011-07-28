package com.picsauditing.auditBuilder;

import java.util.HashSet;
import java.util.Set;

import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Trade;

public class RuleFilter {
	public Set<LowMedHigh> safetyRisks = new HashSet<LowMedHigh>();
	public Set<LowMedHigh> productRisks = new HashSet<LowMedHigh>();
	public Set<ContractorType> contractorType = new HashSet<ContractorType>();
	public Set<Boolean> soleProprietors = new HashSet<Boolean>();
	public Set<AccountLevel> accountLevels = new HashSet<AccountLevel>();
	public Set<Trade> trades = new HashSet<Trade>();
	public Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
	public Set<AuditType> auditTypes = new HashSet<AuditType>();

	public RuleFilter(ContractorAccount contractor) {
		safetyRisks.add(null);
		safetyRisks.add(contractor.getSafetyRisk());

		productRisks.add(null);
		productRisks.add(contractor.getProductRisk());

		contractorType.add(null);
		contractorType.addAll(contractor.getAccountTypes());

		soleProprietors.add(null);
		soleProprietors.add(contractor.getSoleProprietor());

		accountLevels.add(null);
		accountLevels.add(contractor.getAccountLevel());

		trades.add(null);
		HashSet<Trade> allTrades = new HashSet<Trade>();
		for (ContractorTrade ct : contractor.getTrades()) {
			Trade trade = ct.getTrade();
			while (trade != null) {
				allTrades.add(trade);
				trade = trade.getParent();
			}
		}
		for (Trade trade : allTrades) {
			trades.add(trade);
		}

		operators.add(null);
		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			operators.add(co.getOperatorAccount());
			// adding parent facilities
			for (Facility f : co.getOperatorAccount().getCorporateFacilities()) {
				operators.add(f.getCorporate());
			}
		}
	}
	
	public void addAuditType(AuditType auditType) {
		auditTypes.add(null);
		auditTypes.add(auditType);
	}
}
