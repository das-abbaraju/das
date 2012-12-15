<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:url action="EmployeeDashboard" var="employee_dashboard">
	<s:param name="id">
		${account.id}
	</s:param>
</s:url>

<h1>
	<s:property value="account.name" />

	<span class="sub">
		<s:text name="ManageEmployees.title" />
	</span>
</h1>

<s:include value="../../actionMessages.jsp"/>

<s:if test="audit == null" >
	<div>
		<a href="${employee_dashboard}">
			<s:text name="global.EmployeeGUARD" />
		</a>
	</div>
</s:if>
