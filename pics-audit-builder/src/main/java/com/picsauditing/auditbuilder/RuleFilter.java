package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.AccountService;
import com.picsauditing.auditbuilder.service.TradeService;

import java.util.HashSet;
import java.util.Set;

public class RuleFilter {
    public Set<Boolean> safetySensitives = new HashSet<>();
	public Set<LowMedHigh> safetyRisks = new HashSet<>();
	public Set<LowMedHigh> productRisks = new HashSet<>();
    public Set<LowMedHigh> tradeSafetyRisks = new HashSet<>();
	public Set<ContractorType> contractorType = new HashSet<>();
	public Set<Boolean> soleProprietors = new HashSet<>();
	public Set<AccountLevel> accountLevels = new HashSet<>();
	public Set<Trade> trades = new HashSet<>();
	public Set<OperatorAccount> operators = new HashSet<>();
	public Set<AuditType> auditTypes = new HashSet<>();

	public RuleFilter(ContractorAccount contractor) {
        safetySensitives.add(null);
        safetySensitives.add(contractor.isSafetySensitive());

		safetyRisks.add(null);
		safetyRisks.add(contractor.getSafetyRisk());

		productRisks.add(null);
		productRisks.add(contractor.getProductRisk());

		contractorType.add(null);
		contractorType.addAll(AccountService.getAccountTypes(contractor));

		soleProprietors.add(null);
		soleProprietors.add(contractor.getSoleProprietor());

		accountLevels.add(null);
		accountLevels.add(contractor.getAccountLevel());

		trades.add(null);
        tradeSafetyRisks.add(null);
		for (ContractorTrade ct : contractor.getTrades()) {
			trades.add(ct.getTrade());
            tradeSafetyRisks.add(TradeService.getSafetyRiskI(ct.getTrade()));
		}

		operators.add(null);
		for (ContractorOperator co : AccountService.getNonCorporateOperators(contractor)) {
			operators.add(co.getOperatorAccount());
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