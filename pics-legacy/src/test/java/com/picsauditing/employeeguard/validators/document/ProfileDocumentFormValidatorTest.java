package com.picsauditing.employeeguard.validators.document;

import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.*;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.employeeguard.validators.factory.struts.ValidatorContextFactory;
import com.picsauditing.employeeguard.validators.factory.struts.ValueStackFactory;
import com.picsauditing.employeeguard.validators.skill.SkillFormValidator;
import com.picsauditing.model.i18n.KeyValue;
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
        ValueStack valueStack = ValueStackFactory.getValueStack(request, new KeyValue<String, Object>(SkillFormValidator.SKILL_FORM, buildDocumentFormNoValidationFailure()));
        ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

        profileDocumentFormValidator.validate(valueStack, validatorContext);

        assertFalse(validatorContext.hasErrors());
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
