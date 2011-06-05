package com.picsauditing.auditBuilder;

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
		if (contractor.isOnsiteServices())
			contractorTypes.add(ContractorType.Onsite);
		if (contractor.isOffsiteServices())
			contractorTypes.add(ContractorType.Offsite);
		if (contractor.isMaterialSupplier())
			contractorTypes.add(ContractorType.Supplier);
	}
	
	protected Set<OperatorTag> getRequiredTags(List<? extends AuditRule> rules) {
		Set<Integer> tagsNeeded = new HashSet<Integer>();
		for (AuditRule rule : rules) {
			if (rule.getTag() != null)
				tagsNeeded.add(rule.getTag().getId());
		}
		Set<OperatorTag> tags = new HashSet<OperatorTag>();
		if (tagsNeeded.size() > 0) {
			ContractorTagDAO dao = (ContractorTagDAO) SpringUtils.getBean("ContractorTagDAO");
			List<ContractorTag> contractorTags = dao.getContractorTags(contractor.getId(), tagsNeeded);
			for (ContractorTag contractorTag : contractorTags) {
				tags.add(contractorTag.getTag());
			}
		}
		return tags;
	}

	protected boolean isValid(AuditRule rule, Map<Integer, AuditData> contractorAnswers, Set<OperatorTag> opTags) {
		if (rule.getQuestion() != null && !rule.isMatchingAnswer(contractorAnswers.get(rule.getQuestion().getId()))) {
			return false;
		}

		if (rule.getTag() != null && !opTags.contains(rule.getTag())) {
			return false;
		}

		return true;
	}

}
