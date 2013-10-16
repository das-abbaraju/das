<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="name_error_class" value="%{hasFieldError('contractor_group_edit.name') ? 'error' : ''}"/>

<%-- Url --%>
<s:url action="employee-group" var="contractor_group_show_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="employee-group" method="delete" var="contractor_group_delete_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="employee-group" method="edit" var="contractor_group_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="employee-group" method="deleteConfirmation" var="contractor_group_delete_confirmation_url">
    <s:param name="id">${id}</s:param>
</s:url>


<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Edit Employee Group</s:param>
    <s:param name="actions">
        <a href="${contractor_group_delete_url}" class="btn btn-danger delete"
           data-url="${contractor_group_delete_confirmation_url}">Delete</a>
    </s:param>
</s:include>

<%-- Breadcrumb --%>
<s:include value="/struts/employee-guard/_breadcrumb.jsp"/>

<tw:form formName="contractor_group_edit" action="${contractor_group_edit_url}" method="post" class="form-horizontal">
    <fieldset>
        <div class="control-group ${name_error_class}">
            <tw:label labelName="name"><strong>Name</strong></tw:label>
            <div class="controls">
                <tw:input inputName="name" type="text" value="${group.name}"/>
                <tw:error errorName="name"/>
            </div>
        </div>

        <div class="control-group">
            <tw:label labelName="description">Description</tw:label>
            <div class="controls">
                <tw:textarea textareaName="description">${group.description}</tw:textarea>
            </div>
        </div>

        <s:set var="selected_skills" value="groupForm.skills"/>

        <div class="control-group">
            <tw:label labelName="skills">Skills</tw:label>
            <div class="controls">
                <tw:select selectName="skills" multiple="true">
                    <s:iterator value="groupSkills" var="company_skill">
                        <s:set var="is_selected" value="false"/>
                        <s:iterator value="#selected_skills" var="selected_skill">
                            <s:if test="#selected_skill == #company_skill.id">
                                <s:set var="is_selected" value="true"/>
                            </s:if>
                        </s:iterator>

                        <tw:option value="#company_skill.id" selected="${is_selected}">${company_skill.name}</tw:option>
                    </s:iterator>
                </tw:select>
            </div>
        </div>

        <s:set var="selected_employees" value="groupForm.employees"/>

        <div class="control-group">
            <tw:label labelName="employees">Employees</tw:label>
            <div class="controls">
                <tw:select selectName="employees" multiple="true">
                    <s:iterator value="groupEmployees" var="contractor_employee">
                        <s:set var="is_selected" value="false"/>
                        <s:iterator value="#selected_employees" var="selected_employee">
                            <s:if test="#selected_employee == #contractor_employee.id">
                                <s:set var="is_selected" value="true"/>
                            </s:if>
                        </s:iterator>

                        <tw:option value="${contractor_employee.id}" selected="${is_selected}">${contractor_employee.firstName} ${contractor_employee.lastName}</tw:option>
                    </s:iterator>
                </tw:select>
            </div>
        </div>

        <div class="control-group">
            <div class="controls">
                <tw:button buttonName="save" type="submit" class="btn btn-success">Save</tw:button>
                <a href="${contractor_group_show_url}" class="btn btn-default">Cancel</a>
            </div>
        </div>
    </fieldset>
</tw:form>