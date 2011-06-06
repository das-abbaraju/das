package com.picsauditing.util;

import java.util.Map;

/**
 * Experimental Class
 * @author Trevor
 *
 */
public class QuestionFunctions {
	public static boolean isYes(String answer) {
		return answer.equals("Yes");
	}

	/**
	 * Bank Y/N
	 * Date of Bank
	 *   Visible/isValue
	 *   	answer = Bank
	 *   	value = "Y"
	 * @param answer
	 * @param value
	 * @return
	 */
	public static boolean isValue(String answer, String value) {
		return answer.equals(value);
	}

	/**
	 * TRIR
	 * 	 Catcher/getTRIR
	 * 		inTotalRecordables = q254
	 * 		inManHours = q23
	 * @param inTotalRecordables
	 * @param inManHours
	 * @return
	 */
	public static String getTRIR(Map<String, String> values) {
		int totalRecordables = Integer.parseInt(values.get("totalRecordables"));
		int manHours = Integer.parseInt(values.get("manHours"));
		return Integer.toString(totalRecordables * 200000 / manHours);
	}

}
