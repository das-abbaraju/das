package com.picsauditing.flagcalculator.util;

import com.picsauditing.flagcalculator.entities.AuditData;
import com.picsauditing.flagcalculator.entities.FlagCriteria;
import com.picsauditing.flagcalculator.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlagAnswerParser {
	
	private final static Logger logger = LoggerFactory.getLogger(FlagAnswerParser.class);
	
	public static String parseAnswer(FlagCriteria flagCriteria, AuditData auditData) {
		String qType = auditData.getQuestion().getQuestionType();
		String cType = flagCriteria.getDataType();
		String answer = auditData.getAnswer();

        try {
            if ("Check Box".equals(qType)) {
                return parseForCheckBox(flagCriteria, cType, answer);
            }

            if ("Manual".equals(qType)) {
                return parseForManual(flagCriteria, cType, answer);
            }

            if (AuditService.isMultipleChoice(auditData)
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
        } catch (Exception e) {
            logParsingProblem(cType, flagCriteria.getId(), auditData.getId());
        }
        return "";
	}
	
	private static String parseForNumber(String answer) throws Exception {
		answer = answer.replace(",", "");
		Float parsedAnswer = Float.parseFloat(answer);
        return parsedAnswer.toString();
	}

	private static String parseForDate(FlagCriteria flagCriteria, String cType,
			String answer) {
		if (!"date".equals(cType))
			logger.warn("WARNING!! {} should be set to date but isn't", flagCriteria);
		DateBean.parseDate(answer);
		return answer;
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

    private static void logParsingProblem(String dataType, int criteriaID, int auditDataID) {
        logger.warn("Unable to parse " + dataType + " for flag criteria id " + criteriaID + " and audit data id " + auditDataID);
    }

}
