package com.picsauditing.auditBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.SpringUtils;

public abstract class AuditBuilderBase {
	protected ContractorAccount contractor;
	protected Set<ContractorType> contractorTypes = new HashSet<ContractorType>();
	protected Set<Trade> trades = new HashSet<Trade>();
	
	public AuditBuilderBase(ContractorAccount contractor) {
		this.contractor = contractor;

		for (ContractorTrade ct : contractor.getTrades()) {
			this.trades.add(ct.getTrade());
		}
		if (this.trades.size() == 0) {
			// We have to add a blank trade in case the contractor is missing trades.
			// If we don't then no rules will be found, even wildcard trade rules.
			Trade blank = new Trade();
			blank.setId(-1);
			this.trades.add(blank);
		}
		// FIXME PICS-4324 follow-up: Replace the following code with ... 
		// contractorTypes =  contractor.getAccountTypes();
		// ... and find other places to do it, too.
		if (contractor.isOnsiteServices())
			contractorTypes.add(ContractorType.Onsite);
		if (contractor.isOffsiteServices())
			contractorTypes.add(ContractorType.Offsite);
		if (contractor.isMaterialSupplier())
			contractorTypes.add(ContractorType.Supplier);
		if (contractor.isTransportationServices())
			contractorTypes.add(ContractorType.Transportation);
	}
	
	protected Map<Integer, OperatorTag> getRequiredTags(List<? extends AuditRule> rules) {
		Map<Integer, OperatorTag> tagsNeeded = new HashMap<Integer, OperatorTag>();
		for (AuditRule rule : rules) {
			if (rule.getTag() != null)
				tagsNeeded.put(rule.getTag().getId(), null);
		}
		if (tagsNeeded.size() > 0) {
			ContractorTagDAO dao = (ContractorTagDAO) SpringUtils.getBean("ContractorTagDAO");
			List<ContractorTag> contractorTags = dao.getContractorTags(contractor.getId(), tagsNeeded.keySet());
			for (ContractorTag contractorTag : contractorTags) {
				tagsNeeded.put(contractorTag.getTag().getId(), contractorTag.getTag());
			}
		}
		return tagsNeeded;
	}
	
	protected boolean isValid(AuditRule rule, Map<Integer, AuditData> contractorAnswers, Map<Integer, OperatorTag> opTags) {
		if (rule.getQuestion() != null && !rule.isMatchingAnswer(contractorAnswers.get(rule.getQuestion().getId()))) {
			return false;
		}

		if (rule.getTag() != null && opTags.get(rule.getTag().getId()) == null) {
			return false;
		}

		return true;
	}

}
