package com.picsauditing.interceptors;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.picsauditing.forms.binding.ActionFormBindingAnnotationCache;
import com.picsauditing.util.Strings;

public class PicsFormBindingInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 9148209636221548902L;

    public static final String ERROR_PRE_RESULT_LISTENER = "pics.error.preresult.listener";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        Map<String, String> formNameMapping = ActionFormBindingAnnotationCache.getFormMappingForAction(action);
        String formName = getFormName(formNameMapping);
        String propertyName = getPropertyName(formNameMapping, formName);
        Map<String, Object> params = ActionContext.getContext().getParameters();

        replaceFormName(params, formName, propertyName);

        ErrorPreResultListener listener = new ErrorPreResultListener(propertyName, formName, params);
        invocation.addPreResultListener(listener);
        ActionContext.getContext().getValueStack().getContext().put(ERROR_PRE_RESULT_LISTENER, listener);

        String actionResult = invocation.invoke();

        return actionResult;
    }

    private String getFormName(Map<String, String> formNameMapping) {
        if (MapUtils.isEmpty(formNameMapping)) {
            return null;
        }

        return findFormInRequest(ServletActionContext.getRequest(), formNameMapping);
    }

    private String getPropertyName(Map<String, String> formNameMapping, String formName) {
        if (MapUtils.isEmpty(formNameMapping) || Strings.isEmpty(formName)) {
            return null;
        }

        return formNameMapping.get(formName);
    }

    private void replaceFormName(Map params, String formName, String replacement) {
        if (MapUtils.isEmpty(params) || Strings.isEmpty(formName) || Strings.isEmpty(replacement)) {
            return;
        }

        Map replacementParameters = new HashMap();

        Iterator<String> parameterKeyIterator = params.keySet().iterator();
        while (parameterKeyIterator.hasNext()) {
            String parameterKey = parameterKeyIterator.next();
            if (parameterKey.startsWith(formName)) {
                String newKey = replaceName(parameterKey, formName, replacement);
                replacementParameters.put(newKey, params.get(parameterKey));
                parameterKeyIterator.remove();
            }
        }

        params.putAll(replacementParameters);
    }

    private String replaceName(String parameterKey, String name, String replacement) {
        return parameterKey.replace(name, replacement);
    }

    private String parseFormName(String paramName) {
        if (Strings.isEmpty(paramName)) {
            return paramName;
        }

        int indexOfFirstDot = paramName.indexOf('.');
        if (indexOfFirstDot < 0) {
            return paramName;
        }

        return paramName.substring(0, indexOfFirstDot);
    }

    @SuppressWarnings("unchecked")
    private String findFormInRequest(HttpServletRequest request, Map<String, String> formNameMapping) {
        List<String> paramList = new ArrayList<>();

        Enumeration<String> params = request.getParameterNames();
        while (params != null && params.hasMoreElements()) {
            paramList.add(params.nextElement());
        }

        if (request instanceof MultiPartRequestWrapper) {
            MultiPartRequestWrapper multiPartRequestWrapper = (MultiPartRequestWrapper) request;
            params = multiPartRequestWrapper.getFileParameterNames();

            while (params != null && params.hasMoreElements()) {
                paramList.add(params.nextElement());
            }
        }

        for (String parameterName : paramList) {
            String formName = parseFormName(parameterName);
            if (formNameMapping.containsKey(formName)) {
                return formName;
            }
        }

        return null;
    }

    public static class ErrorPreResultListener implements PreResultListener {

        private final String propertyName;
        private final String formName;
        private final Map<String, Object> params;

        public ErrorPreResultListener(final String propertyName, final String formName, final Map<String, Object> params) {
            this.propertyName = propertyName;
            this.formName = formName;
            this.params = params;
        }

        @Override
        public void beforeResult(ActionInvocation invocation, String resultCode) {
            Object action = invocation.getAction();
            if (action instanceof ValidationAware) {
                ValidationAware validationAware = (ValidationAware) action;

                if (validationAware.hasFieldErrors()) {
                    Map<String, List<String>> fieldErrors = validationAware.getFieldErrors();
                    replaceFormName(fieldErrors, propertyName, formName);
                    validationAware.setFieldErrors(fieldErrors);

                    invocation.getInvocationContext().getValueStack().getContext().put("fieldErrors", fieldErrors);
                }
            }

            replaceFormName(params, propertyName, formName);
        }

        private void replaceFormName(Map params, String formName, String replacement) {
            if (MapUtils.isEmpty(params) || Strings.isEmpty(formName) || Strings.isEmpty(replacement)) {
                return;
            }

            Map replacementParameters = new HashMap();

            Iterator<String> parameterKeyIterator = params.keySet().iterator();
            while (parameterKeyIterator.hasNext()) {
                String parameterKey = parameterKeyIterator.next();
                if (parameterKey.startsWith(formName)) {
                    String newKey = replaceName(parameterKey, formName, replacement);
                    replacementParameters.put(newKey, params.get(parameterKey));
                    parameterKeyIterator.remove();
                }
            }

            params.putAll(replacementParameters);
        }

        private String replaceName(String parameterKey, String name, String replacement) {
            return parameterKey.replace(name, replacement);
        }
    }
}
