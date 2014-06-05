package com.picsauditing.employeeguard.validators.employee;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.EmployeeEmploymentForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeePhotoForm;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.validators.AbstractBasicAndDuplicateValidator;
import com.picsauditing.employeeguard.validators.AbstractBasicValidator;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class EmployeePhotoFormValidator extends AbstractBasicValidator<EmployeePhotoForm> {

	public static final String EMPLOYEE_PHOTO_FORM = "employeePhotoForm";

	@Override
	protected void doFormValidation(EmployeePhotoForm form) {
		if (!fileExtensionValid(form)) {
			addFieldErrorIfMessage(fieldKeyBuilder(EMPLOYEE_PHOTO_FORM, "photo"), "Invalid filetype. Please upload either a .jpg or .png file.");
		}
	}

	@Override
	protected EmployeePhotoForm getFormFromValueStack(ValueStack valueStack) {
		return (EmployeePhotoForm) valueStack.findValue(EMPLOYEE_PHOTO_FORM, EmployeePhotoForm.class);
	}

	private boolean fileExtensionValid(EmployeePhotoForm photoForm){
		boolean status=false;

        String fileName = Strings.isEmpty(photoForm.getPhotoFileName()) ? photoForm.getValidate_filename() : photoForm.getPhotoFileName();

		String extension = FileUtils.getExtension(fileName).toLowerCase();
		PhotoUtil photoUtil = SpringUtils.getBean("PhotoUtil");
		if (photoUtil.isValidExtension(extension)) {
			status=true;
		}

		return status;
	}

}