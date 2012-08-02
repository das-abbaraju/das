package com.picsauditing.model.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AuditDataSaveEventListener implements ApplicationListener<AuditDataSaveEvent> {
	private final Logger logger = LoggerFactory.getLogger(AuditDataSaveEventListener.class);

	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;

	@Override
	public void onApplicationEvent(AuditDataSaveEvent event) {
		AuditData auditData = (AuditData) event.getSource();

		if (clockNeedsReseting(auditData)) {
			resetClock(getApplicableOperators(auditData));
		}
	}

	private boolean clockNeedsReseting(AuditData auditData) {
		// currently the event is only fired when question is answered for the
		// first time
		// we can add logic here if need be later

		return true;
	}

	private Set<ContractorOperator> getApplicableOperators(AuditData auditData) {
		Set<Integer> operatorIds = new HashSet<Integer>();
		List<ContractorAuditOperator> caos = auditData.getAudit().getOperatorsVisible();
		List<ContractorAuditOperatorPermission> caops = new ArrayList<ContractorAuditOperatorPermission>();

		for (ContractorAuditOperator cao : caos) {
			caops.addAll(cao.getCaoPermissions());
		}

		for (ContractorAuditOperatorPermission caop : caops) {
			OperatorAccount operator = caop.getOperator();
			if (operator.getType().equals("Operator")) {
				operatorIds.add(caop.getOperator().getId());
			}
		}

		return contractorOperatorDAO.findForOperators(auditData.getAudit().getContractorAccount().getId(), operatorIds);
	}

	private void resetClock(Set<ContractorOperator> contractorOperators) {
		for (ContractorOperator contractorOperator : contractorOperators) {
			contractorOperator.setLastStepToGreenDate(new Date());
		}
	}
}
