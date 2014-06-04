<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="employee" method="edit" var="contractor_employee_edit_url">
    <s:param name="id">${employee.id}</s:param>
</s:url>

<tw:form formName="contractor_employee_edit_employment" action="${contractor_employee_edit_url}" method="post" class="form-horizontal js-validation" autocomplete="off" role="form">
    <div class="form-group">
        <tw:label labelName="employeeId" class="col-md-3 control-label">Employee ID</tw:label>
        <div class="col-md-4">
            <tw:input inputName="employeeId" class="form-control" type="text" autofocus="true" tabindex="1" value="${employee.slug}" maxlength="70" />
        </div>
    </div>

    <div class="form-group">
        <tw:label labelName="title" class="col-md-3 control-label">Title</tw:label>
        <div class="col-md-4">
            <tw:input inputName="title" class="form-control" type="text" tabindex="2" value="${employee.positionName}" maxlength="100" />
        </div>
    </div>

    <s:set var="selected_groups" value="employee.groups"/>

    <div class="form-group">
        <tw:label labelName="groups" class="col-md-3 control-label">Employee Groups</tw:label>
        <div class="col-md-4">
            <tw:select selectName="groups" multiple="true" class="form-control select2" tabindex="3">
                <s:iterator value="employeeGroups" var="contractor_group">
                    <s:set var="is_selected" value="false"/>
                    <s:iterator value="#selected_groups" var="selected_group">
                        <s:if test="#selected_group.group.name == #contractor_group.name">
                            <s:set var="is_selected" value="true"/>
                        </s:if>
                    </s:iterator>

                    <tw:option value="${contractor_group.name}" selected="${is_selected}">${contractor_group.name}</tw:option>
                </s:iterator>
            </tw:select>
        </div>
    </div>

    <div class="form-group">
        <div class="col-md-9 col-md-offset-3 form-actions">
            <tw:button buttonName="save" type="submit" class="btn btn-success" tabindex="4">Save</tw:button>
            <tw:button buttonName="cancel" type="button" class="btn btn-default cancel" tabindex="5">Cancel</tw:button>
        </div>
    </div>
</tw:form>