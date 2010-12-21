<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
	<title>Profile of <s:property value="employee.firstName" /> <s:property value="employee.lastName" /></title>
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
	</style>
	<s:include value="../jquery.jsp"/>
	<script type="text/javascript">
		function showHideResults() {
			if ($('.assessmentResults').is(':visible'))
				$('.assessmentResults').hide();
			else
				$('.assessmentResults').show();
		}
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
										alt="<s:property value="employee.displayName"/>" title="Profile Photo for <s:property value="employee.displayName"/>"/>
								</td>
							</s:if>
							<td style="vertical-align: middle; padding: 0px 5px;">
								<h2><s:property value="employee.displayName" /></h2>
								<s:property value="employee.title" /><br />
								<s:if test="employee.account.contractor && (permissions.admin || (permissions.operatorCorporate && canViewContractor) || permissions.accountId == employee.account.id)">
									<a href="ContractorView.action?id=<s:property value="employee.account.id" />"><s:property value="employee.account.name" /></a><br />
								</s:if>
								<s:elseif test="employee.account.operatorCorporate && (permissions.admin || permissions.accountId == employee.account.id)">
									<a href="FacilitiesEdit.action?id=<s:property value="employee.account.id" />"><s:property value="employee.account.name" /></a><br />
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
								<div class="panel_header">
									Works At
								</div>
								<div class="panel_content">
									<table>
										<s:if test="employee.location.length() > 0">
											<tr>
												<th>Location:</th>
												<td><s:property value="employee.location" /></td>
											</tr>
										</s:if>
										<s:iterator value="worksAt" id="sites" status="stat">
											<tr>
												<th>
													<s:property value="#sites.operator.name" /><s:if test="#sites.jobSite.name != null" >:
												</th>
												<td>
													<s:property value="#sites.jobSite.name" /></s:if><br />
													<span style="font-size: 12px;" >
														Since: <s:property value="#sites.effectiveDate" />
														<s:if test="!#sites.jobSite.current">
															Finished: <s:property value="#sites.jobSite.projectStop" />
														</s:if>
														<s:if test="#sites.orientationDate!=null" >
															<span style="padding-left: 8px;" >Orientation: <s:property value="#sites.orientationDate" /></span><br />
														</s:if>
														<s:if test="employee.account.requiresOQ && permissions.requiresOQ">
															<a href="#" onclick="$('#jst_<s:property value="#sites.id" />').toggle('slow'); return false;" class="preview">View/Hide Site Tasks</a>
														</s:if>
													</span>
													<table class="jobSiteTasks" id="jst_<s:property value="#sites.id" />">
														<thead>
															<tr>
																<th colspan="2">Task</th>
																<th>Qualified</th>
															</tr>
														</thead>
														<tbody>
															<s:iterator value="tasks.get(#sites.jobSite)" id="task">
																<tr>
																	<td style="text-align: center; font-weight: bold;"><s:property value="#task.label" /></td>
																	<td><s:property value="#task.name" /></td>
																	<td style="text-align: center;">
																		<s:iterator value="jobTasks" id="qual">
																			<s:if test="#qual.task == #task && #qual.qualified">
																				<img src="images/okCheck.gif" alt="Qualified" />
																			</s:if>
																			<s:elseif test="#qual.task == #task && !#qual.qualified">
																				<img src="images/notOkCheck.gif" alt="Not Qualified" />
																			</s:elseif>
																		</s:iterator>
																	</td>
																</tr>
															</s:iterator>
														</tbody>
													</table>
												</td>
											</tr>
										</s:iterator>
									</table>
								</div>
							</div>
						</div>
					</s:if>
					<s:if test="employee.employeeRoles.size() > 0">
						<table class="report">
							<thead>
								<tr>
									<th>HSE Competencies</th>
									<s:iterator value="employee.employeeRoles" id="role" status="stat">
										<th><s:property value="#role.jobRole.name" /></th>
									</s:iterator>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="opComps" id="opComp" status="stat">
									<tr>
										<td><s:property value="#opComp.label" /></td>
										<s:iterator value="employee.employeeRoles" id="role" status="stat">
											<td class="center">
												<s:iterator value="getCompetenciesByRole(#opComp, #role.jobRole)" id="ec">
													<s:if test="#ec">
														<img src="images/okCheck.gif" alt="Skilled" />
													</s:if>
													<s:elseif test="!#ec">
														<img src="images/notOkCheck.gif" alt="Unskilled" />
													</s:elseif>
												</s:iterator>
											</td>
										</s:iterator>
									</tr>
								</s:iterator>
							</tbody>
						</table>
					</s:if>
					<s:elseif test="employee.account.requiresCompetencyReview">
						<div class="panel_placeholder">
							<div class="panel">
								<div class="panel_header">
									Job Roles
								</div>
								<div class="panel_content">
									<s:property value="employee.displayName" /> has no job roles.
								</div>
							</div>
						</div>
					</s:elseif>
				</td>
				<td width="20px;">&nbsp;</td>
				<td>
					<s:if test="employee.phone.length() > 0 || employee.email.length() > 0 || employee.location.length() > 0 || employee.twicExpiration != null">
						<div class="panel_placeholder">
							<div class="panel">
								<div class="panel_header">
									Information
								</div>
								<div class="panel_content">
									<table>
										<s:if test="employee.phone.length() > 0">
											<tr>
												<th>Phone:</th>
												<td><s:property value="employee.phone" /></td>
											</tr>
										</s:if>
										<s:if test="employee.email.length() > 0">
											<tr>
												<th>Email:</th>
												<td><s:property value="employee.email" /></td>
											</tr>
										</s:if>
										<s:if test="employee.twicExpiration != null">
											<tr>
												<th>TWIC Card Expiration:</th>
												<td><s:date name="employee.twicExpiration" format="MM/dd/yyyy" /></td>
											</tr>
										</s:if>
									</table>
									<s:if test="permissions.admin || permissions.accountID == employee.account.id">
										<pics:permission perm="ManageEmployees">
											<a href="ManageEmployees.action?employee.id=<s:property value="employee.id" />" class="edit">Edit Profile</a>
										</pics:permission>
									</s:if> 
								</div>
							</div>
						</div>
					</s:if>
					<s:if test="employee.employeeQualifications.size() > 0">
						<s:set name="qualCount" value="0" />
						<table class="report">
							<thead>
								<tr>
									<th colspan="2">All Job Tasks</th>
									<th>Qualified</th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="jobTasks" id="quals">
									<tr>
										<td class="center" style="font-weight: bold;"><s:property value="#quals.task.label" /></td>
										<td><s:property value="#quals.task.name" /></td>
										<td class="center">
											<s:if test="#quals.qualified && qualification.get(#quals).size > 0">
												<a href="#" onclick="$('#jt_<s:property value="#quals.id" />').toggle(); return false;">
													<img src="images/okCheck.gif" alt="Qualified" />
												</a>
											</s:if>
											<s:else><img src="images/notOkCheck.gif" alt="Not Qualified" /></s:else>
										</td>
									</tr>
									<s:if test="#quals.qualified">
										<s:set name="qualCount" value="#qualCount + 1" />
										<tr id="jt_<s:property value="#quals.id" />" class="assessmentResults">
											<td colspan="3" style="padding: 10px;">
												<table class="report">
													<thead>
														<tr>
															<th>Center</th>
															<th>Method</th>
															<th>Type</th>
															<th>Effective</th>
															<th>Expiration</th>
															<th></th>
														</tr>
													</thead>
													<tbody>
														<s:iterator value="qualification.get(#quals)" id="results">
															<tr>
																<td><s:property value="#results.assessmentTest.assessmentCenter.name" /></td>
																<td><s:property value="#results.assessmentTest.qualificationMethod" /></td>
																<td><s:property value="#results.assessmentTest.qualificationType" /></td>
																<td><s:date name="#results.assessmentTest.effectiveDate" format="M/d/yyyy" /></td>
																<td><s:date name="#results.assessmentTest.expirationDate" format="M/d/yyyy" /></td>
																<td><img src="images/help.gif" alt="Description" title="<s:property value="#results.assessmentTest.description" />" style="cursor: help" />
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
							<a href="#" onclick="showHideResults(); return false;">See/Hide all qualifications</a>
						</s:if>
					</s:if>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>
