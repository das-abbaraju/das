<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<head>
	<title>
		<s:text name="EmployeeDetail.title.ProfileOf">
			<s:param>
				${employee.displayName}
			</s:param>
		</s:text>
	</title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" media="screen" href="css/dashboard.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=${version}"/>
	<style type="text/css">
		table tr td {
			vertical-align: top;
		}

		.skilled {
			background-color: #AFA;
			text-align: center;
		}

		.unskilled {
			background-color: #FAA;
			text-align: center;
		}

		.panel_placeholder {
			margin: 5px;
		}

		.panel_content th {
			padding: 0.5ex 5px;
			text-align: right;
			font-weight: bold;
		}

		.panel_content td {
			padding: 0.5ex 0;
		}

		#profile img {
			max-height: 150px;
			height: expression(this.height > 150 ? 150 : true);
		}

		.assessmentResults {
			display: none;
		}

		table.jobSiteTasks {
			font-size: 12px;
			line-height: 12px;
			border-collapse: collapse;
			border: 1px solid #B3B3B3;
			display: none;
			margin-top: 5px;
			margin-bottom: 5px;
		}

		table.jobSiteTasks th {
			background-color: #ECECEC;
			color: #A84D10;
			text-align: center;
			font-weight: bold;
		}

		table.jobSiteTasks td {
			text-align: left;
		}

		table.jobSiteTasks th, table.jobSiteTasks td {
			border: 1px solid #B3B3B3;
			padding: 4px;
		}

		.alert {
			width: auto !important;
		}
	</style>
	<s:include value="../jquery.jsp"/>
</head>
<body>
<s:include value="../actionMessages.jsp"/>
<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
<table width="100%">
<tr>
	<td colspan="3">
		<table style="margin: 0px auto;">
			<tr>
				<s:if test="employee.photo.length() > 0">
					<td style="vertical-align: middle; padding: 0px 5px;">
						<img id="cropPhoto"
						     src="EmployeePhotoStream.action?employeeID=${employee.id}"
						     alt="${employee.displayName}"
						     title="<s:text name="EmployeeDetail.title.ProfilePhotoFor"><s:param>${employee.displayName}</s:param></s:text>"/>
					</td>
				</s:if>
				<td style="vertical-align: middle; padding: 0px 5px;">
					<h2>${employee.displayName}</h2>
					<s:if test="employee.title.length > 0">
						<s:text name="Employee.title"/>: ${employee.title}<br/>
					</s:if>
					<s:if test="(permissions.admin && permissions.hasPermission('ManageEmployees')) || permissions.accountId == employee.account.id">
						<s:url var="manage_employee" action="ManageEmployees">
							<s:param name="employee">
								${employee.id}
							</s:param>
						</s:url>
						<a href="${manage_employee}" class="edit"><s:text
								name="EmployeeDetail.link.EditEmployee"/></a><br/>
					</s:if>
					<s:if test="canViewContractor">
						<s:url var="contractor_view" action="ContractorView">
							<s:param name="id">
								${employee.account.id}
							</s:param>
						</s:url>
						<a href="${contractor_view}">${employee.account.name}</a><br/>
					</s:if>
					<s:elseif test="canViewOperator">
						<s:url var="facilities_edit" action="FacilitiesEdit">
							<s:param name="id">
								${employee.account.id}
							</s:param>
						</s:url>
						<a href="${facilities_edit}">${employee.account.name}</a><br/>
					</s:elseif>
					<s:else>
						${employee.account.name}
					</s:else>
				</td>
				<s:if test="!employee.account.logoFile.empty()">
					<td style="vertical-align: middle; padding: 0px 5px;">
						<img src="ContractorLogo.action?id=${employee.account.id}" alt="Logo"/>
					</td>
				</s:if>
			</tr>
		</table>
	</td>
