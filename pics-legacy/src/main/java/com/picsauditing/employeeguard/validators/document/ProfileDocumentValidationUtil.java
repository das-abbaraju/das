package com.picsauditing.employeeguard.validators.document;

import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

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
