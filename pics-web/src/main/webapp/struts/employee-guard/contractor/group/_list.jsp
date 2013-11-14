<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!#contractor_groups.isEmpty()">
    <ul class="employee-guard-list roles">
        <s:iterator value="#contractor_groups" var="contractor_group">
            <s:url action="employee-group" var="contractor_role_show_url">
                <s:param name="id">${contractor_group.group.id}</s:param>
            </s:url>

            <li>
                <a href="${contractor_role_show_url}"><span class="label label-pics">${contractor_group.group.name}</span></a>
            </li>
        </s:iterator>
    </ul>
</s:if>