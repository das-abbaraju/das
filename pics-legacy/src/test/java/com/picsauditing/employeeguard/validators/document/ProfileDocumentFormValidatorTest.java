package com.picsauditing.employeeguard.validators.document;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.employeeguard.validators.factory.struts.ValidatorContextFactory;
import com.picsauditing.employeeguard.validators.factory.struts.ValueStackFactory;
import com.picsauditing.model.i18n.KeyValue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class ProfileDocumentFormValidatorTest {
	public static final String FILE_CREATE_FILENAME_REQUEST_PARAM = "employee_file_create.validate_filename";
	public static final String FILE_EDIT_FILENAME_REQUEST_PARAM = "employee_file_edit.validate_filename";

	private ProfileDocumentFormValidator profileDocumentFormValidator;

	@Mock
	private HttpServletRequest request;
	@Mock
	private File file;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		profileDocumentFormValidator = new ProfileDocumentFormValidator();
	}

	@Test
	public void testNoValidationFailure() {
		when(request.getMethod()).thenReturn("POST");
		ValueStack valueStack = ValueStackFactory.getValueStack(request,
				new KeyValue<String, Object>(ProfileDocumentFormValidator.PROFILE_DOCUMENT_FORM,
						buildDocumentFormNoValidationFailure()));
		ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

		profileDocumentFormValidator.validate(valueStack, validatorContext);

		assertFalse(validatorContext.hasErrors());
	}

	@Ignore("Need to rewrite") // FIXME
	@Test
	public void testFileValidationFailure_FileNameNotProvided() {
//        setupRequest_FileNameNotProvidedInAjaxValidationRequest();
//        ValueStack valueStack = ValueStackFactory.getValueStack(request,
//                new KeyValue<String, Object>(ProfileDocumentFormValidator.PROFILE_DOCUMENT_FORM,
//                        buildDocumentFormNoValidationFailure()));
//        ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();
//
//        profileDocumentFormValidator.validate(valueStack, validatorContext);
//
//        assertEquals("Upload is missing", validatorContext.getFieldErrors()
//                .get(ProfileDocumentFormValidator.PROFILE_DOCUMENT_FORM + ".file").get(0));
	}

	private DocumentForm buildDocumentFormNoValidationFailure() {
		DocumentForm documentForm = new DocumentForm();
		documentForm.setName("Document Name");
		documentForm.setExpireDay(1);
		documentForm.setExpireMonth(1);
		documentForm.setExpireYear(2014);
		documentForm.setFile(file);
		return documentForm;
	}
}
