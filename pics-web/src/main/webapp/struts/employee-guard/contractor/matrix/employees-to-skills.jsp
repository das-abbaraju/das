<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
	<s:param name="title">Employees / Skill Matrix</s:param>
</s:include>



<table id="contractor_employees_to_skills_matrix" class="table table-striped table-bordered table-condensed matrix">
    <s:url action="matrix/employee-to-skills" var="contractor_employee_to_skills_matrix_url">
        <s:param name="id">1</s:param>
    </s:url>
    
	<tr>
		<th class="col-md-2">
			Employees
		</th>
		<th class="col-md-2">
			Forklift
		</th>
		<th class="col-md-2">
			Gas Freeing
		</th>
		<th class="col-md-2">
			H2S Hazards
		</th>
		<th class="col-md-2">
			Rigging
		</th>
		<th class="col-md-2">
			Startup Equipment
		</th>
		<th class="col-md-2">
			Tank & Vessel
		</th>
	</tr>
    
	<tr>
		<td class="col-md-2">
			<a href="${contractor_employee_to_skills_matrix_url}">Desia, Mattia</a>
		</td>
		<td class="col-md-2">
		</td>
		<td class="complete">
			<i class="icon-ok-sign icon-large"></i>
		</td>
		<td class="complete">
			<i class="icon-ok-sign icon-large"></i>
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
	</tr>
	<tr>
		<td class="col-md-2">
			<a href="${contractor_employee_to_skills_matrix_url}">Hinoki, Samurai</a>
		</td>
		<td class="complete">
			<i class="icon-ok-sign icon-large"></i>
		</td>
		<td class="col-md-2">
		</td>
		<td class="expire">
            <i class="icon-warning-sign icon-large"></i>
		</td>
		<td class="incomplete">
            <i class="icon-minus-sign-alt icon-large"></i>
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
	</tr>
	<tr>
		<td class="col-md-2">
			<a href="${contractor_employee_to_skills_matrix_url}">Mower, David</a>
		</td>
		<td class="incomplete">
			<i class="icon-minus-sign-alt icon-large"></i>
		</td>
		<td class="incomplete">
			<i class="icon-minus-sign-alt icon-large"></i>
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
		<td class="incomplete">
			<i class="icon-minus-sign-alt icon-large"></i>
		</td>
	</tr>
</table>