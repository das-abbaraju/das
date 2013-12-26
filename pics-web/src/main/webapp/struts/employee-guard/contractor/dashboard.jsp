<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="employee" var="contractor_employee_url" />
<s:url action="employee-group" var="contractor_role_url" />
<s:url action="skill" var="contractor_skill_url" />
<s:url action="project" var="contractor_project_url" />
<s:url action="matrix/employee-groups-to-employees" var="contractor_employee_groups_to_employees_matrix_url" />
<s:url action="matrix/employees-to-skills" var="contractor_employees_to_skills_matrix_url" />
<s:url action="matrix/skills-to-employee-groups" var="contractor_skills_to_employee_groups_matrix_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">EmployeeGUARD</s:param>
    <s:param name="subtitle">Summary</s:param>
    <s:param name="breadcrumbs">false</s:param>
</s:include>

<div class="row">
    <ul class="nav nav-pills nav-stacked col-md-3">
        <li>
            <a href="${contractor_employee_url}"><i class="icon-user"></i>Employees</a>
        </li>
        <li>
            <a href="${contractor_role_url}"><i class="icon-group"></i>Employee Groups</a>
        </li>
        <li>
            <a href="${contractor_skill_url}"><i class="icon-certificate"></i>Skills</a>
        </li>
        <li>
            <a href="${contractor_project_url}"><i class="icon-sitemap"></i>Assignments and Projects</a>
        </li>
        <%-- <li>
            <a href="#"><i class="icon-ok"></i>Quick Manage</a>
        </li>
        <li>
            <a href="${contractor_employee_groups_to_employees_matrix_url}">Employee Groups / Employees Matrix</a>
        </li>
        <li>
            <a href="${contractor_employees_to_skills_matrix_url}">Employees / Skills Matrix</a>
        </li>
        <li>
            <a href="${contractor_skills_to_employee_groups_matrix_url}">Skills / Employee Groups Matrix</a>
        </li> --%>
    </ul>
<%--     <div class="col-md-9">
        <section class="employee-guard-section">
            <h1>Updates</h1>

            <div class="content"></div>
        </section>

        <section class="employee-guard-section">
            <h1>Alerts</h1>

            <div class="content"></div>
        </section>

        <section class="employee-guard-section">
            <h1>Status Changes</h1>

            <div class="content"></div>
        </section>
    </div> --%>
</div>