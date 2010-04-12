<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Assessments</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
</head>
<body>
<h1>Assessment Results <s:if test="employee != null"><span class="sub"><s:property value="employee.firstName"/> <s:property value="employee.lastName"/></span></s:if></h1>

<s:if test="employee != null">
	<div id="internalnavcontainer">
		<ul id="navlist">
			<li><a href="EmployeeDetail.action?employee.id=<s:property value="employee.id" />">Edit</a></li>
			<li><a href="#">Assessments</a></li>
			<li><a href="#">Data Partner IDs</a></li>
		</ul>
	</div>
</s:if>
	
<s:include value="../actionMessages.jsp"/>

<s:if test="employee == null">
	<s:form method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
		<input type="submit" value="Generate Assessment Results" name="button" class="picsbutton positive" />
	</s:form>
	<br />
</s:if>
<s:if test="effective.size() > 0">
	<table class="report"><thead>
		<tr>
			<th>Assessment Center</th>
			<th>Qualification Type</th>
			<th>Qualification Method</th>
			<th>Test Description</th>
			<s:if test="employee == null">
				<th>Employee</th>
			</s:if>
			<s:if test="canEdit">
				<th>Remove</th>
			</s:if>
		</tr>
	</thead><tbody>
		<s:iterator value="effective" id="result" status="stat">
			<tr>
				<td><s:property value="#result.assessmentTest.assessmentCenter.name" /></td>
				<td><s:property value="#result.assessmentTest.qualificationType" /></td>
				<td><s:property value="#result.assessmentTest.qualificationMethod" /></td>
				<td><s:property value="#result.assessmentTest.description" /></td>
				<s:if test="employeeID == 0">
					<td>
						<a href="EmployeeDetail.action?employee.id=<s:property value="#result.employee.id" />">
							<s:property value="#result.employee.lastName" />, <s:property value="#result.employee.firstName" />
						</a>
					</td>
				</s:if>
				<s:if test="canEdit">
					<td class="center">
						<a href="ManageAssessmentResults.action?button=Remove&resultID=<s:property value="#result.id" />" class="remove"></a>
					</td>
				</s:if>
			</tr>
		</s:iterator>
	</tbody></table>
	<br />
</s:if>
<s:else>
	<div class="info">This employee has no assessments associated or in effect.</div>
</s:else>

<s:if test="expired.size() > 0">
	<h3>Expired/Removed</h3>
	<table class="report"><thead>
		<tr>
			<th>Assessment Center</th>
			<th>Qualification Type</th>
			<th>Qualification Method</th>
			<th>Test Description</th>
			<s:if test="employee == null">
				<th>Employee</th>
			</s:if>
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
			</tr>
		</s:iterator>
	</tbody></table>
</s:if>
</body>
</html>
