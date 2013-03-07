package com.picsauditing.flags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;

public class ContractorScore {
    public static final int BASE_SCORE = 500;

	static public void calculate(ContractorAccount contractor) {
		float score = BASE_SCORE;
		List<ContractorAudit> roseburgAudits = new ArrayList<ContractorAudit>();
		for (ContractorAudit conAudit : contractor.getAudits()) {
			if (!conAudit.isExpired()) {
				int numOperators = conAudit.getOperatorsVisible().size();
				if (conAudit.getAuditType().isPqf()) {
					for (ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
						float pqfScore = 0;
						if (cao.getStatus().isComplete() || cao.getStatus().isResubmit()
								|| cao.getStatus().isResubmitted())
							pqfScore = 100;
						else if (cao.getStatus().isSubmitted())
							pqfScore = 75;
						else if (cao.getStatus().isPending())
							pqfScore = cao.getPercentComplete() / 2.0f;

						score += pqfScore / numOperators;
					}
				} else if (conAudit.getAuditType().isAnnualAddendum() || conAudit.getAuditType().isDesktop()
						|| conAudit.getAuditType().isImplementation()) {
					int scorePossible = 0;
					if (conAudit.getAuditType().isAnnualAddendum()) {
						int year = DateBean.getCurrentYear();
						if (Integer.parseInt(conAudit.getAuditFor()) == year - 1)
							scorePossible = 25;
						else if (Integer.parseInt(conAudit.getAuditFor()) == year - 2)
							scorePossible = 20;
						else if (Integer.parseInt(conAudit.getAuditFor()) == year - 3)
							scorePossible = 15;
					} else if (conAudit.getAuditType().isDesktop() || conAudit.getAuditType().isImplementation()) {
						scorePossible = 100;
					}

					for (ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
						if (cao.getStatus().isComplete())
							score += (float) scorePossible / numOperators;
					}
				} else if (conAudit.getAuditType().getId() == 126 || conAudit.getAuditType().getId() == 172
						|| conAudit.getAuditType().getId() == 173) {
					// Save these to find the 3 most recent
					roseburgAudits.add(conAudit);
				}
			}
		}

		// Calculate Roseburg Audits (MAX of 50)
		Collections.sort(roseburgAudits, new Comparator<ContractorAudit>() {
			public int compare(ContractorAudit o1, ContractorAudit o2) {
				return o1.getCreationDate().compareTo(o2.getCreationDate());
			}
		});

		if (roseburgAudits.size() > 0) {
			List<ContractorAudit> mostRecentRoseburgAudits = roseburgAudits.subList(0,
					Math.min(3, roseburgAudits.size()));
			int roseburgTotal = 0;
			for (ContractorAudit roseburgAudit : mostRecentRoseburgAudits) {
				roseburgTotal += roseburgAudit.getScore();
			}
			score += ((float) roseburgTotal / mostRecentRoseburgAudits.size()) / 2;
		}

		int scoreRounded = Math.round(score);
		contractor.setScore(scoreRounded);
	}
}
