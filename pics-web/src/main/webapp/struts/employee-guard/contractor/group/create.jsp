<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="employee-group" var="contractor_group_list_url"/>
<s:url action="employee-group" method="create" var="contractor_group_create_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Add Employee Group</s:param>
</s:include>

<tw:form formName="contractor_group_create" action="${contractor_group_create_url}" method="post"
         class="form-horizontal js-validation" role="form">
    <fieldset>
        <div class="form-group">
            <tw:label labelName="name" class="col-md-3 control-label"><strong>Name</strong></tw:label>
            <div class="col-md-4">
                <tw:input inputName="name" class="form-control" tabindex="1" type="text" autofocus="true" />
                <tw:error errorName="name"/>
            </div>
        </div>

        <s:set var="selected_skills" value="groupForm.skills"/>

        <div class="form-group">
            <tw:label labelName="skills" class="col-md-3 control-label">Required Skills</tw:label>
            <div class="col-md-4">
                <tw:select selectName="skills" multiple="true" class="form-control" tabindex="2">
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

        <div class="form-group">
            <tw:label labelName="employees" class="col-md-3 control-label">Employees</tw:label>
            <div class="col-md-4">
                <tw:select selectName="employees" multiple="true" class="form-control" tabindex="3">
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
                <a href="${contractor_group_list_url}" class="btn btn-default" tabindex="6">Cancel</a>
            </div>
        </div>
    </fieldset>
</tw:form>