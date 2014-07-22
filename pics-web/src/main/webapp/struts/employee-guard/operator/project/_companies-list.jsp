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
<s:else>
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="alert alert-warning">
                <h4><s:text name="OPERATOR.PROJECT.COMPANIES_REQUESTED.EDIT.NO_COMPANIES_MSG.TITLE"/></h4>

                <p><s:text name="OPERATOR.PROJECT.COMPANIES_REQUESTED.EDIT.NO_COMPANIES_MSG1"/></p>

                <p><s:text name="OPERATOR.PROJECT.COMPANIES_REQUESTED.EDIT.NO_COMPANIES_MSG2"/></p>
            </div>
        </div>
    </div>
</s:else>
