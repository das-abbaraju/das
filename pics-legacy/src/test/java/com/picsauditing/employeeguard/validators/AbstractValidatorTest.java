package com.picsauditing.employeeguard.validators;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.employeeguard.validators.factory.struts.ValidatorContextFactory;
import com.picsauditing.employeeguard.validators.factory.struts.ValueStackFactory;
import com.picsauditing.model.i18n.KeyValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AbstractValidatorTest {

    private AbstractValidator validator;

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        validator = new AbstractValidatorImpl();
    }

    @Test(expected = IllegalStateException.class)
    public void testNoContext() {
        validator.validate(ValueStackFactory.getValueStack(), null);
    }

    @Test
    public void testValidationNotApplicableForRequest() {
        when(request.getMethod()).thenReturn("GET");
        ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

        validator.validate(ValueStackFactory.getValueStack(request), validatorContext);

        assertFalse(validatorContext.hasErrors());
    }

    @Test
    public void testValidation() {
        when(request.getMethod()).thenReturn("POST");
        ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();
        Form form = new Form();
        form.setName("");

        validator.validate(ValueStackFactory.getValueStack(request, new KeyValue<String, Object>(AbstractValidatorImpl.FORM_NAME, form)), validatorContext);

        assertTrue(validatorContext.hasErrors());
        assertEquals("Name is missing", validatorContext.getFieldErrors().get(AbstractValidatorImpl.FORM_NAME + ".name").get(0));
    }

    private class AbstractValidatorImpl extends AbstractValidator {

        public static final String FORM_NAME = "FORM";

        @Override
        protected void performValidation(Object form) {
            addFieldErrorIfMessage(fieldKeyBuilder(FORM_NAME, "name"), "Name is missing");
        }

        @Override
        protected Object getFormFromValueStack(ValueStack valueStack) {
            return valueStack.findValue(FORM_NAME, Form.class);
        }
    }

    private class Form {

        private String name;

        private String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }
    }
}
