package com.picsauditing.employeeguard.validators.document;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.employeeguard.validators.AbstractBasicAndDuplicateValidator;
import com.picsauditing.employeeguard.validators.AbstractBasicValidator;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.Strings;

public class ProfileDocumentFormValidator extends AbstractBasicAndDuplicateValidator<DocumentForm> {

	public static final String PROFILE_DOCUMENT_FORM = "documentForm";

	@Override
	protected DocumentForm getFormFromValueStack(ValueStack valueStack) {
		return (DocumentForm) valueStack.findValue(PROFILE_DOCUMENT_FORM, DocumentForm.class);
	}

	@Override
	protected void doFormValidation(DocumentForm documentForm) {
		if (!ProfileDocumentValidationUtil.valid(documentForm, ProfileDocumentValidationUtil.DocumentField.NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_DOCUMENT_FORM, "name"), "Name is missing");
		}

		if (!isFileValid(documentForm)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_DOCUMENT_FORM, "file"), "Upload is missing");
		}

		String errorString=ProfileDocumentValidationUtil.validateExpirationDate(documentForm);
		if (Strings.isNotEmpty(errorString)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_DOCUMENT_FORM, "expireYear"), errorString);
		}
	}

	private boolean isFileValid(DocumentForm documentForm) {
		if(documentForm.getId()>0)
			return true;

		if (AjaxUtils.isAjax(request) && Strings.isEmpty(documentForm.getValidate_filename())) {
			return false;
		} else if (AjaxUtils.isAjax(request) && Strings.isNotEmpty(documentForm.getValidate_filename())) {
			return true;
		}

		return ProfileDocumentValidationUtil.valid(documentForm, ProfileDocumentValidationUtil.DocumentField.FILE);
	}

	@Override
	protected String getDuplicateErrorMessage() {
		return "Name";
	}
}
