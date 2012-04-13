<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><s:text name="global.EmployeeGUARD" /></title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp" />

</head>
<body>
	<s:include value="../contractors/conHeader.jsp"></s:include>

	<s:include value="../actionMessages.jsp" />

	<s:set name="canEditEmployees"
		value="permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorAdmin) || permissions.hasPermission('ManageEmployees', 'Edit')" />

	<s:set name="canEditJobRoles"
		value="permissions.admin || permissions.hasPermission('DefineRoles', 'Edit')" />
		
	<s:set name="canEditCompetencies"
		value="permissions.hasPermission('DefineCompetencies', 'Edit')" />

	<s:set name="canAddAudits"
		value="manuallyAddAudit" />
		
	<table id="employee_dashboard">
		<tr>
			<td style="vertical-align: top; width: 48%">
				<!--  1st Column -->
				<div class="panel_placeholder">
					<div class="panel">
						<div class="panel_header"><s:text name="global.Employees" /></div>
						<div class="panel_content">
							<s:if test="activeEmployees.size() > 0">
								<table class="table">
									<thead>
										<tr>
											<th><s:text name="Employee.lastName" /></th>
											<th><s:text name="Employee.firstName" /></th>
											<th><s:text name="Employee.title" /></th>
											<th><s:text name="Employee.classification" /></th>
											<th><s:text name="EmployeeDashboard.JobRoles" /></th>
										</tr>
									</thead>
									<s:iterator value="activeEmployees" var="employee"
										status="stat">
										<tr <s:if test="(#stat.index + 1)%2 == 1">class="odd"</s:if>>
											<td><s:property value="#employee.lastName" />
											</td>
											<td><s:property value="#employee.firstName" />
											</td>
											<td><s:property value="#employee.title" />
											</td>
											<td><s:property value="#employee.classification" />
											</td>
											<td><s:iterator value="#employee.employeeRoles" var="er"
													status="line">
													<s:if test="#line.index > 0">, </s:if>
													<s:property value="#er.jobRole.name" />
												</s:iterator></td>
										</tr>
									</s:iterator>
								</table>
							</s:if>
							
							<s:if test="canEditEmployees" >
								<a href="ManageEmployees.action?id=<s:property value='id' />"
									class="edit"><s:text name="EmployeeDashboard.EditEmployees" /></a>
							</s:if>
						</div>
					</div>
				</div>
				<br />
				<div class="panel_placeholder">
					<div class="panel">
						<div class="panel_header"><s:text name="EmployeeDashboard.JobRolesTitle" /></div>
						<div class="panel_content">
							<s:if test="contractor.jobRoles.size() > 0">
								<table class="table">
									<thead>
										<tr>
											<th><s:text name="global.JobRole" /></th>
											<th><s:text name="global.Active" /></th>
											<th><s:text name="global.HSECompetencies" /></th>
										</tr>
									</thead>
									<s:iterator value="contractor.jobRoles" var="role" status="stat">
										<tr <s:if test="(#stat.index + 1)%2 == 1">class="odd"</s:if>>
											<td><s:property value="#role.name" /></td>
											<td class="center">
												<s:if test="active">
													<s:text name="YesNo.Yes" />
												</s:if>
												<s:else>
													<s:text name="YesNo.No" />
												</s:else>
											</td>
											<td>
												<s:set var="addBreak" value="false" />
												<s:iterator value="#role.jobCompetencies" var="jobCompetency" >
													<s:if test="#addBreak" >, </s:if>
													<s:set var="addBreak" value="true" />
													<s:property value="#jobCompetency.competency.label" />
												</s:iterator>
											</td>
										</tr>
									</s:iterator>
								</table>
							</s:if>
							<s:if test="#canEditJobRoles" >
								<a href="ManageJobRoles.action?id=<s:property value="id" />"
									class="edit"><s:text name="EmployeeDashboard.EditJobRoles" /></a>
								<br />
							</s:if>

						</div>
					</div>
				</div>
			</td>
			<td width="15px"></td>
			<td style="vertical-align: top; width: 48%">
				<!--  2nd Column -->
				<div class="panel_placeholder">
					<div class="panel">
						<div class="panel_header"><s:text name="global.Audits" /></div>
						<div class="panel_content">
							<s:if test="employeeGuardAudits.size() > 0" >
								<table class="table">
									<thead>
										<tr>
											<th><s:text name="EmployeeDashboard.Audit" /></th>
											<th><s:text name="EmployeeDashboard.Employee" /></th>
											<th><s:text name="EmployeeDashboard.Location" /></th>
											<th><s:text name="EmployeeDashboard.Effective" /></th>
											<th><s:text name="EmployeeDashboard.Status" /></th>
										</tr>
									</thead>
								<s:iterator value="employeeGuardAudits"  var="audit" status="stat" >
									<tr <s:if test="(#stat.index + 1)%2 == 1">class="odd"</s:if>>
										<td>
											<a href="Audit.action?auditID=<s:property value='#audit.id' />"> <s:property value='getAuditName(#audit)' /></a>
										</td>
										<td>
											<s:if test="#audit.employee !=null" >
												<s:property value='#audit.employee.nameTitle' />
											</s:if>
										</td>
										<td>
											<s:if test="#audit.requestingOpAccount !=null" >
												<s:property value='#audit.requestingOpAccount.name' />
											</s:if>
										</td>
										<td>
											<s:if test="#audit.effectiveDate !=null" >
												<s:date name="#audit.effectiveDate" format="%{getText('date.short')}" />
											</s:if>
										</td>
										<td>
											<s:if test="#audit.operators.size > 0">
												<s:iterator value="#audit.getCaoStats(permissions).keySet()" id="status" status="stat">
													<s:if test="getCaoStats(permissions).get(#status) > 1 || #audit.getCaoStats(permissions).keySet().size > 1">
														<s:property value="getCaoStats(permissions).get(#status)" />
													</s:if>
															
													<s:text name="%{#status.getI18nKey()}" /><s:if test="!#stat.last">,</s:if>
												</s:iterator>
											</s:if>
										</td>
									</tr>
								</s:iterator>
								</table>
							</s:if>
							<s:if test="#canAddAudits">
								<s:set var="addBreak" value="false" />
								<s:if test="isValidManualAuditType(29)" >
									<a class="add" href="AuditOverride.action?id=<s:property value="id"/>&selectedAudit=29"><s:text name="EmployeeDashboard.CreateNewAuditImplementationAuditPlus" /></a>
									<s:set var="addBreak" value="true" />
								</s:if>
								<s:if test="#addBreak" >
									<br />
								</s:if>
								<s:if test="isValidManualAuditType(17)" >
									<a class="add" href="AuditOverride.action?id=<s:property value="id"/>&selectedAudit=17"><s:text name="EmployeeDashboard.CreateIntegrityManagement" /></a>
								</s:if>
							</s:if>
						</div>
					</div>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>