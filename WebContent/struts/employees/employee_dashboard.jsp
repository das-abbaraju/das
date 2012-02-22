<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
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

<s:include value="../jquery.jsp" />

<script type="text/javascript">
	function checkSelections(listId) {
		if (!$('form .selectable').is(':checked')) {
			alert("Please select an employee for audit.");
			return false;			
		}
		
		var e = document.getElementById(listId);
		var value = e.options[e.selectedIndex].value;
		document.forms['empAudits'].selectedOperatorId.value = value;
		
		return true;
	}
</script>

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
		
	<table id="employee_dashboard">
		<tr>
			<td style="vertical-align: top; width: 48%">
				<!--  1st Column -->
				<div class="panel_placeholder">
					<div class="panel">
						<div class="panel_header">Employees</div>
						<div class="panel_content">
							<s:if test="activeEmployees.size() > 0">
								<table class="table">
									<thead>
										<tr>
											<th></th>
											<th>Last Name</th>
											<th>First Name</th>
											<th>Title</th>
											<th>Classification</th>
											<th>Job Roles</th>
<%-- 											<s:if test="canEditEmployees" > --%>
<!-- 												<th>Edit</th> -->
<%-- 											</s:if> --%>
										</tr>
									</thead>
									<s:iterator value="activeEmployees" var="employee"
										status="stat">
										<tr <s:if test="(#stat.index + 1)%2 == 1">class="odd"</s:if>>
											<td class="center"><s:property value="#stat.index + 1" />
											</td>
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
<%-- 											<s:if test="canEditEmployees" > --%>
<!-- 												<td> -->
<%-- 													<a href="ManageEmployees.action?id=<s:property value="id" />#employee=<s:property value="#employee.id" />" --%>
<!-- 									class="edit center" ></a> -->
<!-- 												</td> -->
<%-- 											</s:if> --%>
										</tr>
									</s:iterator>
								</table>
							</s:if>
							
							<s:if test="canEditEmployees" >
								<a href="ManageEmployees.action?id=<s:property value="id" />"
									class="edit">Edit <s:text name="global.Employees" /> </a>
							</s:if>
						</div>
					</div>
				</div>
				<br />
				<div class="panel_placeholder">
					<div class="panel">
						<div class="panel_header">Job Roles and HSE Job Competencies</div>
						<div class="panel_content">
							<s:if test="contractor.jobRoles.size() > 0">
								<table class="table">
									<thead>
										<tr>
											<th></th>
											<th>Job Role</th>
											<th>Active</th>
											<th>HSE Competencies</th>
<%-- 											<s:if test="#canEditJobRoles || #canEditCompetencies" > --%>
<!-- 												<th>Edit</th> -->
<%-- 											</s:if> --%>
										</tr>
									</thead>
									<s:iterator value="contractor.jobRoles" var="role" status="stat">
										<tr <s:if test="(#stat.index + 1)%2 == 1">class="odd"</s:if>>
											<td class="center"><s:property value="#stat.index + 1" /></td>
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
<%-- 											<s:if test="#canEditJobRoles" > --%>
<%-- 												<td><a href="ManageJobRoles.action?role=<s:property value="#role.id" />&account=<s:property value="#role.account.id" />" class="edit"></a> --%>
<!-- 												</td> -->
<%-- 											</s:if> --%>
										</tr>
									</s:iterator>
								</table>
							</s:if>
							<s:if test="#canEditJobRoles" >
<%-- 								<s:if test="permissions.admin || permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorAdmin)"> --%>
								<a href="ManageJobRoles.action?id=<s:property value="id" />"
									class="edit">Edit <s:text name="ManageEmployees.header.JobRoles" /> </a>
								<br />
							</s:if>

						</div>
					</div>
				</div>

