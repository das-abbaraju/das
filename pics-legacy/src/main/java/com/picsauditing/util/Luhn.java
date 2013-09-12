package com.picsauditing.util;

/**
 * Utility methods for the Luhn algorithm
 *
 * 1. Starting with the second to last digit and moving left, double the
 *    value of all the alternating digits. For any digits that thus become
 *    10 or more, add their digits together. For example, 1111 becomes 2121,
 *    while 8763 becomes 7733 (from (1+6)7(1+2)3).
 *
 * 2. Add all these digits together. For example, 1111 becomes 2121, then
 *    2+1+2+1 is 6; while 8763 becomes 7733, then 7+7+3+3 is 20.
 *
 * 3. If the total ends in 0 (put another way, if the total modulus 10 is
 *    0), then the number is valid according to the Luhn formula, else it is
 *    not valid. So, 1111 is not valid (as shown above, it comes out to 6),
 *    while 8763 is valid (as shown above, it comes out to 20).
 */
public class Luhn {
	/**
	 * Checks whether a string of digits is a valid credit card number according
	 * to the Luhn algorithm.
	 * @param number to validate.
	 * @return true if the number is valid, false otherwise.
	 */
	public static boolean isValidNumber(String number) {
		int length = number.length()-1;
		Character checkDigit = getCheckDigit(number.substring(0, length));
		return checkDigit.equals(number.charAt(length));
	}

	/**
	 * Calculates the appropriate check digit according to the Luhn algorithm.
	 *
	 * @param number you're trying to calculate for
	 * @return the check digit (0-9) for the given number.
	 */
	public static char getCheckDigit(String number) {
		int sum = 0;

		boolean alternate = true;
		for (int i = number.length() - 1; i >= 0; i--) {
			int n = Integer.parseInt(number.substring(i, i + 1));
			if (alternate) {
				n *= 2;
				if (n > 9) {
					n = (n % 10) + 1;
				}
			}
			sum += n;
			alternate = !alternate;
		}

		Integer remainder = 10 - (sum % 10);
		if (remainder==10) return "0".charAt(0);
		return remainder.toString().charAt(0);
	}
	public static String addCheckDigit(String number) {
		Character checkDigit = Luhn.getCheckDigit(number);
		return number+checkDigit.toString();
	}
}
