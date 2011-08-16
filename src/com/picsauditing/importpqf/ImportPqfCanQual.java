package com.picsauditing.importpqf;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditExtractOption;
import com.picsauditing.jpa.entities.AuditTransformOption;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.AnswerMap;

public class ImportPqfCanQual extends ImportPqf {

	@Override
	public int getAuditType() {
		return AuditType.CAN_QUAL_PQF;
	}

	@Override
	protected String preprocessPage(int page, String text) {
		text = text.replace('', '\''); // replace special quote characters
		text = text.replace('', '\'');
		text = text.replaceAll("[^\\x00-\\x7F]", " "); // replace non-ASCII with spaces
		
		int endIndex = text.lastIndexOf("Page");
		if (endIndex > -1)
			text = text.substring(0, endIndex);
		
		if (page == 1) {
			endIndex = text.lastIndexOf("Questionnaire > Print Questionnaire");
			if (endIndex > -1)
				text = text.substring(0, endIndex);
		}
	
		return text;
	}

	@Override
	protected boolean isValidResponse(AuditExtractOption option, String response) {
		if (response == null || response.length() == 0)
			return false;
		if (response.toLowerCase().contains("no answer"))
			return false;
		if (response.toLowerCase().contains("none"))
			return false;
		return true;
	}

	@Override
	protected String processAnswer(AuditTransformOption option, String response, AnswerMap auditAnswers) {
		if (option.getSourceQuestion().getId() == 8397 && option.getDestinationQuestion().getId() == 72) {
			AuditData data = auditAnswers.get(option.getSourceQuestion().getId());
			if (data != null && data.getAnswer() != null) {
				String[] lines = data.getAnswer().split("\\n");
				for (String line:lines) {
					int presIndex = line.indexOf("President");
					int viceIndex = line.indexOf("Vice");
					if (presIndex > 0 && viceIndex < 0) {
						return line.substring(0, presIndex).trim();
					}
				}
			}
			
			return null;
		}

		if (response == null)
			return response;
		
		if (option.getSourceQuestion().getId() == 8427 && option.getDestinationQuestion().getId() == 103) {
			AuditData data = auditAnswers.get(option.getSourceQuestion().getId());
			if (data != null)
				response = data.getAnswer();
			String[] values = response.split(" ");
			for (String item:values) {
				if (Integer.parseInt(item) > 0)
					return "Yes";
			}
			return "No";
		}
		
		return response;
	}

}
