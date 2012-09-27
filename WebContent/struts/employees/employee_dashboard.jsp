<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>
		<s:text name="global.EmployeeGUARD" />
	</title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	
	<s:include value="../jquery.jsp" />
	
	<style type="text/css">
		#employee_dashboard,
		.table
		{
			width: 100%;
		}
		
		.table tbody tr:nth-child(odd)
		{
			background-color: #F1F5FA;
		}
		
		.column
		{
			vertical-align: top;
			width: 48%;
		}
	</style>
</head>
<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<s:include value="../contractors/conHeader.jsp" />
		<s:include value="../actionMessages.jsp" />
	
		<table id="employee_dashboard">
			<tr>
				<td class="column">
					<div class="panel_placeholder">
						<div class="panel">
							<div class="panel_header">
								<s:text name="global.Audits" />
							</div>
							<div class="panel_content">
								<s:if test="displayedAudits.size() > 0 || auditsByYearAndType.size() > 0">
									<table class="table">
										<thead>
											<tr>
												<th>
													<s:text name="EmployeeDashboard.Audit" />
												</th>
												<th>
													<s:text name="EmployeeDashboard.Employee" />
												</th>
												<th>
													<s:text name="EmployeeDashboard.Location" />
												</th>
												<th>
													<s:text name="EmployeeDashboard.Effective" />
												</th>
												<th>
													<s:text name="EmployeeDashboard.Status" />
												</th>
												<th><s:text name="EmployeeDashboard.Requirements" /></th>
											</tr>
										</thead>
										<tbody>
											<s:iterator value="yearsDescending" var="audit_group_year">
												<s:iterator value="distinctAuditTypes" var="audit_group_type">
													<s:if test="auditsByYearAndType.get(#audit_group_year, #audit_group_type) != null">
														<s:url action="EmployeeDashboard" method="employeeGUARDAudits" var="employee_audit_group">
															<s:param name="id">
																${contractor.id}
															</s:param>
															<s:param name="auditTypeID">
																${audit_group_type.id}
															</s:param>
															<s:param name="year">
																${audit_group_year}
															</s:param>
														</s:url>
														<tr>
															<td>
																<a href="${employee_audit_group}">
																	<s:text name="%{#audit_group_type.getI18nKey('name')}" />
																</a>
															</td>
															<td>
																${auditsByYearAndType.get(audit_group_year, audit_group_type)}
															</td>
															<td></td>
															<td>
																${audit_group_year}
															</td>
															<td></td>
															<td></td>
														</tr>
													</s:if>
												</s:iterator>
											</s:iterator>
											<s:iterator value="displayedAudits" var="audit" status="stat">
												<s:url action="Audit" var="audit_link">
													<s:param name="auditID">
														${audit.id}
													</s:param>
												</s:url>
												<tr>
													<td>
														<a href="${audit_link}">
															<s:property value='getAuditName(#audit)' />
														</a>
													</td>
													<td>
														<s:if test="#audit.employee !=null">
															<s:property value='#audit.employee.nameTitle' />
														</s:if>
													</td>
													<td>
														<s:if test="#audit.requestingOpAccount !=null">
															<s:property value='#audit.requestingOpAccount.name' />
														</s:if>
													</td>
													<td>
														<s:if test="#audit.effectiveDate !=null">
															<s:date name="#audit.effectiveDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
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
													<td>
														<s:if test="#audit.hasOpenRequirements">
															<a href="ContractorAuditFileUpload.action?auditID=<s:property value='#audit.id'/>" ><s:text name="EmployeeDashboard.Upload" /></a>
														</s:if>
													</td>
												</tr>
											</s:iterator>
										</tbody>
									</table>
								</s:if>
								<s:if test="methodName != null">
									<s:url action="EmployeeDashboard" var="employee_dashboard">
										<s:param name="id">
											${id}
										</s:param>
									</s:url>
									<a href="${employee_dashboard}" class="preview">
										<s:text name="ContractorDocuments.link.ViewAll" />
									</a>
									<br />
									<s:set var="addBreak" value="true" />
								</s:if>
								<s:if test="manuallyAddAudits">
									<s:if test="#addBreak">
										<br />
									</s:if>
									<s:set var="addBreak" value="false" />
									<s:if test="isValidManualAuditType(29)">
										<s:url action="AuditOverride" var="audit_override_training_verification">
											<s:param name="id">
												${id}
											</s:param>
											<s:param name="selectedAudit" value="29" />
										</s:url>
										<a class="add" href="${audit_override_training_verification}">
											<s:text name="EmployeeDashboard.CreateNewAuditImplementationAuditPlus" />
										</a>
										<s:set var="addBreak" value="true" />
									</s:if>
									<s:if test="#addBreak">
										<br />
									</s:if>
									<s:if test="isValidManualAuditType(17)">
										<s:url action="AuditOverride" var="audit_override_competency_review">
											<s:param name="id">
												${id}
											</s:param>
											<s:param name="selectedAudit" value="17" />
										</s:url>
										<a class="add" href="${audit_override_competency_review}">
											<s:text name="EmployeeDashboard.CreateIntegrityManagement" />
										</a>
									</s:if>
								</s:if>
							</div>
						</div>
					</div>
				</td>
				<td width="15px"></td>
				<td class="column">
					<div class="panel_placeholder">
						<div class="panel">
							<div class="panel_header">
								<s:text name="global.Employees" />
							</div>
							<div class="panel_content">
								<s:if test="activeEmployees.size() > 0">
									<table class="table">
										<thead>
											<tr>
												<th>
													<s:text name="Employee.lastName" />
												</th>
												<th>
													<s:text name="Employee.firstName" />
												</th>
												<th>
													<s:text name="Employee.title" />
												</th>
												<th>
													<s:text name="Employee.classification" />
												</th>
												<th>
													<s:text name="EmployeeDashboard.JobRoles" />
												</th>
											</tr>
										</thead>
										<s:iterator value="activeEmployees" var="employee" status="stat">
											<tr>
												<td>
													<s:property value="#employee.lastName" />
												</td>
												<td>
													<s:property value="#employee.firstName" />
												</td>
												<td>
													<s:property value="#employee.title" />
												</td>
												<td>
													<s:property value="#employee.classification" />
												</td>
												<td>
													<s:iterator value="#employee.employeeRoles" var="er" status="line">
														<s:property value="#er.jobRole.name" /><s:if test="!#line.last">, </s:if>
													</s:iterator>
												</td>
											</tr>
										</s:iterator>
									</table>
								</s:if>
	
								<s:if test="canEditEmployees">
									<s:url action="ManageEmployees" var="manage_employees">
										<s:param name="id">
											${id}
										</s:param>
									</s:url>
									<a href="${manage_employees}" class="edit">
										<s:text name="EmployeeDashboard.EditEmployees" />
									</a>
								</s:if>
							</div>
						</div>
					</div>
					<br />
					<div class="panel_placeholder">
						<div class="panel">
							<div class="panel_header">
								<s:text name="EmployeeDashboard.JobRolesTitle" />
							</div>
							<div class="panel_content">
								<table class="table">
									<thead>
										<tr>
											<th>
												<s:text name="global.JobRole" />
											</th>
											<th>
												<s:text name="global.Active" />
											</th>
											<th>
												<s:text name="global.HSECompetencies" />
											</th>
										</tr>
									</thead>
									<s:iterator value="contractor.jobRoles" var="role" status="stat">
										<tr>
											<td>
												<s:property value="#role.name" />
											</td>
											<td class="center">
												<s:if test="active">
													<s:text name="YesNo.Yes" />
												</s:if>
												<s:else>
													<s:text name="YesNo.No" />
												</s:else>
											</td>
											<td>
												<s:iterator value="#role.jobCompetencies" var="jobCompetency" status="stat">
													<s:property value="#jobCompetency.competency.label" /><s:if test="!#stat.last">, </s:if>
												</s:iterator>
											</td>
										</tr>
									</s:iterator>
									<s:if test="contractor.jobRoles.size == 0">
										<tr>
											<td colspan="3">
												<s:text name="Filters.paging.NoResultsFound" />
											</td>
										</tr>
									</s:if>
								</table>
								<s:if test="#canEditJobRoles">
									<s:url action="ManageJobRoles" var="manage_job_roles">
										<s:param name="id">
											${id}
										</s:param>
									</s:url>
									<a href="${manage_job_roles}" class="edit">
										<s:text name="EmployeeDashboard.EditJobRoles" />
									</a>
								</s:if>
							</div>
						</div>
					</div>
				</td>
			</tr>
		</table>
	</div>
</body>