package com.picsauditing.PICS.flags;

import java.util.Date;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.util.Strings;

/**
 * Made this class package private so that it is only used by other classes
 * within this package.
 */
final class FlagUtilities {

	/**
	 * Do not create new instances of this class
	 */
	private FlagUtilities() {
	}
	
	/**
	 * Returns a String value for the Hurdle for this criteria. Should only be used
	 * from within this package, which is why it has default package scope.
	 * 
	 * @param operatorCriteria
	 * @return
	 */
	static String getHurdle(FlagCriteriaOperator operatorCriteria) {
		FlagCriteria criteria = operatorCriteria.getCriteria();
		String hurdle = criteria.getDefaultValue();
		
		if (criteria.isAllowCustomValue() && !Strings.isEmpty(operatorCriteria.getHurdle())) {
			hurdle = operatorCriteria.getHurdle();
		}
		
		return hurdle;
	}
	
	/**
	 * @return true if something is BAD
	 */
	static boolean compare(String dataType, String comparison, String hurdle, String answer) {
		try {
			if (dataType.equals("boolean")) {
				return compareBooleans(dataType, comparison, hurdle, answer);
			}

			if (dataType.equals("number")) {
				return compareNumbers(dataType, comparison, hurdle, answer);
			}

			if (dataType.equals("string")) {
				return compareStrings(dataType, comparison, hurdle, answer);
			}

			if (dataType.equals("date")) {
				return compareDates(dataType, comparison, hurdle, answer);
			}

			return false;
		} catch (Exception e) {
			System.out.println("Datatype is " + dataType + " but values were not " 
					+ dataType + "s");
			return true;
		}
	}
	
	private static boolean compareBooleans(String dataType, String comparison, String hurdle, String answer) {
		return (Boolean.parseBoolean(answer) == Boolean.parseBoolean(hurdle));
	}
	
	private static boolean compareNumbers(String dataType, String comparison, String hurdle, String answer) {
		float answer2 = Float.parseFloat(answer.replace(",", ""));
		float hurdle2 = Float.parseFloat(hurdle.replace(",", ""));
		
		if (comparison.equals("="))
			return answer2 == hurdle2;
		
		if (comparison.equals(">"))
			return answer2 > hurdle2;
		
		if (comparison.equals("<"))
			return answer2 < hurdle2;
		
		if (comparison.equals(">="))
			return answer2 >= hurdle2;
		
		if (comparison.equals("<="))
			return answer2 <= hurdle2;
		
		if (comparison.equals("!="))
			return answer2 != hurdle2;
		
		return false;
	}
	
	private static boolean compareStrings(String dataType, String comparison, String hurdle, String answer) {
		if (comparison.equals("NOT EMPTY"))
			return Strings.isEmpty(answer);
		
		if (comparison.equalsIgnoreCase("contains"))
			return answer.contains(hurdle);
		
		if (comparison.equals("="))
			return hurdle.equals(answer);
		
		return false;
	}

	private static boolean compareDates(String dataType, String comparison, String hurdle, String answer) {
		Date conDate = DateBean.parseDate(answer);
		Date opDate;

		if (hurdle.equals("Today"))
			opDate = new Date();
		else
			opDate = DateBean.parseDate(hurdle);

		if (comparison.equals("<"))
			return conDate.before(opDate);

		if (comparison.equals(">"))
			return conDate.after(opDate);

		if (comparison.equals("="))
			return conDate.equals(opDate);
		
		return false;
	}

}
