package com.picsauditing.util;

import java.io.IOException;
import java.util.Map;

import com.picsauditing.jpa.entities.ContractorAudit;

public class AuditQuestionInterpreter {
	private VelocityAdaptor adaptor;
	private Map<String, Object> data;
	
	public AuditQuestionInterpreter(ContractorAudit conAudit) {
		data.put("conAudit", conAudit);
	}
	
	public String convertQuestion(String question) {
		if (Strings.isEmpty(question))
			return question;
		// See if there are any velocity tags in this at all
		// Search for this string ${tag} anywhere in the text
		// It may be faster to just always send it to the velocity engine
		if (!question.matches(".*(\\$\\{.+\\}).*"))
			return question;
		try {
			question = adaptor.merge(question, data);
		} catch (IOException | TemplateParseException e) {
			e.printStackTrace();
		}
		return question;
	}
}
