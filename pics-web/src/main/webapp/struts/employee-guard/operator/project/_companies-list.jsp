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
                <h4>No Requested Companies!</h4>

                <p>You must request specific companies to this project. Once requested, those companies will be able to view this project and assign their employees to the Job Roles specified for the project.</p>

                <p>Select <strong>Edit</strong> ( <i class="icon-edit icon-large"></i> ) in the <strong>Companies Requested for Project</strong> bar above to add Job Roles to this project.</p>
            </div>
        </div>
    </div>
</s:else>
