package com.picsauditing.importpqf;

import java.util.List;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;

public class ImportPqfIsn extends ImportPqf {

	@Override
	public int getAuditType() {
		return AuditType.ISN_CAN_QUAL_PQF;
	}
	
	@Override
	protected String preprocessPage(int page, String text) {
		text = text.replace('', '\''); // replace special quote characters
		text = text.replace('', '\'');
		text = text.replaceAll("[^\\x00-\\x7F]", " "); // replace non-ASCII with spaces
		
		int startIndex = text.indexOf("Response.");
	
		if (startIndex < 0) {
			startIndex = 0;
		}
		else {
			startIndex += "Response.".length();
		}

		return text.substring(startIndex);
	}

	@Override
	protected boolean isPartialMatchPQFSpecific(String question, String match) {
		int index = match.indexOf(" ");
		if (index > 0 && question.startsWith(match.substring(index + 1).trim())) return true;
		return false;
	}
	
	@Override
	protected boolean isMatchPQFSpecific(String question, String match) {
		int index = match.indexOf(" ");
		if (index > 0 && question.equals(match.substring(index + 1).trim())) {
			return true;
		} else if (match.substring(index + 1).trim().startsWith(question)){
			return true;
		}
		
		return false;
	}
	
	@Override
	protected String trimPQFSpecific(String question, String match) {
		match = match.substring(match.indexOf(question) + question.length(), match.length());
		match = match.trim();
		return match;
	}

	
	@Override
	protected String processMatchPQFSpecific(String question, String match) {
		int index = match.indexOf(question);
		if (index > -1) {
			match = match.substring(index + question.length()).trim();
		}
		return match;
	}
	
	@Override
	protected String filterQuestionLine(String match) {
		match = super.filterQuestionLine(match);
		if (match != null && match.equals("No. Question. Response.")) return "";
		return match;
	}
	
	@Override
	protected boolean isNextQuestionPQFSpecific(List<AuditQuestion> questions, int curIndex, String match) {
		int spaceIndex = match.indexOf(" ");
		int colonIndex = match.indexOf(":");
		
		if (colonIndex > 0 && spaceIndex > colonIndex) {
			return true;
		}
		return false;
	}
}
