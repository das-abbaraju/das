<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="is_skill_of_training_type"
       value="skillForm.skillType != null && skillForm.skillType.training ? true : false"/>
<s:set var="required_for_all" value="%{skillForm.required ? 'checked' : ''}"/>


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

<tw:form formName="contractor_skill_edit" action="${contractor_skill_update_url}" method="post" class="form-horizontal js-validation" role="form">
    <fieldset>
        <div class="form-group">
            <tw:label labelName="name" class="col-md-3 control-label"><strong><s:text name="CONTRACTOR.SKILL.EDIT_FORM.NAME" /></strong></tw:label>
            <div class="col-md-4">
                <tw:input inputName="name" class="form-control" type="text" autofocus="true" tabindex="1" value="${skill.name}" maxlength="70" />
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="description" class="col-md-3 control-label"><s:text name="CONTRACTOR.SKILL.EDIT_FORM.DESCRIPTION" /></tw:label>
            <div class="col-md-4">
                <tw:textarea textareaName="description" class="form-control" tabindex="2" maxlength="1470" >${skill.description}</tw:textarea>
            </div>
        </div>

        <dl class="employee-guard-information non-editable-form-field">
            <dt class="col-md-3"><s:text name="CONTRACTOR.SKILL.EDIT_FORM.SKILL_TYPE" /></dt>
            <dd class="col-md-4">
                <%-- Skill Type --%>
                <s:include value="/struts/employee-guard/_skilltype.jsp">
                    <s:param name="skillType">${skill.skillType}</s:param>
                </s:include>
            </dd>
        </dl>
        <input type="hidden" name="contractor_skill_edit.skillType" value="${skill.skillType}" />

        <s:if test="#is_skill_of_training_type">
            <s:include value="/struts/employee-guard/contractor/skill/_training-form.jsp"/>
        </s:if>

        <s:set var="selected_groups" value="skillForm.groups"/>

        <div class="form-group">
            <tw:label labelName="groups" class="col-md-3 control-label"><s:text name="CONTRACTOR.SKILL.EDIT_FORM.EMPLOYEE_GROUPS" /></tw:label>
            <div class="col-md-4">
                <tw:select selectName="groups" multiple="true" class="form-control contractor-skill-employee-groups select2" tabindex="7">
                    <s:iterator value="skillGroups" var="contractor_group">
                        <s:set var="is_selected" value="false"/>
                        <s:iterator value="#selected_groups" var="selected_group">
                            <s:if test="#selected_group == #contractor_group.id">
                                <s:set var="is_selected" value="true"/>
                            </s:if>
                        </s:iterator>

                        <tw:option value="${contractor_group.id}" selected="${is_selected}">${contractor_group.name}</tw:option>
                    </s:iterator>
                </tw:select>
            </div>
            <div class="col-md-4 col-md-offset-3">
                <div class="checkbox">
                    <tw:label labelName="required" class="control-label required-for-all">
                        <s:if test="skillForm.required">
                            <tw:input inputName="required" class="required" data-toggle="form-input" data-target=".contractor-skill-employee-groups" type="checkbox" value="true" checked="checked" tabindex="8"/>
                        </s:if>
                        <s:else>
                            <tw:input inputName="required" class="required" data-toggle="form-input" data-target=".contractor-skill-employee-groups" type="checkbox" value="true" tabindex="8" />
                        </s:else>
                      <s:text name="CONTRACTOR.SKILL.EDIT_FORM.REQUIRED_FOR_ALL_EMPLOYEES" />
                    </tw:label>
                </div>
            </div>
        </div>

        <div class="form-group">
            <div class="col-md-9 col-md-offset-3 form-actions">
                <tw:button buttonName="update" type="submit" class="btn btn-success" tabindex="9"><s:text name="CONTRACTOR.SKILL.EDIT_FORM.SAVE" /></tw:button>
                <tw:button buttonName="cancel" type="button" class="btn btn-default cancel" tabindex="10"><s:text name="CONTRACTOR.SKILL.EDIT_FORM.CANCEL" /></tw:button>
            </div>
        </div>
    </fieldset>
</tw:form>