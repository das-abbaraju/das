<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Assessment Results</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
</head>
<body>
<s:form method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
	<input type="submit" value="Generate Assessment Results" name="button" class="picsbutton positive" />
</s:form>
<br />
<s:if test="effective.size() > 0">
	<h3>Assessment Results Still In Effect</h3>
	<table class="report"><thead>
		<tr>
			<th>Assessment Center</th>
			<th>Qualification Type</th>
			<th>Qualification Method</th>
			<th>Test Description</th>
			<th>Employee</th>
			<th>Employer</th>
			<th>Remove</th>
		</tr>
	</thead><tbody>
		<s:iterator value="effective" id="result" status="stat">
			<tr>
				<td><s:property value="#result.assessmentTest.assessmentCenter.name" /></td>
				<td><s:property value="#result.assessmentTest.qualificationType" /></td>
				<td><s:property value="#result.assessmentTest.qualificationMethod" /></td>
				<td><s:property value="#result.assessmentTest.description" /></td>
				<td>
					<a href="ManageEmployees.action?employee.id=<s:property value="#result.employee.id" />">
						<s:property value="#result.employee.lastName" />, <s:property value="#result.employee.firstName" />
					</a>
				</td>
				<td>
					<s:if test="#result.employee.account.contractor">
						<a href="ContractorView.action?id=<s:property value="#result.employee.account.id" />"><s:property value="#result.employee.account.name" /></a>
					</s:if>
					<s:else>
						<a href="FacilitiesEdit.action?id=<s:property value="#result.employee.account.id" />"><s:property value="#result.employee.account.name" /></a>
					</s:else>
				</td>
				<td class="center"><a href="ManageAssessmentResults.action?button=Remove&resultID=<s:property value="#result.id" />" class="remove"></a></td>
			</tr>
		</s:iterator>
	</tbody></table>
	<br />
</s:if>
<s:if test="expired.size() > 0">
	<h3>Expired/Removed Assessment Results</h3>
	<table class="report"><thead>
		<tr>
			<th>Assessment Center</th>
			<th>Qualification Type</th>
			<th>Qualification Method</th>
			<th>Test Description</th>
			<th>Employee</th>
			<th>Employer</th>
		</tr>
	</thead><tbody>
		<s:iterator value="expired" id="result" status="stat">
			<tr>
				<td><s:property value="#result.assessmentTest.assessmentCenter.name" /></td>
				<td><s:property value="#result.assessmentTest.qualificationType" /></td>
				<td><s:property value="#result.assessmentTest.qualificationMethod" /></td>
				<td><s:property value="#result.assessmentTest.description" /></td>
				<td>
					<a href="ManageEmployees.action?employee.id=<s:property value="#result.employee.id" />">
						<s:property value="#result.employee.lastName" />, <s:property value="#result.employee.firstName" />
					</a>
				</td>
				<td>
					<s:if test="#result.employee.account.contractor">
						<a href="ContractorView.action?id=<s:property value="#result.employee.account.id" />"><s:property value="#result.employee.account.name" /></a>
					</s:if>
					<s:else>
						<a href="FacilitiesEdit.action?id=<s:property value="#result.employee.account.id" />"><s:property value="#result.employee.account.name" /></a>
					</s:else>
				</td>
			</tr>
		</s:iterator>
	</tbody></table>
</s:if>
</body>
</html>
