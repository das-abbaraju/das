<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="is_skill_of_training_type" value="operator_skill_create.type == 'Training' ? true : false" />
<s:set var="name_error_class" value="%{hasFieldError('operator_skill_create.name') ? 'error' : ''}" />
<s:set var="type_error_class" value="%{hasFieldError('operator_skill_create.type') ? 'error' : ''}" />

<%-- Url --%>
<s:url action="skill" var="operator_skill_list_url" />
<s:url action="skill" method="create" var="operator_skill_create_url" />

<tw:form formName="operator_skill_create" action="${operator_skill_create_url}" method="post" class="form-horizontal js-validation">
    <fieldset>
        <div class="control-group ${name_error_class}">
            <tw:label labelName="name"><strong>Name</strong></tw:label>
            <div class="controls">
                <tw:input autofocus="true" inputName="name" class="form-control" type="text" tabindex="1" value="${skillForm.name}"/>
                <tw:error errorName="name" />
            </div>
        </div>

        <div class="control-group">
            <tw:label labelName="description">Description</tw:label>
            <div class="controls">
                <tw:textarea textareaName="description" class="form-control" tabindex="2">${skillForm.description}</tw:textarea>
            </div>
        </div>

        <div class="control-group ${type_error_class}">
            <tw:label labelName="type"><strong>Type</strong></tw:label>
            <div class="controls">
                <tw:select selectName="skillType" class="form-control skillType" tabindex="3">
                    <tw:option value="Certification" selected="${skillForm.skillType == 'Certification'}">Certification</tw:option>
                    <tw:option value="Training" selected="${skillForm.skillType == 'Training'}">Training</tw:option>
                </tw:select>
                <tw:error errorName="skillType" />
            </div>
        </div>

        <s:if test="#is_skill_of_training_type">
            <s:include value="/struts/employee-guard/operator/skill/_training-form.jsp" />
        </s:if>

        <div class="control-group">
            <div class="controls">
                <tw:label labelName="company_required" class="checkbox">
                    <tw:input inputName="company_required" type="checkbox" value="true" /> Required for all employees
                </tw:label>
            </div>
        </div>

        <div class="control-group">
            <div class="controls">
                <tw:label labelName="add_another" class="checkbox">
                    <tw:input inputName="add_another" type="checkbox" value="true" /> Add Another
                </tw:label>

                <tw:button buttonName="save" type="submit" class="btn btn-success">Save</tw:button>
                <a href="${operator_skill_list_url}" class="btn btn-default">Cancel</a>
            </div>
        </div>
    </fieldset>
</tw:form>