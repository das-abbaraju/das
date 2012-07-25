package com.picsauditing.PICS.flags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.FlagCriteria;

public class FlagAnswerParser {
	
	private final static Logger logger = LoggerFactory.getLogger(FlagAnswerParser.class);
	
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
		
		logger.warn("Failed to parse type " + cType + " " + qType
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
			logger.error("Failed to parse date [{}]", answer );
		}
		
		return "";
	}

	private static String parseForDate(FlagCriteria flagCriteria, String cType,
			String answer) {
		if (!"date".equals(cType))
			logger.warn("WARNING!! {} should be set to date but isn't", flagCriteria);
		
		try {
			DateBean.parseDate(answer);
			return answer;
		} catch (Exception doNothingRightHere) {
			logger.error("Failed to parse date [{}]", answer);
			return "";
		}
	}

	private static String parseForManual(FlagCriteria flagCriteria,
			String cType, String answer) {
		if (!"string".equals(cType))
			logger.warn("WARNING!! {} should be set to boolean but isn't", flagCriteria);
		return answer;
	}

	private static String parseForCheckBox(FlagCriteria flagCriteria,
			String cType, String answer) {
		if (!"boolean".equals(cType))
			logger.warn("WARNING!! {} should be set to boolean but isn't", flagCriteria);
		if ("X".equals(answer))
			return "true";
		else
			return "false";
	}

}
