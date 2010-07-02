<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
	<title>Employee Details</title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
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
		
		.cell {
			border: 1px solid black;
		}
		
		.cell, .report {
			padding: 10px;
			margin: 10px;
		}
		
		.cell td {
			padding: 0px 5px;
		}
		
		.cell th {
			padding: 0px 5px;
			text-align: right;
			font-weight: bold;
		}
		
		#profile img {
			max-height: 150px;
			height: expression(this.height > 150 ? 150 : true);
		}
	</style>
	<s:include value="../jquery.jsp"/>
</head>
<body>
	<s:include value="../actionMessages.jsp"/>
	<div id="profile">
		<table>
			<tr>
				<td colspan="2">
					<table style="margin: 0px auto;">
						<tr>
							<td style="vertical-align: middle; padding: 0px 5px;">
								<s:if test="employee.photo.length() > 0">
								<img id="cropPhoto" src="EmployeePhotoStream.action?employeeID=<s:property value="employee.id"/>"
									alt="<s:property value="employee.displayName"/>" title="Profile Photo for <s:property value="employee.displayName"/>"/>
								</s:if>
							</td>
							<td style="vertical-align: middle; padding: 0px 5px;">
								<h2><s:property value="employee.displayName" /></h2>
								<s:property value="employee.title" /><br />
								<a href="ContractorView.action?id=<s:property value="employee.account.id" />"><s:property value="employee.account.name" /></a><br />
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
					<s:if test="employee.employeeSites.size() > 0 || employee.employeeRoles.size() > 0">
						<div class="cell">
							<table>
								<s:if test="employee.employeeSites.size() > 0">
									<tr>
										<th>Works at:</th>
										<td>
											<s:iterator value="employee.employeeSites" id="sites" status="stat">
												<s:if test="#sites.current">
													<s:if test="permissions.operatorCorporate">
														<s:if test="canOperatorViewSite(permissions.accountId)">
															<s:property value="#sites.operator.name" /><s:if test="#sites.jobSite.name != null" >: <s:property value="#sites.jobSite.name" /></s:if><br />
															<span style="font-size: 12px; padding-left: 5px;" >
																Since: <s:property value="#sites.effectiveDate" />
																<s:if test="#sites.orientationDate!=null" >
																	<span style="padding-left: 8px;" >Orientation: <s:property value="#sites.orientationDate" /></span>
																</s:if>
																<br />
															</span>
														</s:if>
													</s:if>
													<s:else>
														<s:property value="#sites.operator.name" /><s:if test="#sites.jobSite.name != null" >: <s:property value="#sites.jobSite.name" /></s:if><br />
														<span style="font-size: 12px; padding-left: 5px;" >
															Since: <s:property value="#sites.effectiveDate" />
															<s:if test="#sites.orientationDate!=null" >
																<span style="padding-left: 8px;" >Orientation: <s:property value="#sites.orientationDate" /></span>
															</s:if>
															<br />
														</span>
													
													</s:else>													
												</s:if>
											</s:iterator>
										</td>
									</tr>
								</s:if>
								<s:if test="employee.employeeRoles.size() > 0">
									<tr>
										<th>Job Role<s:if test="employee.employeeRoles.size() > 0">s</s:if>:</th>
										<td>
											<s:iterator value="employee.employeeRoles" id="role" status="stat">
												<s:property value="jobRole.name" /><s:if test="!#stat.last">, </s:if>
											</s:iterator>
										</td>
									</tr>
								</s:if>
							</table>
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
					<s:elseif test="permissions.requiresCompetencyReview">
						<div class="cell"><s:property value="employee.displayName" /> has no job roles.</div>
					</s:elseif>
				</td>
				<td>
					<s:if test="employee.phone.length() > 0 || employee.email.length() > 0 || employee.location.length() > 0 || employee.twicExpiration != null">
						<div class="cell">
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
								<s:if test="employee.location.length() > 0">
									<tr>
										<th>Location:</th>
										<td><s:property value="employee.location" /></td>
									</tr>
								</s:if>
								<s:if test="employee.twicExpiration != null">
									<tr>
										<th>TWIC Card Expiration:</th>
										<td><s:date name="employee.twicExpiration" format="MM/dd/yyyy" /></td>
									</tr>
								</s:if>
							</table>
						</div>
					</s:if>
					<s:if test="employee.employeeQualifications.size() > 0">
						<table class="report">
							<thead>
								<tr>
									<th>Job Tasks</th>
									<th>Qualified</th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="jobTasks" id="quals">
									<tr>
										<td><s:property value="#quals.task.label" />: <s:property value="#quals.task.name" /></td>
										<td class="center">
											<s:if test="#quals.qualified"><img src="images/okCheck.gif" alt="Qualified" /></s:if>
											<s:else><img src="images/notOkCheck.gif" alt="Not Qualified" /></s:else>
										</td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
					</s:if>
					<s:if test="employee.assessmentResults.size() > 0">
						<table class="report">
							<thead>
								<tr>
									<th>Assessment Results</th>
									<th>Effective</th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="employee.assessmentResults" id="results">
									<tr>
										<td><s:property value="#results.assessmentTest.assessmentCenter.name" />: <s:property value="#results.assessmentTest.qualificationType" /> - <s:property value="#results.assessmentTest.qualificationMethod" /></td>
										<td><s:date name="#results.effectiveDate" format="MM/dd/yyyy" /></td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
						<pics:permission perm="DevelopmentEnvironment">
							<a href="EmployeeAssessmentResults.action?id=<s:property value="employee.account.id" />&employeeID=<s:property value="employee.id" />"
								style="margin-left: 10px;">View All Assessment Results</a>
						</pics:permission>
					</s:if>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>
