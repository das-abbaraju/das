package com.picsauditing.actions.audits;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class PrintIolRmpAudit extends PicsActionSupport {

	private ContractorAudit audit;
	private Map<Integer, AuditData> answerMap = new HashMap<>();

	public String execute(){
		for (AuditData answer: audit.getData()) {
			answerMap.put(answer.getQuestion().getId(), answer);
		}
		return SUCCESS;
	}

	public AuditData getAuditDataIfAnswerNotEmpty(int questionId) {
		AuditData auditData = answerMap.get(questionId);
		String answer = auditData.getAnswer();

		return !answer.isEmpty() ? auditData : null;
	}

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	public AuditQuestion getQuestion(int id) {
		AuditData data = answerMap.get(id);

		if (data != null) {
			return data.getQuestion();
		} else {
			for (AuditCategory category: audit.getAuditType().getCategories()) {
				for (AuditQuestion question: category.getQuestions()) {
					if (question.getId() == id) {
						return question;
					}
				}
			}
		}

		return null;
	}

	public String getTranslationKeyForAnswer(AuditData auditData) {
		return "AuditOptionGroup." + auditData.getQuestion().getOption().getId() + "." + auditData.getAnswer();
	}

	public String getTranslationKeyForTitle(int id) {
		return "AuditQuestion." + id + ".title";
	}
}
