<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!#operator_companies.isEmpty()">
    <ul class="employee-guard-list roles edit-display-values">
        <s:iterator value="#operator_companies" var="operator_company">
            <li>
                <span class="label label-pics">${operator_company.name}</span>
            </li>
        </s:iterator>
    </ul>
</s:if>
