<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="is_skill_of_training_type"
       value="skillForm.skillType != null && skillForm.skillType.training ? true : false"/>
<s:set var="name_error_class" value="%{hasFieldError('contractor_skill_edit.name') ? 'error' : ''}"/>
<s:set var="type_error_class" value="%{hasFieldError('contractor_skill_edit.type') ? 'error' : ''}"/>
<s:set var="required_for_all" value="%{skillForm.required ? 'checked' : ''}"/>
<s:set var="disable_groups" value="%{skillForm.required ? true : ''}"/>

<%-- Url --%>
<s:url action="skill" var="contractor_skill_show_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="skill" method="editSkillSection" var="contractor_skill_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="skill" method="update" var="contractor_skill_update_url">
    <s:param name="id">${id}</s:param>
</s:url>

<tw:form formName="contractor_skill_edit" action="${contractor_skill_update_url}" method="post" class="form-horizontal" role="form">
    <fieldset>
        <div class="form-group ${name_error_class}">
            <tw:label labelName="name" class="col-md-3 control-label"><strong>Name</strong></tw:label>
            <div class="col-md-4">
                <tw:input inputName="name" class="form-control" type="text" autofocus="true" tabindex="1" value="${skill.name}"/>
                <tw:error errorName="name"/>
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="description" class="col-md-3 control-label">Description</tw:label>
            <div class="col-md-4">
                <tw:textarea textareaName="description" class="form-control" tabindex="2" >${skill.description}</tw:textarea>
            </div>
        </div>

        <div class="form-group ${type_error_class}">
            <tw:label labelName="skillType" class="col-md-3 control-label"><strong>Type</strong></tw:label>
            <div class="col-md-4 col-xs-11">
                <tw:select selectName="skillType" value="${skill.skillType}" class="form-control skillType" tabindex="3">
                    <tw:option value="Certification" selected="${skill.skillType == 'Certification'}">Certification</tw:option>
                    <tw:option value="Training" selected="${skill.skillType == 'Training'}">Training</tw:option>
                </tw:select>
            </div>
            <div class="toolip-container col-md-1 col-xs-1">
               <i class="icon-info-sign icon-large" data-toggle="tooltip" data-placement="right" title="" data-original-title="Certificates require an uploaded file and expiration to be supplied. Training is honor-based." data-container="body"></i>
            </div>
        </div>

        <s:if test="#is_skill_of_training_type">
            <s:include value="/struts/employee-guard/contractor/skill/_training-form.jsp"/>
        </s:if>

        <s:set var="selected_groups" value="skillForm.groups"/>

        <div class="form-group">
            <tw:label labelName="groups" class="col-md-3 control-label">Employee Groups</tw:label>
            <div class="col-md-4">
                <tw:select selectName="groups" multiple="true" class="form-control contractor-skill-employee-groups" tabindex="7" disabled="${disable_groups}" >
                    <s:iterator value="skillGroups" var="contractor_group">
                        <s:set var="is_selected" value="false"/>
                        <s:iterator value="#selected_groups" var="selected_group">
                            <s:if test="#selected_group == #contractor_group.name">
                                <s:set var="is_selected" value="true"/>
                            </s:if>
                        </s:iterator>

                        <tw:option value="${contractor_group.name}"
                                   selected="${is_selected}">${contractor_group.name}</tw:option>
                    </s:iterator>
                </tw:select>
            </div>
            <div class="col-md-4 col-md-offset-3">
                <div class="checkbox">
                    <tw:label labelName="required" class="control-label required-for-all">
                        <s:if test="skillForm.required">
                            <tw:input inputName="required" class="required" type="checkbox" value="true" checked="checked" tabindex="8"/>
                        </s:if>
                        <s:else>
                            <tw:input inputName="required" class="required" type="checkbox" value="true" tabindex="8" />
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