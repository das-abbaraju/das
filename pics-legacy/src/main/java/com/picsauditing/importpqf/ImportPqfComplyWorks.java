package com.picsauditing.importpqf;

import com.picsauditing.jpa.entities.AuditExtractOption;
import com.picsauditing.jpa.entities.AuditType;

public class ImportPqfComplyWorks extends ImportPqf {

	@Override
	public int getAuditType() {
		return AuditType.COMPLYWORKS_PQF;
	}

	@Override
	protected String preprocessPage(int page, String text) {
		text = text.replace('', '\''); // replace special quote characters
		text = text.replace('', '\'');
		text = text.replaceAll("[^\\x00-\\x7F]", " "); // replace non-ASCII with spaces
		
		int startIndex = text.indexOf("distribution is prohibited.");
		int endIndex = text.lastIndexOf("Page");
	
		if (startIndex < 0) {
			startIndex = 0;
		} else {
			startIndex += "distribution is prohibited.".length();
		}
		if (endIndex < 0) {
			endIndex = text.length();
		}

		return text.substring(startIndex, endIndex);
	}

	@Override
	protected boolean isMatchPQFSpecific(String question, String match) {
		return (match.indexOf(question) >= 0);
	}
	
	@Override
	protected boolean isValidResponse(AuditExtractOption option, String response) {
		if (response == null || response.length() == 0)
			return false;
		if (response.toLowerCase().contains("no answer"))
			return false;
		if (response.toLowerCase().contains("not answered"))
			return false;
		if (response.toLowerCase().contains("none"))
			return false;
		return true;
	}

	@Override
	protected void processAnswer(AuditExtractOption option, String response) {
		int index = response.indexOf("comment: ");
		if (index >= 0) {
			response = response.substring(0, index);
		}
		option.setAnswer(response);
	}

}
