<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="role" var="operator_role_show_url">
    <s:param name="id">1</s:param>
</s:url>
<s:url action="role" method="delete" var="operator_role_delete_url">
    <s:param name="id">1</s:param>
</s:url>
<s:url action="role" method="edit" var="operator_role_edit_url">
    <s:param name="id">1</s:param>
</s:url>
<s:url action="role" method="deleteConfirmation" var="operator_role_delete_confirmation_url">
    <s:param name="id">${id}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Edit Role</s:param>
    <s:param name="actions">
        <a href="${operator_role_delete_url}" class="btn btn-danger">Delete</a>
    </s:param>
</s:include>

<%-- Breadcrumb --%>
<s:include value="/struts/employee-guard/_breadcrumb.jsp"/>

<tw:form formName="operator_role_edit" action="${operator_role_edit_url}" method="post" class="form-horizontal">
    <fieldset>
        <div class="control-group ${name_error_class}">
            <tw:label labelName="name"><strong>Name</strong></tw:label>
            <div class="controls">
                <tw:input inputName="name" type="text" value="${role.name}"/>
                <tw:error errorName="name"/>
            </div>
        </div>

        <div class="control-group">
            <tw:label labelName="description">Description</tw:label>
            <div class="controls">
                <tw:textarea textareaName="description">${role.description}</tw:textarea>
            </div>
        </div>

        <s:set var="selected_skills" value="roleForm.skills"/>

        <div class="control-group">
            <tw:label labelName="skills">Skills</tw:label>
            <div class="controls">
                <tw:select selectName="skills" multiple="true">
                    <s:iterator value="roleSkills" var="company_skill">
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

        <s:set var="selected_employees" value="roleForm.employees"/>

        <div class="control-group">
            <tw:label labelName="employees">Employees</tw:label>
            <div class="controls">
                <tw:select selectName="employees" multiple="true">
                    <s:iterator value="roleEmployees" var="operator_employee">
                        <s:set var="is_selected" value="false"/>
                        <s:iterator value="#selected_employees" var="selected_employee">
                            <s:if test="#selected_employee == #operator_employee.id">
                                <s:set var="is_selected" value="true"/>
                            </s:if>
                        </s:iterator>

                        <tw:option value="${operator_employee.id}" selected="${is_selected}">${operator_employee.firstName} ${operator_employee.lastName}</tw:option>
                    </s:iterator>
                </tw:select>
            </div>
        </div>

        <div class="control-group">
            <div class="controls">
                <tw:button buttonName="save" type="submit" class="btn btn-success">Save</tw:button>
                <a href="${operator_role_show_url}" class="btn btn-default">Cancel</a>
            </div>
        </div>
    </fieldset>
</tw:form>