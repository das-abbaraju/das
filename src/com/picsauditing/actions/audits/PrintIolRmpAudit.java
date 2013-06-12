package com.picsauditing.actions.audits;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class PrintIolRmpAudit extends PicsActionSupport {

	private ContractorAudit audit;
	private Map<Integer, String> answerMap = new HashMap<Integer, String>();


	public String execute(){
		for (AuditData answer: audit.getData()) {
			answerMap.put(answer.getId(), answer.getAnswer());
		}
		return SUCCESS;
	}

	public String getAnswer(int questionId) {
		return answerMap.get(questionId);
	}

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}


}
