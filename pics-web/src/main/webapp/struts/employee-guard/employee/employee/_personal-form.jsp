<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="first_name_error_class" value="%{hasFieldError('personalInfo.first_name') ? 'error' : ''}" />
<s:set var="last_name_error_class" value="%{hasFieldError('personalInfo.last_name') ? 'error' : ''}" />
<s:set var="email_error_class" value="%{hasFieldError('personalInfo.email') ? 'error' : ''}" />

<%-- Url --%>
<s:url action="profile" method="edit" var="employee_profile_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>

<tw:form formName="employee_profile_edit" action="${employee_profile_edit_url}"
         method="post" class="form-horizontal" autocomplete="off" role="form">
    <div class="form-group">
        <tw:label labelName="firstName" class="col-md-3 control-label"><strong>First Name</strong></tw:label>
        <div class="col-md-4">
            <tw:input inputName="firstName" class="form-control" type="text" value="${personalInfo.firstName}"/>
            <tw:error errorName="firstName" />
        </div>
    </div>

    <div class="form-group">
        <tw:label labelName="lastName" class="col-md-3 control-label"><strong>Last Name</strong></tw:label>
        <div class="col-md-4">
            <tw:input inputName="lastName" class="form-control" value="${personalInfo.lastName}" type="text" />
            <tw:error errorName="lastName" />
        </div>
    </div>

    <div class="form-group">
        <tw:label labelName="email" class="col-md-3 control-label"><strong>Email</strong></tw:label>
        <div class="col-md-4">
            <tw:input inputName="email" class="form-control" type="text" value="${personalInfo.email}"/>
            <tw:error errorName="email" />
        </div>
    </div>

    <div class="form-group">
        <tw:label labelName="phone" class="col-md-3 control-label">Phone</tw:label>
        <div class="col-md-4">
            <tw:input inputName="phone" class="form-control" type="text" value="${personalInfo.phone}"/>
        </div>
    </div>

    <div class="form-group">
        <div class="col-md-9 col-md-offset-3 form-actions">
            <tw:button buttonName="save" type="submit" class="btn btn-success">Save</tw:button>
            <tw:button buttonName="cancel" type="button" class="btn btn-default cancel">Cancel</tw:button>
        </div>
    </div>
</tw:form>