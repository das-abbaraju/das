package com.picsauditing.employeeguard.validators.factory.struts;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class ValidatorContextForTest implements ValidatorContext {

    private Map<String, List<String>> fieldErrors = new HashMap<>();
    private Collection<String> actionErrors = new ArrayList<>();
    private Collection<String> acctionMessages = new ArrayList<>();

    @Override
    public String getFullFieldName(String fieldName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Locale getLocale() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasKey(String key) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getText(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getText(String key, String defaultValue) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getText(String key, String defaultValue, String obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getText(String key, List<?> args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getText(String key, String[] args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResourceBundle getTexts(String bundleName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResourceBundle getTexts() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setActionErrors(Collection<String> errorMessages) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<String> getActionErrors() {
        return actionErrors;
    }

    @Override
    public void setActionMessages(Collection<String> messages) {
        this.acctionMessages = messages;
    }

    @Override
    public Collection<String> getActionMessages() {
        return acctionMessages;
    }

    @Override
    public void setFieldErrors(Map<String, List<String>> errorMap) {
        this.fieldErrors = errorMap;
    }

    @Override
    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }

    @Override
    public void addActionError(String anErrorMessage) {
        actionErrors.add(anErrorMessage);
    }

    @Override
    public void addActionMessage(String aMessage) {
        acctionMessages.add(aMessage);
    }

    @Override
    public void addFieldError(String fieldName, String errorMessage) {
        List<String> errors = fieldErrors.get(fieldName);
        if (errors == null) {
            errors = new ArrayList<>();
            fieldErrors.put(fieldName, errors);
        }

        errors.add(errorMessage);
    }

    @Override
    public boolean hasActionErrors() {
        return CollectionUtils.isNotEmpty(actionErrors);
    }

    @Override
    public boolean hasActionMessages() {
        return CollectionUtils.isNotEmpty(acctionMessages);
    }

    @Override
    public boolean hasErrors() {
        return hasActionErrors() || hasFieldErrors();
    }

    @Override
    public boolean hasFieldErrors() {
        return MapUtils.isNotEmpty(fieldErrors);
    }
}
