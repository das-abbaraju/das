<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="employee-group" method="edit" var="contractor_group_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>

<tw:form formName="contractor_group_edit_employees" action="${contractor_group_edit_url}" method="post" class="form-horizontal" role="form">
    <fieldset>
        <s:set var="selected_employees" value="group.employees"/>

        <div class="form-group">
            <tw:select selectName="employees" multiple="true" class="form-control select2" autofocus="true" tabindex="1">
                <s:iterator value="groupEmployees" var="contractor_employee">
                    <s:set var="is_selected" value="false"/>
                    <s:iterator value="#selected_employees" var="selected_employee">
                        <s:if test="#selected_employee.employee.id == #contractor_employee.id">
                            <s:set var="is_selected" value="true"/>
                        </s:if>
                    </s:iterator>

                    <tw:option value="${contractor_employee.id}" selected="${is_selected}">${contractor_employee.firstName} ${contractor_employee.lastName}</tw:option>
                </s:iterator>
            </tw:select>
        </div>

        <div class="form-group">
            <tw:button buttonName="save" type="submit" class="btn btn-success btn-block" tabindex="2">Save</tw:button>
            <tw:button buttonName="cancel" type="button" class="btn btn-default btn-block cancel" tabindex="3">Cancel</tw:button>
        </div>
    </fieldset>

</tw:form>