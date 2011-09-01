<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="EmployeeDetail.title.ProfileOf"><s:param value="%{employee.displayName}" /></s:text></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/dashboard.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
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
<script type="text/javascript">
	$(function() {
		$('.cluetip').cluetip({
			closeText : "<img src='images/cross.png' width='16' height='16'>",
			arrows : true,
			cluetipClass : 'jtip',
			local : true,
			clickThrough : false
		});
		
		$('#profile').delegate('.viewSiteTasks', 'click', function(e) {
			e.preventDefault();
			var id = $(this).attr('id').split('_')[1];
			$('#jst_' + id).toggle('slow');
		});
		
		$('#profile').delegate('#toggleAllQualifications', 'click', function(e) {
			e.preventDefault();
			
			if ($('.assessmentResults').is(':visible'))
				$('.assessmentResults').hide();
			else
				$('.assessmentResults').show();
		});
	});
</script>
</head>
<body>
	<s:include value="../actionMessages.jsp"/>
	<div id="profile">
		<table width="100%">
			<tr>
				<td colspan="3">
					<table style="margin: 0px auto;">
						<tr>
							<s:if test="employee.photo.length() > 0">
								<td style="vertical-align: middle; padding: 0px 5px;">
									<img id="cropPhoto" src="EmployeePhotoStream.action?employeeID=<s:property value="employee.id"/>"
										alt="<s:property value="employee.displayName"/>" title="<s:text name="EmployeeDetail.title.ProfilePhotoFor"><s:param value="%{employee.displayName}" /></s:text>"/>
								</td>
							</s:if>
							<td style="vertical-align: middle; padding: 0px 5px;">
								<h2><s:property value="employee.displayName" /></h2>
								<s:if test="employee.title.length > 0">
									<s:text name="Employee.title" />: <s:property value="employee.title" /><br />
								</s:if>
								<s:if test="(permissions.admin && permissions.hasPermission('ManageEmployees')) || permissions.accountId == employee.account.id">
									<a href="ManageEmployees.action?employee=<s:property value="employee.id" />" class="edit"><s:text name="EmployeeDetail.link.EditEmployee" /></a><br />
								</s:if>
								<s:if test="employee.account.contractor && (permissions.admin || (permissions.operatorCorporate && canViewContractor) || permissions.accountId == employee.account.id)">
									<a href="ContractorView.action?id=<s:property value="employee.account.id" />"><s:property value="employee.account.name" /></a><br />
								</s:if>
								<s:elseif test="employee.account.operatorCorporate && (permissions.admin || permissions.accountId == employee.account.id)">
									<a href="FacilitiesEdit.action?operator=<s:property value="employee.account.id" />"><s:property value="employee.account.name" /></a><br />
								</s:elseif>
								<s:else>
									<s:property value="employee.account.name" />
								</s:else>
							</td>
							<s:if test="!employee.account.logoFile.empty()">
								<td style="vertical-align: middle; padding: 0px 5px;">
									<img src="ContractorLogo.action?id=<s:property value="employee.account.id"/>"
										alt="Logo" />
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
								<div class="panel_header"><s:text name="EmployeeDetail.header.WorksAt" /></div>
								<div class="panel_content">
									<table>
										<s:if test="employee.location.length() > 0">
											<tr>
												<th><s:text name="Employee.location" />:</th>
												<td><s:property value="employee.location" /></td>
											</tr>
										</s:if>
										<s:iterator value="employee.employeeSites" id="sites" status="stat">
											<tr>
												<th>
													<s:property value="#sites.operator.name" />
													<s:if test="#sites.jobSite != null">(<s:text name="global.OQ" />):</s:if>
													<s:else>(<s:text name="global.HSE" />):</s:else>
												</th>
												<td>
													<s:if test="#sites.jobSite != null"><s:property value="#sites.jobSite.name" /><br /></s:if>
													<s:text name="ManageEmployees.label.Since" />: <s:property value="#sites.effectiveDate" />
													<s:if test="!#sites.jobSite.current">
														<s:text name="EmployeeDetail.label.Finished" />: <s:property value="#sites.jobSite.projectStop" />
													</s:if>
													<s:if test="#sites.orientationDate!=null" >
														<span style="padding-left: 8px;" ><s:text name="ManageEmployees.label.Orientation" />: <s:date name="#sites.orientationDate" /></span><br />
													</s:if>
													<s:if test="employee.account.requiresOQ && permissions.requiresOQ && #sites.jobSite != null">
														<a href="#" id="viewJst_<s:property value="#sites.id" />" class="preview viewSiteTasks"><s:text name="EmployeeDetail.link.ViewSiteTask" /></a>
													</s:if>
													<s:if test="#sites.jobSite != null">
														<table class="jobSiteTasks" id="jst_<s:property value="#sites.id" />">
															<thead>
																<tr>
																	<th colspan="2"><s:text name="ManageEmployees.label.Task" /></th>
																	<th><s:text name="EmployeeDetail.header.Qualified" /></th>
																</tr>
															</thead>
															<tbody>
																<s:iterator value="tasks.get(#sites.jobSite)" id="task">
																	<tr>
																		<td style="text-align: center; font-weight: bold;"><s:property value="#task.label" /></td>
																		<td><s:property value="#task.name" /></td>
																		<td class="center">
																			<s:iterator value="jobTasks" id="qual">
																				<s:if test="#qual.task == #task && #qual.qualified">
																					<img src="images/okCheck.gif" alt="<s:text name="EmployeeDetail.header.Qualified" />" />
																				</s:if>
																				<s:elseif test="#qual.task == #task && !#qual.qualified">
																					<img src="images/notOkCheck.gif" alt="<s:text name="EmployeeDetail.help.NotQualified" />" />
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
								<div class="panel_header"><s:text name="global.HSECompetencies" /></div>
								<div class="panel_content">
									<table>
										<tr>
											<th><s:text name="ManageEmployees.header.JobRoles" />:</th>
											<td>
												<s:iterator value="employee.employeeRoles" status="stat">
													<s:property value="jobRole.name" /><s:if test="!#stat.last">,</s:if>
												</s:iterator>
											</td>
										</tr>
										<s:if test="missingCompetencies.keySet().size > 0">
											<s:iterator value="missingCompetencies.keySet()" var="key">
												<tr>
													<td colspan="2">
														<div class="alert"><s:text name="EmployeeDetail.message.MissingCompetencies"><s:param value="%{#key.name}" /></s:text>:
															<s:iterator value="missingCompetencies.get(#key)" status="stat">
																<s:property value="label" />
																<img src="images/help.gif" alt="<s:property value="label" />" title="<s:property value="category" />: <s:property value="description" />" /><s:if test="!#stat.last">,</s:if>
															</s:iterator>
														</div>
													</td>
												</tr>
											</s:iterator>
										</s:if>
										<s:if test="skilledCompetencies.size > 0">
											<tr>
												<th><s:text name="EmployeeDetail.message.SkilledCompetencies" />:</th>
												<td>
													<s:iterator value="skilledCompetencies" status="stat">
														<s:property value="label" />
														<img src="images/help.gif" alt="<s:property value="label" />" title="<s:property value="category" />: <s:property value="description" />" />
														<s:if test="!#stat.last"><br /></s:if>
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
								<div class="panel_header"><s:text name="ManageEmployees.header.JobRoles" /></div>
								<div class="panel_content">
									<s:text name="EmployeeDetail.message.NoJobRoles"><s:param value="%{employee.displayName}" /></s:text>
								</div>
							</div>
						</div>
					</s:elseif>
					<s:include value="../notes/account_notes_embed.jsp" />
				</td>
				<td width="20px;">&nbsp;</td>
				<td>
					<s:if test="employee.phone.length() > 0 || employee.email.length() > 0 || employee.location.length() > 0 || employee.twicExpiration != null">
						<div class="panel_placeholder">
							<div class="panel">
								<div class="panel_header"><s:text name="EmployeeDetail.header.Information" /></div>
								<div class="panel_content">
									<table>
										<s:if test="employee.phone.length() > 0">
											<tr>
												<th><s:text name="Employee.phone" />:</th>
												<td><s:property value="employee.phone" /></td>
											</tr>
										</s:if>
										<s:if test="employee.email.length() > 0">
											<tr>
												<th><s:text name="Employee.email" />:</th>
												<td><s:property value="employee.email" /></td>
											</tr>
										</s:if>
										<s:if test="employee.twicExpiration != null">
											<tr>
												<th><s:text name="Employee.twicExpiration" />:</th>
												<td><s:date name="employee.twicExpiration" /></td>
											</tr>
										</s:if>
									</table>
								</div>
							</div>
						</div>
					</s:if>
					<s:if test="qualification.keySet().size > 0">
						<s:set name="qualCount" value="0" />
						<table class="report">
							<thead>
								<tr>
									<th colspan="2"><s:text name="EmployeeDetail.header.AllJobTasks" /></th>
									<th><s:text name="EmployeeDetail.header.Qualified" /></th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="qualification.keySet()" id="task">
									<tr>
										<td class="center" style="font-weight: bold;"><s:property value="label" /></td>
										<td><s:property value="name" /></td>
										<td class="center">
											<s:if test="qualification.get(#task).size > 0">
												<a href="#" onclick="$('#jt_<s:property value="#task.id" />').toggle(); return false;">
													<img src="images/okCheck.gif" alt="<s:text name="EmployeeDetail.header.Qualified" />" />
												</a>
											</s:if>
											<s:else><img src="images/notOkCheck.gif" alt="<s:text name="EmployeeDetail.help.NotQualified" />" /></s:else>
										</td>
									</tr>
									<s:if test="qualification.get(#task).size > 0">
										<s:set name="qualCount" value="#qualCount + 1" />
										<tr id="jt_<s:property value="#task.id" />" class="assessmentResults">
											<td colspan="3" style="padding: 10px;">
												<table class="report">
													<thead>
														<tr>
															<th><s:text name="global.AssessmentCenter" /></th>
															<th><s:text name="AssessmentTest.qualificationMethod" /></th>
															<th><s:text name="AssessmentTest.qualificationType" /></th>
															<th><s:text name="AssessmentTest.effectiveDate" /></th>
															<th><s:text name="AssessmentTest.expirationDate" /></th>
															<th></th>
														</tr>
													</thead>
													<tbody>
														<s:iterator value="qualification.get(#task)" id="results">
															<tr>
																<td><s:property value="#results.assessmentTest.assessmentCenter.name" /></td>
																<td><s:property value="#results.assessmentTest.qualificationMethod" /></td>
																<td><s:property value="#results.assessmentTest.qualificationType" /></td>
																<td><s:date name="#results.assessmentTest.effectiveDate" /></td>
																<td><s:date name="#results.assessmentTest.expirationDate" /></td>
																<td><img src="images/help.gif" alt="<s:text name="AssessmentTest.description" />" title="<s:property value="#results.assessmentTest.description" />" style="cursor: help" />
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
							<a href="#" id="toggleAllQualifications"><s:text name="EmployeeDetail.link.ToggleAllQualifications" /></a>
						</s:if>
					</s:if>
					<s:if test="nccerData.size > 0">
						<div class="panel_placeholder">
							<div class="panel">
								<div class="panel_header">
									<s:text name="%{scope}.label.NCCERAssessmentData" />
								</div>
								<div class="panel_content">
									<s:iterator value="nccerData" status="stat">
										<s:property value="assessmentTest.qualificationType" /> -
										<s:property value="assessmentTest.qualificationMethod" /> 
										<a href="#" rel="#cluetip_<s:property value="id" />" class="cluetip help" 
											title="<s:property value="assessmentTest.qualificationType" /> - <s:property value="assessmentTest.qualificationMethod" />"></a>
										<div id="cluetip_<s:property value="id" />">
											<s:property value="assessmentTest.description" /><br />
											<s:text name="AssessmentTest.effectiveDate" />: <s:date name="effectiveDate" /><br />
											<s:text name="AssessmentTest.expirationDate" />: <s:date name="expirationDate" />
										</div>
										<s:if test="!#stat.last"><br /></s:if>
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
</html>
