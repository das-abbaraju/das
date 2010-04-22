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
		
		.cell, .report {
			padding: 10px;
			margin: 10px;
			border: 1px solid black;
		}
		
		.cell td {
			padding: 0px 5px;
		}
		
		.cell th {
			padding: 0px 5px;
			text-align: right;
			font-weight: bold;
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
								<img src="images/employee/<s:property value="employee.id % 5" />.jpg"
									alt="Picture of <s:property value="employee.displayName" />" style="height: 150px;" />
							</td>
							<td style="vertical-align: middle; padding: 0px 5px;">
								<h2><s:property value="employee.displayName" /></h2>
								<s:property value="employee.title" /><br />
								<s:property value="employee.account.name" /><br />
							</td>
							<td style="vertical-align: middle; padding: 0px 5px;">
								<img src="ContractorLogo.action?id=<s:property value="employee.account.id"/>"
									style="height: 150px" alt="Logo" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<div class="cell">
						<table>
							<tr>
								<th>Age:</th>
								<td><s:property value="age" /> years old</td>
							</tr>
							<tr>
								<th>Works at:</th>
								<td>
									<s:iterator value="employee.employeeSites" id="sites" status="stat">
										<s:property value="#sites.operator.name" /><br />
									</s:iterator>
								</td>
							</tr>
							<tr>
								<th>Job Role<s:if test="roles.size() > 0">s</s:if>:</th>
								<td>
									<s:iterator value="employee.employeeRoles" id="role" status="stat">
										<s:property value="jobRole.name" /><s:if test="#stat.count < employee.employeeRoles.size()">, </s:if>
									</s:iterator>
								</td>
							</tr>
						</table>
					</div>
					<s:if test="employee.employeeRoles.size() > 0">
						<table class="report">
							<thead>
								<tr>
									<th>HSE Competency</th>
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
					<s:else>
						<div class="cell"><s:property value="employee.displayName" /> has no job roles.</div>
					</s:else>
				</td>
				<td>
					<div class="cell">
						<table>
							<tr>
								<th>Phone:</th>
								<td><s:property value="employee.phone" /></td>
							</tr>
							<tr>
								<th>Email:</th>
								<td><s:property value="employee.email" /></td>
							</tr>
							<tr>
								<th>Location:</th>
								<td><s:property value="employee.location" /></td>
							</tr>
						</table>
					</div>
					<div class="cell">
						<table>
							<tr>
								<td colspan="2"><h3><s:property value="employee.account.name" /></h3></td>
							</tr>
							<tr>
								<th>Address:</th>
								<td>
									<s:property value="employee.account.address" /><br />
									<s:property value="employee.account.city" />, <s:property value="employee.account.state.isoCode" /> <s:property value="employee.account.zip" /> 
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>
