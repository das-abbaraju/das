package com.picsauditing.employeeguard.validators.factory.struts;

import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.HashMap;
import java.util.Map;

public class ValueStackForTest implements ValueStack {

    private Map<String, Object> context = new HashMap<>();

    private Map<String, Object> findValueContext = new HashMap<>();

    @Override
    public Map<String, Object> getContext() {
        return context;
    }

    @Override
    public void setDefaultType(Class defaultType) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setExprOverrides(Map<Object, Object> overrides) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<Object, Object> getExprOverrides() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CompoundRoot getRoot() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setValue(String expr, Object value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setParameter(String expr, Object value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String findString(String expr) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String findString(String expr, boolean throwExceptionOnFailure) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object findValue(String expr) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object findValue(String expr, boolean throwExceptionOnFailure) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object findValue(String expr, Class asType) {
        return findValueContext.get(expr);
    }

    @Override
    public Object findValue(String expr, Class asType, boolean throwExceptionOnFailure) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object peek() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object pop() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void push(Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void set(String key, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int size() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* Other methods added to class not part of interface */

    public Map<String, Object> getFindValueContext() {
        return findValueContext;
    }
}
