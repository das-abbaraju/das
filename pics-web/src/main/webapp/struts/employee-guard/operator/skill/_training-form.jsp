<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="expire_error_class" value="%{hasFieldError('operator_skill_create.expire') || hasFieldError('operator_skill_edit.expire') ? 'error' : ''}" />

<div class="control-group ${expire_error_class}">
    <tw:label labelName="expire"><strong>Expires after&hellip;</strong></tw:label>
    <div class="controls">
        <tw:input inputName="expire" type="text" class="input-mini" placeholder="1" />
        
        <tw:select selectName="expire_interval">
            <tw:option value="">Month</tw:option>
            <tw:option value="">Quarter</tw:option>
            <tw:option value="">Semi-Annual</tw:option>
            <tw:option value="">Annual</tw:option>
        </tw:select>
        
        <tw:error errorName="expire" />
        
        <tw:label labelName="expire_not" class="checkbox">
            <tw:input inputName="expire_not" type="checkbox" value="true" /> Does not expire
        </tw:label>
    </div>
</div>