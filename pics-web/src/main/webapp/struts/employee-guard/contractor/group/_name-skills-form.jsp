<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="name_error_class" value="%{hasFieldError('contractor_group_edit.name') ? 'error' : ''}"/>

<%-- Url --%>
<s:url action="employee-group" method="edit" var="contractor_group_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>

<tw:form formName="contractor_group_edit_name_skills" action="${contractor_group_edit_url}" method="post" class="form-horizontal js-validation"
         role="form">
    <fieldset>
        <div class="form-group ${name_error_class}">
            <tw:label labelName="name" class="col-md-3 control-label"><strong>Name</strong></tw:label>
            <div class="col-md-4">
                <tw:input inputName="name" class="form-control" type="text" value="${group.name}" autofocus="true" tabindex="1" maxlength="70" />
                <tw:error errorName="name"/>
            </div>
        </div>

        <s:set var="selected_skills" value="group.skills"/>

        <div class="form-group">
            <tw:label labelName="skills" class="col-md-3 control-label">Required Skills</tw:label>
            <div class="col-md-4">
                <tw:select selectName="skills" multiple="true" class="form-control select2" tabindex="2">
                    <s:iterator value="groupSkills" var="company_skill">
                        <s:set var="is_selected" value="false"/>
                        <s:iterator value="#selected_skills" var="selected_skill">
                            <s:if test="#selected_skill.skill.id == #company_skill.id">
                                <s:set var="is_selected" value="true"/>
                            </s:if>
                        </s:iterator>

                        <tw:option value="#company_skill.id" selected="${is_selected}">${company_skill.name}</tw:option>
                    </s:iterator>
                </tw:select>
            </div>
        </div>

        <div class="form-group">
            <div class="col-md-9 col-md-offset-3 form-actions">
                <tw:button buttonName="save" type="submit" class="btn btn-success" tabindex="3">Save</tw:button>
                <tw:button buttonName="cancel" type="button" class="btn btn-default cancel" tabindex="4">Cancel</tw:button>
            </div>
        </div>
    </fieldset>
</tw:form>