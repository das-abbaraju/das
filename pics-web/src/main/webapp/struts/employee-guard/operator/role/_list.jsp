<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!#operator_roles.isEmpty()">
    <ul class="employee-guard-list roles">
        <s:iterator value="#operator_roles" var="operator_role">
            <s:url action="role" var="operator_role_show_url">
                <s:param name="id">${operator_role.role.id}</s:param>
            </s:url>

            <li>
                <a href="${operator_role_show_url}">
                    <span class="label label-pics">${operator_role.role.name}</span>
                </a>
            </li>
        </s:iterator>
    </ul>
</s:if>