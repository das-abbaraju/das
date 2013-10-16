package com.picsauditing.employeeguard.validators.document;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.employeeguard.validators.AbstractValidator;

public class ProfileDocumentFormValidator extends AbstractValidator<DocumentForm> {
	public static final String PROFILE_DOCUMENT_FORM = "documentForm";

	@Override
	protected DocumentForm getFormFromValueStack(ValueStack valueStack) {
		return (DocumentForm) valueStack.findValue(PROFILE_DOCUMENT_FORM, DocumentForm.class);
	}

	@Override
	protected void performValidation(DocumentForm documentForm) {
		if (!ProfileDocumentValidationUtil.valid(documentForm, ProfileDocumentValidationUtil.DocumentField.NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_DOCUMENT_FORM, "name"), "Name is missing");
		}

		if (!ProfileDocumentValidationUtil.valid(documentForm, ProfileDocumentValidationUtil.DocumentField.FILE)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_DOCUMENT_FORM, "file"), "Upload is missing");
		}

		if (!ProfileDocumentValidationUtil.valid(documentForm, ProfileDocumentValidationUtil.DocumentField.EXPIRATION_DATE)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_DOCUMENT_FORM, "expireYear"), "Expiration date is invalid");
		}
	}

}
