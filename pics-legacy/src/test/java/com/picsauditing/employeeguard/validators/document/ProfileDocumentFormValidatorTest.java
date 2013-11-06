package com.picsauditing.employeeguard.validators.document;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.employeeguard.validators.factory.struts.ValidatorContextFactory;
import com.picsauditing.employeeguard.validators.factory.struts.ValueStackFactory;
import com.picsauditing.employeeguard.validators.skill.SkillFormValidator;
import com.picsauditing.model.i18n.KeyValue;
import com.picsauditing.strutsutil.AjaxUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public class ProfileDocumentFormValidatorTest {

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

    @Test
    public void testFileValidationFailure_FileNameNotProvided() {
        setupRequest_FileNameNotProvidedInAjaxValidationRequest();
        ValueStack valueStack = ValueStackFactory.getValueStack(request,
                new KeyValue<String, Object>(ProfileDocumentFormValidator.PROFILE_DOCUMENT_FORM,
                        buildDocumentFormNoValidationFailure()));
        ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

        profileDocumentFormValidator.validate(valueStack, validatorContext);

        assertEquals("Upload is missing", validatorContext.getFieldErrors()
                .get(ProfileDocumentFormValidator.PROFILE_DOCUMENT_FORM + ".file").get(0));
    }

    private void setupRequest_FileNameNotProvidedInAjaxValidationRequest() {
        when(request.getHeader(AjaxUtils.HTTP_HEADER_X_REQUESTED_WITH)).thenReturn(AjaxUtils.AJAX_REQUEST_HEADER_VALUE);
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameterValues(ProfileDocumentFormValidator.FILE_CREATE_FILENAME_REQUEST_PARAM))
                .thenReturn(new String[]{""});
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
