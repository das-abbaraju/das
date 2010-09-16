package com.picsauditing.actions.operators;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.actions.audits.ContractorAuditsWidget;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.WaitingOn;

@SuppressWarnings("serial")
public class WynnewoodRatesWidget extends ContractorAuditsWidget {
	private ContractorAuditDAO conAuditDAO;
	
	public WynnewoodRatesWidget(ContractorAuditOperatorDAO dao, ContractorAuditDAO conAuditDAO) {
		super(dao);
		this.conAuditDAO = conAuditDAO; 
	}

	public Map<ContractorAudit, ContractorAudit> getContractRates() {
		List<ContractorAudit> waitingOnContracts = conAuditDAO.findAuditsByOperator(
				permissions.getAccountId(), 89, WaitingOn.Operator);
		List<ContractorAudit> rates = conAuditDAO.findAuditsByOperator(permissions
				.getAccountId(), 79, WaitingOn.Operator);
		Map<ContractorAudit, ContractorAudit> contractRateMap = new TreeMap<ContractorAudit, ContractorAudit>(getCaComparator());

		// Creating Contract->Rate Map of all Pending Contracts where status is waiting on Operator
		for (ContractorAudit contract : waitingOnContracts)
			for (ContractorAudit rate : rates)
				if (rate.getContractorAccount().equals(contract.getContractorAccount()))
					contractRateMap.put(contract, rate);

		// Going through Contract and checking answers
		if (permissions.hasGroup(26996)) { // Purchasing Admin
			Map<ContractorAudit, ContractorAudit> kevinsAudits = new TreeMap<ContractorAudit, ContractorAudit>(getCaComparator());
			// Checking answers related to Purchasing
			for (ContractorAudit contract : waitingOnContracts) {
				boolean uploaded = false, kname = false, kapproved = false;
				
				if (contract.getData().size() < 1)
					kevinsAudits.put(contract, contractRateMap.get(contract));
				else {
					for (AuditData question : contract.getData()) {
						switch (question.getQuestion().getId()) {
							case 3473: uploaded = question.isAnswered(); break;
							case 3813: kname = question.isAnswered(); break;
							case 3814: kapproved = question.isAnswered(); break;
							default: break;
						}
					}

					// Whittling Contract->Rate Map to only what Purchasing needs to see
					if (!uploaded || !kname || !kapproved)
						kevinsAudits.put(contract, contractRateMap.get(contract));
				}
			}
			return kevinsAudits;
		} else if (permissions.hasGroup(26995)) { // Maintenance Admin
			Map<ContractorAudit, ContractorAudit> tomsAudits = new TreeMap<ContractorAudit, ContractorAudit>(getCaComparator());
			// Checking answers related to Maintenance
			for (ContractorAudit contract : waitingOnContracts) {
				boolean uploaded = false, kname = false, kapproved = false, tname = false, tapproved = false;
				
				for (AuditData question : contract.getData()) {
					switch (question.getQuestion().getId()) {
						case 3473: uploaded = question.isAnswered(); break;
						case 3813: kname = question.isAnswered(); break;
						case 3814: kapproved = question.isAnswered(); break;
						case 3815: tname = question.isAnswered(); break;
						case 3816: tapproved = question.isAnswered(); break;
						default: break;
					}
				}

				// Whittling Contract->Rate Map to only what Maintenance needs to see
				if (uploaded && kname && kapproved && (!tname || !tapproved))
					tomsAudits.put(contract, contractRateMap.get(contract));
			}
			return tomsAudits;
		}
		return contractRateMap;
	}
	
	public Comparator<ContractorAudit> getCaComparator() {
		return new Comparator<ContractorAudit>() {
			@Override
			public int compare(ContractorAudit a1, ContractorAudit a2) {
				return a1.getContractorAccount().getName().compareTo(a2.getContractorAccount().getName());
			}
		};
	}
}
