package com.picsauditing.importpqf;

import com.picsauditing.jpa.entities.AuditType;

public class ImportPqfCanQual extends ImportPqf {

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

}
