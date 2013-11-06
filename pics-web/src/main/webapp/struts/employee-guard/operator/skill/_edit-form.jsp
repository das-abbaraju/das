<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="is_skill_of_training_type"
       value="operatorSkillForm.skillType != null && operatorSkillForm.skillType.training ? true : false"/>
<s:set var="required_for_all" value="%{operatorSkillForm.required ? 'checked' : ''}"/>

<%-- Url --%>
<s:url action="skill" var="operator_skill_show_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="skill" method="editSkillSection" var="operator_skill_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="skill" method="edit" var="operator_skill_update_url">
    <s:param name="id">${id}</s:param>
</s:url>

<tw:form formName="operator_skill_edit" action="${operator_skill_update_url}" method="post" class="form-horizontal js-validation" role="form">
    <fieldset>
        <div class="form-group">
            <tw:label labelName="name" class="col-md-3 control-label"><strong>Name</strong></tw:label>
            <div class="col-md-4">
                <tw:input inputName="name" class="form-control" type="text" autofocus="true" tabindex="1" value="${skill.name}"/>
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="description" class="col-md-3 control-label">Description</tw:label>
            <div class="col-md-4">
                <tw:textarea textareaName="description" class="form-control" tabindex="2">${skill.description}</tw:textarea>
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="skillType" class="col-md-3 control-label"><strong>Skill Type</strong></tw:label>
            <div class="col-md-4 col-xs-11">
                <tw:select selectName="skillType" value="${skill.skillType}" class="form-control select2Min skillType" tabindex="3">
                    <tw:option value="Certification" selected="${skill.skillType == 'Certification'}">Certification</tw:option>
                    <tw:option value="Training" selected="${skill.skillType == 'Training'}">Training</tw:option>
                </tw:select>
            </div>
            <div class="toolip-container col-md-1 col-xs-1">
                <i class="icon-info-sign icon-large" data-toggle="tooltip" data-placement="right" title=""
                   data-original-title="Certificates require an uploaded file and expiration to be supplied. Training is honor-based."
                   data-container="body"></i>
            </div>
        </div>

        <s:if test="#is_skill_of_training_type">
            <s:include value="/struts/employee-guard/operator/skill/_training-form.jsp"/>
        </s:if>

        <s:set var="selected_roles" value="operatorSkillForm.groups"/>

        <div class="form-group">
            <tw:label labelName="groups" class="col-md-3 control-label">Job Roles</tw:label>
            <div class="col-md-4">
                <tw:select selectName="groups" multiple="true" class="form-control select2 operator-skill-employee-groups" tabindex="7">
                    <s:iterator value="roles" var="operator_role">
                        <s:set var="is_selected" value="false"/>
                        <s:iterator value="#selected_roles" var="selected_role">
                            <s:if test="#selected_role == #operator_role.name">
                                <s:set var="is_selected" value="true"/>
                            </s:if>
                        </s:iterator>

                        <tw:option value="${operator_role.name}" selected="${is_selected}">${operator_role.name}</tw:option>
                    </s:iterator>
                </tw:select>
            </div>
            <div class="col-md-4 col-md-offset-3">
                <div class="checkbox">
                    <tw:label labelName="required" class="control-label required-for-all">
                        <s:if test="operatorSkillForm.required">
                            <tw:input inputName="required" class="required" data-toggle="form-input" data-target=".operator-skill-employee-groups" type="checkbox" value="true" checked="checked" tabindex="8"/>
                        </s:if>
                        <s:else>
                            <tw:input inputName="required" class="required" data-toggle="form-input" data-target=".operator-skill-employee-groups" type="checkbox" value="true" tabindex="8"/>
                        </s:else>
                        Required for all employees
                    </tw:label>
                </div>
            </div>
        </div>

        <div class="form-group">
            <div class="col-md-9 col-md-offset-3 form-actions">
                <tw:button buttonName="update" type="submit" class="btn btn-success" tabindex="9">Save</tw:button>
                <tw:button buttonName="cancel" type="button" class="btn btn-default cancel" tabindex="10">Cancel</tw:button>
            </div>
        </div>
    </fieldset>
</tw:form>