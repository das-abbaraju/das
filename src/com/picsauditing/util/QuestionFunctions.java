package com.picsauditing.util;

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
	public static String getTRIR(String inTotalRecordables, String inManHours) {
		int totalRecordables = Integer.parseInt(inTotalRecordables);
		int manHours = Integer.parseInt(inManHours);
		return Integer.toString(totalRecordables * 200000 / manHours);
	}

}