</tr>
<tr>
<td>
	<s:if test="worksAt.size() > 0">
		<div class="panel_placeholder">
			<div class="panel">
				<div class="panel_header"><s:text name="EmployeeDetail.header.WorksAt"/></div>
				<div class="panel_content">
					<table>
						<s:if test="employee.location.length() > 0">
							<tr>
								<th><s:text name="Employee.location"/>:</th>
								<td>${employee.location}</td>
							</tr>
						</s:if>
						<s:iterator value="worksAt" id="sites" status="stat">
							<tr>
								<th>
										${sites.operator.name}
									<s:if test="#sites.jobSite != null">(<s:text name="global.OQ"/>):</s:if>
									<s:else>(<s:text name="global.HSE"/>):</s:else>
								</th>
								<td>
									<s:if test="#sites.jobSite != null">
										${sites.jobSite.name}
										<br/>
									</s:if>
									<s:text name="ManageEmployees.label.Since"/>:
										${sites.effectiveDate}
									<s:if test="!#sites.jobSite.current">
										<s:text name="EmployeeDetail.label.Finished"/>:
										${sites.jobSite.projectStop}
									</s:if>
									<s:if test="#sites.orientationDate!=null">
														<span style="padding-left: 8px;">
															<s:text name="ManageEmployees.label.Orientation"/>:
															<s:date name="#sites.orientationDate"/>
														</span>
										<br/>
									</s:if>
									<s:if test="employee.account.requiresOQ && permissions.requiresOQ && #sites.jobSite != null">
										<a href="javascript:;" class="preview view-site-tasks" data-site="${sites.id}">
											<s:text name="EmployeeDetail.link.ViewSiteTask"/>
										</a>
									</s:if>
									<s:if test="#sites.jobSite != null">
										<table class="jobSiteTasks" id="jst_${sites.id}">
											<thead>
											<tr>
												<th colspan="2"><s:text name="ManageEmployees.label.Task"/></th>
												<th><s:text name="EmployeeDetail.header.Qualified"/></th>
											</tr>
											</thead>
											<tbody>
											<s:iterator value="tasks.get(#sites.jobSite)" id="task">
												<tr>
													<td style="text-align: center; font-weight: bold;">
															${task.label}
													</td>
													<td>${task.name}</td>
													<td class="center">
														<s:iterator value="jobTasks" id="qual">
															<s:if test="#qual.task == #task && #qual.qualified">
																<img src="images/okCheck.gif"
																     alt="<s:text name="EmployeeDetail.header.Qualified" />"/>
															</s:if>
															<s:elseif test="#qual.task == #task && !#qual.qualified">
																<img src="images/notOkCheck.gif"
																     alt="<s:text name="EmployeeDetail.help.NotQualified" />"/>
															</s:elseif>
														</s:iterator>
													</td>
												</tr>
											</s:iterator>
											</tbody>
										</table>
									</s:if>
								</td>
							</tr>
						</s:iterator>
					</table>
				</div>
			</div>
		</div>
	</s:if>
	<s:if test="employee.employeeRoles.size > 0">
		<div class="panel_placeholder">
			<div class="panel">
				<div class="panel_header"><s:text name="global.HSECompetencies"/></div>
				<div class="panel_content">
					<table>
						<tr>
							<th><s:text name="ManageEmployees.header.JobRoles"/>:</th>
							<td>
								<s:iterator value="employee.employeeRoles" status="stat" var="employee_role">
									${employee_role.jobRole.name}<s:if test="!#stat.last">,</s:if>
								</s:iterator>
							</td>
						</tr>
						<s:if test="missingCompetencies.keySet().size > 0">
							<s:iterator value="missingCompetencies.keySet()" var="key">
								<tr>
									<td colspan="2">
										<div class="alert">
											<s:text name="EmployeeDetail.message.MissingCompetencies">
												<s:param value="%{#key.name}"/>
											</s:text>:
											<s:iterator value="missingCompetencies.get(#key)" status="stat"
											            var="missing_competency">
												${missing_competency.label}
												<img src="images/help.gif" alt="${missing_competency.label}"
												     title="${missing_competency.category}"/>:
												${missing_competency.description}<s:if test="!#stat.last">,</s:if>
											</s:iterator>
										</div>
									</td>
								</tr>
							</s:iterator>
						</s:if>
						<s:if test="skilledCompetencies.size > 0">
							<tr>
								<th><s:text name="EmployeeDetail.message.SkilledCompetencies"/>:</th>
								<td>
									<s:iterator value="skilledCompetencies" status="stat" var="skilled_competency">
										${skilled_competency.label}
										<img src="images/help.gif" alt="${skilled_competency.label}"
										     title="${skilled_competency.category}"/>:
										${skilled_competency.description}<s:if test="!#stat.last"><br/></s:if>
									</s:iterator>
								</td>
							</tr>
						</s:if>
					</table>
				</div>
			</div>
		</div>
	</s:if>
	<s:elseif test="employee.account.requiresCompetencyReview">
		<div class="panel_placeholder">
			<div class="panel">
				<div class="panel_header"><s:text name="ManageEmployees.header.JobRoles"/></div>
				<div class="panel_content">
					<s:text name="EmployeeDetail.message.NoJobRoles">
						<s:param value="%{employee.displayName}"/>
					</s:text>
				</div>
			</div>
		</div>
	</s:elseif>
	<s:include value="../notes/account_notes_embed.jsp"/>
