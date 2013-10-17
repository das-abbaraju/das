<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="name_error_class" value="%{hasFieldError('operator_role_create.name') ? 'error' : ''}"/>

<%-- Url --%>
<s:url action="role" var="operator_role_list_url" />
<s:url action="role" method="create" var="operator_role_create_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Add Role</s:param>
</s:include>
1
<tw:form formName="operator_role_create" action="${operator_role_create_url}" method="post" class="form-horizontal js-validation" role="form">
    <fieldset>
        <div class="form-group ${name_error_class}">
            <tw:label labelName="name" class="col-md-3 control-label"><strong>Name</strong></tw:label>
            <div class="col-md-4">
                <tw:input inputName="name" class="form-control" tabindex="1" type="text" autofocus="true" />
                <tw:error errorName="name"/>
            </div>
        </div>

        <div class="form-group ${name_error_class}">
            <tw:label labelName="description" class="col-md-3 control-label" tabindex="2"><strong>Description</strong></tw:label>
            <div class="col-md-4">
                <tw:textarea textareaName="description"></tw:textarea>
                <tw:error errorName="description"/>
            </div>
        </div>

        <s:set var="selected_skills" value="roleForm.skills"/>

        <div class="form-group">
            <tw:label labelName="skills" class="col-md-3 control-label">Required Skills</tw:label>
            <div class="col-md-4">
                <tw:select selectName="skills" multiple="true" class="form-control" tabindex="2">
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

        <div class="form-group">
            <tw:label labelName="employees" class="col-md-3 control-label">Employees</tw:label>
            <div class="col-md-4">
                <tw:select selectName="employees" multiple="true" class="form-control" tabindex="3">
                    <s:iterator value="roleEmployees" var="role_employee">
                        <s:set var="is_selected" value="false"/>
                        <s:iterator value="#selected_employees" var="selected_employee">
                            <s:if test="#selected_employee == #role_employee.id">
                                <s:set var="is_selected" value="true"/>
                            </s:if>
                        </s:iterator>

                        <tw:option value="${role_employee.id}" selected="${is_selected}">${role_employee.firstName} ${role_employee.lastName}</tw:option>
                    </s:iterator>
                </tw:select>
            </div>
        </div>

        <div class="form-group">
            <div class="col-md-4 col-md-offset-3">
                <div classs="checkbox">
                    <tw:label labelName="addAnother" class="control-label">
                        <tw:input inputName="addAnother" type="checkbox" value="true" tabindex="4"/> Add Another
                    </tw:label>
                </div>
            </div>
            <div class="col-md-9 col-md-offset-3 form-actions">
                <tw:button buttonName="save" type="submit" class="btn btn-success" tabindex="5">Add</tw:button>
                <a href="${operator_role_list_url}" class="btn btn-default" tabindex="6">Cancel</a>
            </div>
        </div>
    </fieldset>
</tw:form>





























<%--
    <fieldset>
        <div class="control-group">
            <tw:label labelName="name"><strong>Role</strong></tw:label>
            <div class="controls">
                <tw:input inputName="name" type="text" />
            </div>
        </div>

        <div class="control-group">
            <tw:label labelName="description">Description</tw:label>
            <div class="controls">
                <tw:textarea textareaName="description"></tw:textarea>
            </div>
        </div>

        <div class="control-group">
            <tw:label labelName="skills">Skills</tw:label>
            <div class="controls">
                <tw:select selectName="skills" multiple="true">
                    <option value="A">A</option>
                    <option value="B">B</option>
                    <option value="C">C</option>
                </tw:select>
            </div>
        </div>

        <div class="control-group">
            <div class="controls">
                <tw:label labelName="add_another" class="checkbox">
                    <tw:input inputName="add_another" type="checkbox" /> Add Another
                </tw:label>

                <tw:button buttonName="save" type="submit" class="btn btn-success">Save</tw:button>
                <a href="${operator_role_list_url}" class="btn btn-default">Cancel</a>
            </div>
        </div>
    </fieldset>
</tw:form>
--%>