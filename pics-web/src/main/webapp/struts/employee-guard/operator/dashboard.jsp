<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="skill" var="operator_skill_url"/>
<s:url action="role" var="operator_role_url"/>
<s:url action="project" var="operator_project_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">EmployeeGUARD</s:param>
    <s:param name="subtitle">Summary</s:param>
    <s:param name="breadcrumbs">false</s:param>
</s:include>

<div class="row">
    <ul class="nav nav-pills nav-stacked col-md-3">
        <li>
            <a href="${operator_project_url}"><i class="icon-sitemap"></i>Assignments and Projects</a>
        </li>
        <li>
            <a href="${operator_skill_url}"><i class="icon-certificate"></i>Skills</a>
        </li>
        <li>
            <a href="${operator_role_url}"><i class="icon-group"></i>Job Roles</a>
        </li>
        <%--         <li>
                    <a href="#"><i class="icon-ok"></i>Quick Manage</a>
                </li> --%>

    </ul>

</div>