</td>
<td width="20px;">&nbsp;</td>
<td>
	<s:if test="employee.phone.length() > 0 || employee.email.length() > 0 || employee.location.length() > 0 || employee.twicExpiration != null">
		<div class="panel_placeholder">
			<div class="panel">
				<div class="panel_header"><s:text name="EmployeeDetail.header.Information"/></div>
				<div class="panel_content">
					<table>
						<s:if test="employee.phone.length() > 0">
							<tr>
								<th><s:text name="Employee.phone"/>:</th>
								<td>${employee.phone}</td>
							</tr>
						</s:if>
						<s:if test="employee.email.length() > 0">
							<tr>
								<th><s:text name="Employee.email"/>:</th>
								<td>${employee.email}</td>
							</tr>
						</s:if>
						<s:if test="employee.twicExpiration != null">
							<tr>
								<th><s:text name="Employee.twicExpiration"/>:</th>
								<td><s:date name="employee.twicExpiration"/></td>
							</tr>
						</s:if>
					</table>
				</div>
			</div>
		</div>
	</s:if>
	<s:if test="qualification.keySet().size > 0">
		<s:set name="qualCount" value="0"/>
		<table class="report">
			<thead>
			<tr>
				<th colspan="2"><s:text name="EmployeeDetail.header.AllJobTasks"/></th>
				<th><s:text name="EmployeeDetail.header.Qualified"/></th>
			</tr>
			</thead>
			<tbody>
			<s:iterator value="qualification.keySet()" var="task">
				<tr>
					<td class="center" style="font-weight: bold;">${task.label}</td>
					<td>${task.name}</td>
					<td class="center">
						<s:if test="qualification.get(#task).size > 0">
							<a href="javascript:;" class="toggle-single-qualification" data-task="${task.id}">
								<img src="images/okCheck.gif" alt="<s:text name="EmployeeDetail.header.Qualified" />"/>
							</a>
						</s:if>
						<s:else>
							<img src="images/notOkCheck.gif" alt="<s:text name="EmployeeDetail.help.NotQualified" />"/>
						</s:else>
					</td>
				</tr>
				<s:if test="qualification.get(#task).size > 0">
					<s:set name="qualCount" value="#qualCount + 1"/>
					<tr id="jt_${task.id}" class="assessmentResults">
						<td colspan="3" style="padding: 10px;">
							<table class="report">
								<thead>
								<tr>
									<th><s:text name="global.AssessmentCenter"/></th>
									<th><s:text name="AssessmentTest.qualificationMethod"/></th>
									<th><s:text name="AssessmentTest.qualificationType"/></th>
									<th><s:text name="AssessmentTest.effectiveDate"/></th>
									<th><s:text name="AssessmentTest.expirationDate"/></th>
									<th></th>
								</tr>
								</thead>
								<tbody>
								<s:iterator value="qualification.get(#task)" var="results">
									<tr>
										<td>${results.assessmentTest.assessmentCenter.name}</td>
										<td>${results.assessmentTest.qualificationMethod}</td>
										<td>${results.assessmentTest.qualificationType}</td>
										<td><s:date name="#results.assessmentTest.effectiveDate"/></td>
										<td><s:date name="#results.assessmentTest.expirationDate"/></td>
										<td><img src="images/help.gif"
										         alt="<s:text name="AssessmentTest.description" />"
										         title="<s:property value="#results.assessmentTest.description" />"
										         style="cursor: help"/>
									</tr>
								</s:iterator>
								</tbody>
							</table>
						</td>
					</tr>
				</s:if>
			</s:iterator>
			</tbody>
		</table>
		<s:if test="#qualCount > 0">
			<a href="javascript:;" id="toggle_qualifications">
				<s:text name="EmployeeDetail.link.ToggleAllQualifications"/>
			</a>
		</s:if>
	</s:if>
	<s:if test="nccerData.size > 0">
		<div class="panel_placeholder">
			<div class="panel">
				<div class="panel_header">
					<s:text name="EmployeeDetail.label.NCCERAssessmentData"/>
				</div>
				<div class="panel_content">
					<s:iterator value="nccerData" status="stat" var="nccer_data">
						${nccer_data.assessmentTest.qualificationType} -
						${nccer_data.assessmentTest.qualificationMethod}
						<a href="javascript:;" rel="#cluetip_${nccer_data.id}" class="cluetip help"
						   title="${nccer_data.assessmentTest.qualificationType} - ${nccer_data.assessmentTest.qualificationMethod}"></a>

						<div id="cluetip_${nccer_data.id}">
								${nccer_data.assessmentTest.description}<br/>
							<s:text name="AssessmentTest.effectiveDate"/>: <s:date name="effectiveDate"/><br/>
							<s:text name="AssessmentTest.expirationDate"/>: <s:date name="expirationDate"/>
						</div>
						<s:if test="!#stat.last"><br/></s:if>
					</s:iterator>
				</div>
			</div>
		</div>
	</s:if>
</td>
</tr>
</table>
</div>
</body>