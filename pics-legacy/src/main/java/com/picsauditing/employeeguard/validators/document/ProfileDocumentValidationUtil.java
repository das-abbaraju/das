package com.picsauditing.employeeguard.validators.document;

import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Years;

import java.io.File;

public class ProfileDocumentValidationUtil {

	public enum DocumentField {
		NAME, FILE, EXPIRATION_DATE
	}

	public static boolean valid(DocumentForm documentForm, DocumentField field) {
		switch (field) {
			case NAME:
				return validateName(documentForm);

			case FILE:
				return validateFile(documentForm);

			default:
				throw new IllegalArgumentException("You have not setup validation for that field: " + field);
		}
	}

	public static String validateExpirationDate(DocumentForm documentForm) {
		if (documentForm.isNoExpiration()) {
			return Strings.EMPTY_STRING;
		}

		int year = documentForm.getExpireYear();
		int month = documentForm.getExpireMonth();
		int day = documentForm.getExpireDay();

		if (year == 0 && month == 0 && day == 0 && !documentForm.isNoExpiration()) {
			return "Expiration date is required";
		}

		String error = Strings.EMPTY_STRING;

		DateTime expirationDateTime = null;
		DateTime currentDateTime = null;
		try {
			expirationDateTime = new DateTime(year, month, day, 0, 0);
			currentDateTime = new DateTime();
		} catch (IllegalFieldValueException e) {
			error = "Invalid Year-Month-Day";
			return error;
		}

		int yearsAheadBehind = Math.abs(Years.yearsBetween(expirationDateTime, currentDateTime).getYears());
		if (yearsAheadBehind > 99) {
			if (expirationDateTime.isBefore(currentDateTime)) {
				error = "Expiration Date is too far in the past";
			} else {
				error = "Expiration Date is too far in the future";
			}
		}

		return error;

	}


	private static boolean validateName(DocumentForm documentForm) {
		return Strings.isNotEmpty(documentForm.getName());
	}

	private static boolean validateFile(DocumentForm documentForm) {
		if (documentForm.getFile() != null) {
			return true;
		}

		return fileExists(documentForm);
	}

	private static boolean fileExists(DocumentForm documentForm) {
		try {
			File file = new File(FileUtils.getFtpDir() + "/files/" + FileUtils.thousandize(documentForm.getId()) + documentForm.getValidate_filename());
			return file.exists(); // we could also validate file type
		} catch (Exception exception) {
			// Ignore
		}

		return false;
	}
}
