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
	function checkSelections() {
		if (!$('form .selectable').is(':checked')) {
			alert("Please select an employee.");
			return false;			
		}

		if ($('#operatorId').val() == 0) {
			alert("Please select an operator.");
			return false;
		}
		return true;
	}
</script>
</head>
<body>
	<s:include value="../contractors/conHeader.jsp"></s:include>

	<s:include value="../actionMessages.jsp" />

	<s:if
		test="permissions.admin || permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorAdmin)">
		<a href="ManageJobRoles.action?id=<s:property value="id" />"
			class="edit">Edit <s:text name="ManageEmployees.header.JobRoles" />
		</a>
		<br />
	</s:if>

	<s:set name="canAddAudits"
		value="permissions.hasPermission('ManageAudits', 'Edit')" />
	<table id="employee_dashboard">
		<tr>
			<td style="vertical-align: top; width: 48%">
				<div class="panel_placeholder">
					<div class="panel">
						<div class="panel_header">Employees</div>
						<div class="panel_content">
							<table class="table">
								<thead>
									<tr>
										<td></td>
										<td>Last Name</td>
										<td>First Name</td>
										<td>Title</td>
										<td>Classification</td>
										<td>Job Roles</td>
										<td>Edit</td>
										<td>Summary</td>
									</tr>
								</thead>
								<s:iterator value="activeEmployees" var="employee" status="stat">
									<tr <s:if test="(#stat.index + 1) == true">class="odd"</s:if>>
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
												<s:if test="#line.index > 0">
													<br />
												</s:if>
												<s:property value="#er.jobRole.name" />
											</s:iterator></td>
										<td></td>
										<td></td>
									</tr>
								</s:iterator>
							</table>
							<s:if
								test="permissions.admin || permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorAdmin)">
								<a href="ManageEmployees.action?id=<s:property value="id" />"
									class="edit">Edit <s:text name="global.Employees" /> </a>
							</s:if>
						</div>
					</div>
				</div>
			</td>
			<td width="15px"></td>
			<td style="vertical-align: top; width: 48%"></td>
		</tr>
		<tr>
			<td style="vertical-align: top; width: 48%">
				<div class="panel_placeholder">
					<div class="panel">
						<div class="panel_header">Audits</div>
						<div class="panel_content">
							<s:form id="employeeStatus" method="post" cssClass="forms">
								<s:hidden name="id" />
								<table class="table">
									<thead>
										<tr>
											<s:if test="#canAddAudits">
												<th colspan="2"></th>
											</s:if>
											<s:else>
												<th><s:property value="#canAddAudits" /></th>
											</s:else>
											<th align="center">Employee</th>
											<th align="center">Title</th>
											<th align="center">Audits</th>
										</tr>
									</thead>
									<s:set name="rowNum" value="0" />
									<s:iterator value="activeEmployees" var="employee"
										status="stat">
										<tr <s:if test="(#stat.index + 1) == true">class="odd"</s:if>>
											<td class="center"><s:property value="#stat.index + 1" />
											</td>
											<s:set name="rowNum" value="#stat.index + 1" />
											<s:if test="#canAddAudits">
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
									<s:if test="UnattachedEmployeeAudits.size() > 0">
										<tr <s:if test="(#rowNum % 2) == 0">class="odd"</s:if>>
											<td><s:property value="#rowNum + 1" /> <s:if
													test="#canAddAudits">
													<td></td>
												</s:if>
											<td></td>
											<td></td>
											<td><s:set var="addBreak" value="false" /> <s:iterator
													value="UnattachedEmployeeAudits" var="unattachedAudit">
													<s:if test="#unattachedAudit.isVisibleTo(permissions)">
														<s:if test="#addBreak">
															<br />
														</s:if>
														<a
															href="Audit.action?auditID=<s:property value="#unattachedAudit.id" />">
															<s:property value='#unattachedAudit.auditFor' /> <s:property
																value='#unattachedAudit.auditType.name' /></a>
														<s:set var="addBreak" value="true" />
													</s:if>
												</s:iterator></td>
										</tr>
									</s:if>
								</table>

								<s:if test="#canAddAudits && visibleOperators.size() > 0">
								Operator: 
								<s:select id="operatorId" name="selectedOperator"
										list="contractor.nonCorporateOperators" headerKey=""
										headerValue="- Operator -" listKey="operatorAccount.id"
										listValue="operatorAccount.name" />
									<br />
									<s:if
										test="isAuditTypeAddable(@com.picsauditing.jpa.entities.AuditType@INTEGRITYMANAGEMENT)">
										<s:submit name="button" cssClass="picsbutton "
											value="Create Integrity Management Audit(s)"
											method="addIntegrityManagementAudits"
											onclick="return checkSelections() " />
										<br />
									</s:if>
									<s:if
										test="isAuditTypeAddable(@com.picsauditing.jpa.entities.AuditType@IMPLEMENTATIONAUDITPLUS)">
										<s:submit name="button" cssClass="picsbutton "
											value="Create Implementation Audit Plus Audit(s)"
											method="addImplementationAuditPlusAudits"
											onclick="return checkSelections()" />
									</s:if>
								</s:if>
							</s:form>
						</div>
					</div>
				</div></td>
			<td width="15px"></td>
			<td style="vertical-align: top; width: 48%"></td>
		</tr>
	</table>

</body>
</html>