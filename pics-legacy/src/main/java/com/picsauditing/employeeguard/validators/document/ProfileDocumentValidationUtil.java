package com.picsauditing.employeeguard.validators.document;

import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.util.Strings;

import java.util.Calendar;
import java.util.Date;

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

			case EXPIRATION_DATE:
				return validateExpirationDate(documentForm);

			default:
				throw new IllegalArgumentException("You have not setup validation for that field: " + field);
		}
	}

	private static boolean validateName(DocumentForm documentForm) {
		return Strings.isNotEmpty(documentForm.getName());
	}

	private static boolean validateFile(DocumentForm documentForm) {
		return documentForm.getFile() != null; // we could also validate file type
	}

	private static boolean validateExpirationDate(DocumentForm documentForm) {
		if (!documentForm.isNoExpiration()) {
			Calendar expiration = Calendar.getInstance();
			expiration.set(Calendar.YEAR, documentForm.getExpireYear());
			expiration.set(Calendar.MONTH, documentForm.getExpireMonth() - 1);
			expiration.set(Calendar.DAY_OF_MONTH, documentForm.getExpireDay());

            // TODO: Replace with a utility method that validates that the date is within a reasonable time-frame
            // from the current time.
			if (expiration.getTime().before(new Date())) {
				return false;
			}
		}

		return true;
	}
}
