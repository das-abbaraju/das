package com.picsauditing.PICS.flags;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.FlagCriteria;

public class FlagAnswerParser {
	
	public static String parseAnswer(FlagCriteria flagCriteria, AuditData auditData) {
		String qType = auditData.getQuestion().getQuestionType();
		String cType = flagCriteria.getDataType();
		String answer = auditData.getAnswer();

		if ("Check Box".equals(qType)) {
			return parseForCheckBox(flagCriteria, cType, answer);
		}
		
		if ("Manual".equals(qType)) {
			return parseForManual(flagCriteria, cType, answer);
		}
		
		if (auditData.isMultipleChoice()
				&& ("YesNoNA".equals(auditData.getQuestion().getOption()
						.getUniqueCode()) || "YesNo".equals(auditData
						.getQuestion().getOption().getUniqueCode()))) {
			return parseForManual(flagCriteria, cType, answer);
		}
		
		if ("Date".equals(qType)) {
			return parseForDate(flagCriteria, cType, answer);
		}
		
		if ("string".equals(cType)) {
			return answer;
		}
		
		if ("number".equals(cType)) {
			return parseForNumber(answer);
		}
		
		System.out.println("Failed to parse type " + cType + " " + qType
				+ " for flagCriteria #" + flagCriteria.getId()
				+ ", and question#" + auditData.getQuestion().getId());
		
		return "";
	}
	
	private static String parseForNumber(String answer) {
		answer = answer.replace(",", "");
		try {
			Float parsedAnswer = Float.parseFloat(answer);
			return parsedAnswer.toString();
		} catch (Exception doNothingRightHere) {
			System.out.println("Failed to parse date [" + answer + "]");
		}
		
		return "";
	}

	private static String parseForDate(FlagCriteria flagCriteria, String cType,
			String answer) {
		if (!"date".equals(cType))
			System.out.println("WARNING!! " + flagCriteria + " should be set to date but isn't");
		try {
			DateBean.parseDate(answer);
			return answer;
		} catch (Exception doNothingRightHere) {
			System.out.println("Failed to parse date [" + answer + "]");
			return "";
		}
	}

	private static String parseForManual(FlagCriteria flagCriteria,
			String cType, String answer) {
		if (!"string".equals(cType))
			System.out.println("WARNING!! " + flagCriteria + " should be set to boolean but isn't");
		return answer;
	}

	private static String parseForCheckBox(FlagCriteria flagCriteria,
			String cType, String answer) {
		if (!"boolean".equals(cType))
			System.out.println("WARNING!! " + flagCriteria + " should be set to boolean but isn't");
		if ("X".equals(answer))
			return "true";
		else
			return "false";
	}

}
