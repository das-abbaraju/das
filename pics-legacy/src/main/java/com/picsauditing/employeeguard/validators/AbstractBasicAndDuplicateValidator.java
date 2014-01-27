package com.picsauditing.employeeguard.validators;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.employeeguard.daos.DuplicateEntityChecker;
import com.picsauditing.employeeguard.entities.duplicate.UniqueIndexable;
import com.picsauditing.employeeguard.validators.duplicate.DuplicateInfoProvider;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractBasicAndDuplicateValidator<T extends DuplicateInfoProvider> extends AbstractBasicValidator<T> {

    protected static final String DUPLICATE_ERROR_MSG_KEY = "PICS.DUPLICATE";

    @Autowired
    protected DuplicateEntityChecker duplicateEntityChecker;

    @Override
    public void validate(final ValueStack valueStack, final ValidatorContext validatorContext) {
        if (validatorContext == null) {
            throw new IllegalStateException("You must set the ValidatorContext to use this validator.");
        }

        request = getRequest(valueStack);
        if (validationNotApplicableToRequestMethod(request)) {
            return;
        }

        T form = getFormFromValueStack(valueStack);

        if (form == null) {
            return;
        }

        if (isDuplicate(form)) {
            addFieldErrorIfMessage(DUPLICATE_ERROR_MSG_KEY, getDuplicateErrorMessage());
            addErrorsToValidatorContext(validatorContext);
            return;
        }

        doFormValidation(form);
        addErrorsToValidatorContext(validatorContext);
    }

    protected boolean isDuplicate(final T form) {
        return duplicateEntityChecker.isDuplicate(form);
    }

    protected abstract String getDuplicateErrorMessage();
}
