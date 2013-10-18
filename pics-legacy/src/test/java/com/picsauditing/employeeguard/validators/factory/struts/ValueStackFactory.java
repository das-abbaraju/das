package com.picsauditing.employeeguard.validators.factory.struts;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.model.i18n.KeyValue;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.apache.struts2.StrutsStatics.HTTP_REQUEST;

public class ValueStackFactory {

    public static ValueStack getValueStack() {
        return new ValueStackForTest();
    }

    public static ValueStack getValueStack(HttpServletRequest request) {
        ValueStackForTest valueStackForTest = new ValueStackForTest();
        return addRequest(valueStackForTest, request);
    }

    private static ValueStackForTest addRequest(ValueStackForTest valueStackForTest, HttpServletRequest request) {
        valueStackForTest.getContext().put(HTTP_REQUEST, request);
        return valueStackForTest;
    }

    public static ValueStack getValueStack(HttpServletRequest request, KeyValue<String, Object>... valueStackData) {
        ValueStackForTest valueStackForTest = new ValueStackForTest();
        valueStackForTest = addRequest(valueStackForTest, request);
        addDataOnStack(valueStackForTest, Arrays.asList(valueStackData));
        return valueStackForTest;
    }

    private static ValueStackForTest addDataOnStack(ValueStackForTest valueStackForTest, List<KeyValue<String, Object>> data) {
        for (KeyValue<String, Object> value : data) {
            valueStackForTest.getFindValueContext().put(value.getKey(), value.getValue());
        }

        return valueStackForTest;
    }
}
