package com.picsauditing.employeeguard.validators.document;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.employeeguard.validators.AbstractValidator;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.Strings;
import org.apache.commons.lang.ArrayUtils;

public class ProfileDocumentFormValidator extends AbstractValidator<DocumentForm> {

    public static final String PROFILE_DOCUMENT_FORM = "documentForm";

    public static final String FILE_CREATE_FILENAME_REQUEST_PARAM = "employee_file_create.validate_filename";
    public static final String FILE_EDIT_FILENAME_REQUEST_PARAM = "employee_file_edit.validate_filename";

    @Override
    protected DocumentForm getFormFromValueStack(ValueStack valueStack) {
        return (DocumentForm) valueStack.findValue(PROFILE_DOCUMENT_FORM, DocumentForm.class);
    }

    @Override
    protected void performValidation(DocumentForm documentForm) {
        if (!ProfileDocumentValidationUtil.valid(documentForm, ProfileDocumentValidationUtil.DocumentField.NAME)) {
            addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_DOCUMENT_FORM, "name"), "Name is missing");
        }

        if (!isFileValid(documentForm)) {
            addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_DOCUMENT_FORM, "file"), "Upload is missing");
        }

        if (!ProfileDocumentValidationUtil.valid(documentForm, ProfileDocumentValidationUtil.DocumentField.EXPIRATION_DATE)) {
            addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_DOCUMENT_FORM, "expireYear"), "Expiration date is invalid");
        }
    }

    private boolean isFileValid(DocumentForm documentForm) {
	    if (AjaxUtils.isAjax(request) && Strings.isEmpty(getFileNameFromRequest())) {
		    return false;
	    } else if (AjaxUtils.isAjax(request) && Strings.isNotEmpty(getFileNameFromRequest())) {
		    return true;
	    }

	    return ProfileDocumentValidationUtil.valid(documentForm, ProfileDocumentValidationUtil.DocumentField.FILE);
    }

    private String getFileNameFromRequest() {
        String[] values = getRequestValue();

        if (ArrayUtils.isEmpty(values)) {
            return null;
        }

        return values[0];
    }

    private String[] getRequestValue() {
        String[] values = request.getParameterValues(FILE_CREATE_FILENAME_REQUEST_PARAM);
        if (ArrayUtils.isEmpty(values)) { // fall-back to edit form
            values = request.getParameterValues(FILE_EDIT_FILENAME_REQUEST_PARAM);
        }

        return values;
    }
}