<!-- 				<div class="panel_placeholder"> -->
<!-- 					<div class="panel"> -->
<!-- 						<div class="panel_header">Projects and Job Sites</div> -->
<!-- 						<div class="panel_content"></div> -->
<!-- 					</div> -->
<!-- 				</div> -->
			</td>
			<td width="15px"></td>
			<td style="vertical-align: top; width: 48%">
				<!--  2nd Column -->
				<div class="panel_placeholder">
					<div class="panel">
						<div class="panel_header">Audits</div>
						<div class="panel_content">
							<s:form id="empAudits" method="post" cssClass="forms">
								<s:hidden name="id" />
								<s:if
									test="activeEmployees.size() > 0 || unattachedEmployeeAudits.size() > 0">
									<table class="table">
										<thead>
											<tr>
												<s:if test="canAddAudits">
													<th colspan="2"></th>
												</s:if>
												<s:else>
													<th></th>
												</s:else>
												<th align="center">Employee</th>
												<th align="center">Title</th>
												<th align="center">Audits</th>
											</tr>
										</thead>
										<s:set name="rowNum" value="0" />
										<s:iterator value="activeEmployees" var="employee"
											status="stat">
											<tr <s:if test="(#stat.index + 1)%2 == 1">class="odd"</s:if>>
												<td class="center"><s:property value="#stat.index + 1" />
												</td>
												<s:set name="rowNum" value="#stat.index + 1" />
												<s:if test="canAddAudits">
													<td class="center"><input type="checkbox"
														name="selectedEmployeeIds"
														value="<s:property value="#employee.id" />"
														class="selectable" />
													</td>
												</s:if>
												<td><s:property value="#employee.firstName" /> <s:property
														value="#employee.lastName" /></td>
												<td><s:property value="#employee.title" /></td>
												<td><s:set var="addBreak" value="false" /> <s:iterator
														value="#employee.audits" var="empAudit">
														<s:if
															test="#empAudit.isVisibleTo(permissions) && (#empAudit.auditType.id==17 || #empAudit.auditType.id==29)">
															<s:if test="#addBreak">
																<br />
															</s:if>
															<a
																href="Audit.action?auditID=<s:property value='#empAudit.id' />">
																<s:property value='#empAudit.auditType.name' /> for <s:property
																	value='#empAudit.requestingOpAccount.name' /> </a>
															<s:set var="addBreak" value="true" />
														</s:if>
													</s:iterator>
												</td>
											</tr>
										</s:iterator>
										<s:iterator value="unattachedEmployeeAudits" var="unattachedAudit" status="stat">
											<tr <s:if test="((#rowNum + #stat.index) % 2) == 0">class="odd"</s:if>>
												<td><s:property value="#rowNum + #stat.index + 1" /></td>
												<s:if test="canAddAudits"> <td></td> </s:if>
												<td></td>
												<td></td>
												<td>
													<a href="Audit.action?auditID=<s:property value="#unattachedAudit.id" />">
																<s:property value='#unattachedAudit.auditFor' /> <s:property
																	value='#unattachedAudit.auditType.name' /> </a>
												</td>
											</tr>										
										</s:iterator>
									</table>
								</s:if>
								<s:if test="canAddAudits">
									<s:hidden name="selectedOperatorId" />
									<s:if test="isAuditTypeAddable(@com.picsauditing.jpa.entities.AuditType@INTEGRITYMANAGEMENT) && getOperatorsByAuditTypeId(17).size() > 0">
										<s:submit name="button" method="addIntegrityManagementAudits" onclick="return checkSelections('imOperator')" />
										Integrity Management Audits for <s:select id="imOperator" list="getOperatorsByAuditTypeId(17)" listKey="id" listValue="name" />
										<br />
									</s:if>
									<s:if test="isAuditTypeAddable(@com.picsauditing.jpa.entities.AuditType@IMPLEMENTATIONAUDITPLUS) && getOperatorsByAuditTypeId(29).size() > 0">
										<s:submit name="button" method="addImplementationAuditPlusAudits" onclick="return checkSelections('iapOperator')"/>
										Implementation Audit Plus Audits for <s:select id="iapOperator" list="getOperatorsByAuditTypeId(29)" listKey="id" listValue="name" />
									</s:if>
								</s:if>
							</s:form>
						</div>
					</div>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>