<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="is_skill_of_training_type" value="operator_skill_create.type == 'Training' ? true : false" />
<s:set var="name_error_class" value="%{hasFieldError('operator_skill_create.name') ? 'error' : ''}" />
<s:set var="type_error_class" value="%{hasFieldError('operator_skill_create.type') ? 'error' : ''}" />

<%-- Url --%>
<s:url action="skill" var="operator_skill_list_url"/>
<s:url action="skill" method="create" var="operator_skill_create_url"/>

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

        <div class="form-group ${type_error_class}">
            <tw:label labelName="skillType" class="col-md-3 control-label"><strong>Skill Type</strong></tw:label>
            <div class="col-md-4 col-xs-11">
                <tw:select selectName="skillType" class="form-control skillType" tabindex="3">
                    <tw:option value="Certification"
                               selected="${skillForm.skillType == 'Certification'}">Certification</tw:option>
                    <tw:option value="Training" selected="${skillForm.skillType == 'Training'}">Training</tw:option>
                </tw:select>
                <tw:error errorName="skillType" />
            </div>
            <div class="toolip-container col-md-1 col-xs-1">
               <i class="icon-info-sign icon-large" data-toggle="tooltip" data-placement="right" title="" data-original-title="Certificates require an uploaded file and expiration to be supplied. Training is honor-based." data-container="body"></i>
            </div>
        </div>

        <s:if test="#is_skill_of_training_type">
            <s:include value="/struts/employee-guard/operator/skill/_training-form.jsp" />
        </s:if>

        <div class="form-group">
            <tw:label labelName="groups" class="col-md-3 control-label"><strong>Job Roles</strong></tw:label>
            <div class="col-md-4">
                <tw:select selectName="groups" multiple="true" class="form-control operator-skill-employee-groups"
                           tabindex="7">
                    <s:iterator value="roles" var="operator_role">
                        <s:set var="is_selected" value="false"/>
                        <s:iterator value="#selected_roles" var="selected_role">
                            <s:if test="#selected_role == #operator_role.name">
                                <s:set var="is_selected" value="true"/>
                            </s:if>
                        </s:iterator>

                        <tw:option value="${operator_role.name}"
                                   selected="${is_selected}">${operator_role.name}</tw:option>
                    </s:iterator>
                </tw:select>

                <div class="checkbox">
                    <tw:label labelName="required" class="control-label">
                        <tw:input inputName="required" class="required" type="checkbox" value="true"
                                  tabindex="8"/> Required for all employees
                    </tw:label>
                </div>
            </div>
        </div>

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
                    <tw:input inputName="add_another" type="checkbox" value="true"/> Add Another
                </tw:label>

            <div class="col-md-9 col-md-offset-3 form-actions">
                <tw:button buttonName="save" type="submit" class="btn btn-success" tabindex="10" >Add</tw:button>
                <a href="${operator_skill_list_url}" class="btn btn-default" tabindex="11" >Cancel</a>
            </div>
        </div>
    </fieldset>
</tw:form>