<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="first_name_error_class" value="%{hasFieldError('employee.first_name') ? 'error' : ''}" />
<s:set var="last_name_error_class" value="%{hasFieldError('employee.last_name') ? 'error' : ''}" />
<s:set var="email_error_class" value="%{hasFieldError('employee.email') ? 'error' : ''}" />

<%-- Url --%>
<s:url action="employee" method="edit" var="contractor_employee_edit_url">
    <s:param name="id">${employee.id}</s:param>
</s:url>

<tw:form formName="contractor_employee_edit_personal" action="${contractor_employee_edit_url}" method="post" class="form-horizontal js-validation" autocomplete="off" role="form">
    <div class="form-group ${first_name_error_class}">
        <tw:label labelName="firstName" class="col-md-3 control-label"><strong>First
            Name</strong></tw:label>
        <div class="col-md-4">
            <tw:input inputName="firstName" class="form-control" type="text" autofocus="true" tabindex="1" value="${employee.firstName}" maxlength="100" />
            <tw:error errorName="firstName"/>
        </div>
    </div>

    <div class="form-group ${last_name_error_class}">
        <tw:label labelName="lastName" class="col-md-3 control-label"><strong>Last Name</strong></tw:label>
        <div class="col-md-4">
            <tw:input inputName="lastName" class="form-control" type="text" tabindex="2" value="${employee.lastName}" maxlength="100" />
            <tw:error errorName="lastName"/>
        </div>
    </div>

    <div class="form-group ${email_error_class}">
        <tw:label labelName="email" class="col-md-3 control-label"><strong>Email</strong></tw:label>
        <div class="col-md-4">
            <tw:input inputName="email" class="form-control" type="text" tabindex="3" value="${employee.email}" maxlength="70" />
            <tw:error errorName="email"/>
        </div>
    </div>

    <div class="form-group">
        <tw:label labelName="phoneNumber" class="col-md-3 control-label">Phone</tw:label>
        <div class="col-md-4">
            <tw:input inputName="phoneNumber" class="form-control" tabindex="4" type="text" value="${employee.phone}" maxlength="24" />
        </div>
    </div>

    <div class="form-group">
        <div class="col-md-9 col-md-offset-3 form-actions">
            <tw:button buttonName="save" type="submit" class="btn btn-success" tabindex="5">Save</tw:button>
            <tw:button buttonName="cancel" type="button" class="btn btn-default cancel" tabindex="6">Cancel</tw:button>
        </div>
    </div>
</tw:form>