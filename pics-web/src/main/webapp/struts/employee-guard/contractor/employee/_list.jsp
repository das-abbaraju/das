<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!#contractor_employees.isEmpty()">
    <ul class="employee-guard-list employees edit-display-values">
        <s:iterator value="#contractor_employees" var="contractor_employee">
            <s:if test="#contractor_employee.employee.deletedBy == 0 && #contractor_employee.employee.deletedDate == null">
                <s:url action="employee" var="contractor_employee_show_url">
                    <s:param name="id">${contractor_employee.employee.id}</s:param>
                </s:url>

                <li>
                    <a href="${contractor_employee_show_url}"><span class="label label-pics">${contractor_employee.employee.firstName} ${contractor_employee.employee.lastName}</span></a>
                </li>
            </s:if>
        </s:iterator>
    </ul>
</s:if>