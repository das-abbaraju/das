<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>EmployeeGUARD&trade; Dashboard</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />
</head>
<body>
	<s:include value="../contractors/conHeader.jsp"></s:include>

	<s:include value="../actionMessages.jsp" />

	<s:if test="permissions.admin || permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorAdmin)" >
		<a href="ManageEmployees.action?id=<s:property value="id" />">Edit <s:text name="global.Employees" /></a><br />
		<a href="ManageJobRoles.action?id=<s:property value="id" />">Edit <s:text name="ManageEmployees.header.JobRoles" /></a><br />
	</s:if>
	
	<s:form id="employeeStatus" method="post" cssClass="forms">
		<s:hidden name="id" />
		<table class="report">
			<thead>
				<tr>
					<td colspan="2"></td>
					<td align="center">Last Name</td>
					<td align="center">First Name</td>
					<td align="center">Title</td>
					<td align="center">Classification</td>
					<td align="center">Job Roles</td>
					<td align="center">Integrity Management</td>
					<td align="center">Integrity Audit Plus</td>
				</tr>
			</thead>
			<s:iterator value="activeEmployees" var="employee" status="stat">
				<tr>
					<td class="right">
						<s:property value="#stat.index + 1" />
					</td>
					<td class="center">
						<input type="checkbox" name="selectedEmployeeIds" value="<s:property value="#employee.id" />" class="selectable" />
					</td>
					<td><s:property value="#employee.lastName" /></td>
					<td><s:property value="#employee.firstName" /></td>
					<td><s:property value="#employee.title" /></td>
					<td><s:property value="#employee.classification" /></td>
					<td>
						<s:iterator value="#employee.employeeRoles" var="er" status="line">
							<s:if test="#line.index > 0"><br /></s:if><s:property value="#er.jobRole.name" />
						</s:iterator>
					</td>
					<td>
						<s:set var="addBreak" value="false" />
						<s:iterator value="#employee.audits" var="empAudit">
							<s:if test="#empAudit.auditType.id==17">
								<s:if test="#addBreak"><br /></s:if>
								<a href="Audit.action?auditID=<s:property value='#empAudit.id' />"><s:property value='#empAudit.auditType.name' /></a>
								<s:set var="addBreak" value="true" />
							</s:if>
						</s:iterator>
					</td>
					<td>
						<s:set var="addBreak" value="false" />
						<s:iterator value="#employee.audits" var="empAudit">
							<s:if test="#empAudit.auditType.id==29">
								<s:if test="#addBreak"><br /></s:if>
								<a href="Audit.action?auditID=<s:property value='#empAudit.id' />"><s:property value='#empAudit.auditType.name' /></a>
								<s:set var="addBreak" value="true" />
							</s:if>
						</s:iterator>
					</td>
				</tr>
			</s:iterator>
		</table>
		
		<s:if test="permissions.admin || permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorAdmin)" >
			<s:if test="contractor.nonCorporateOperators != null && contractor.nonCorporateOperators.size() > 1" >
				<label>Operator</label>
				<s:select name="selectedOperator" list="contractor.nonCorporateOperators" headerKey="" headerValue="- Operator -" listKey="operatorAccount.id" listValue="operatorAccount.name" />
			</s:if>
			<s:if test="isAuditTypeAddable(@com.picsauditing.jpa.entities.AuditType@INTEGRITYMANAGEMENT)" >
				<s:submit name="button" cssClass="picsbutton " value="Create Integrity Management Audit(s)" method="addIntegrityManagementAudits"/>
			</s:if>
			<s:if test="isAuditTypeAddable(@com.picsauditing.jpa.entities.AuditType@IMPLEMENTATIONAUDITPLUS)" >
				<s:submit name="button" cssClass="picsbutton " value="Create Integrity Management Audit(s)" method="addImplementationAuditPlusAudits"/>
			</s:if>
		</s:if>
	</s:form>
	
</body>
</html>