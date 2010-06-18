<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Manage Assessment Test Results</title>
<s:include value="../reportHeader.jsp" />
</head>
<body>

<h1><s:property value="center.name" /><span class="sub">Manage Assessment Test Results</span></h1>

<s:if test="permissions.admin">
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="AssessmentCenterEdit.action?id=<s:property value="center.id"/>"
		<s:if test="requestURI.contains('assessment_edit')">class="current"</s:if>>Edit</a></li>
	<li><a href="UsersManage.action?accountId=<s:property value="center.id"/>"
		<s:if test="requestURI.contains('users_manage')">class="current"</s:if>>Users</a></li>
	<li><a href="ManageAssessmentTestResults.action?id=<s:property value="center.id"/>"
		<s:if test="requestURI.contains('manage_assessment_test_results')">class="current"</s:if>>Manage Test Results</a></li>
	<!-- Will we need this?
	<li><a href="ContractorList.action?filter.status=Active&filter.status=Demo<s:property value="operatorIds"/>">Contractors</a></li>
	 -->
</ul>
</div>
</s:if>

<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Result ID</th>
			<th>Qualification Type</th>
			<th>Qualification Method</th>
			<th>Description</th>
			<th>Test ID</th>
			<th colspan="2">Employee</th>
			<th>Employee ID</th>
			<th>Company</th>
			<th>Company ID</th>
			<th>Qualification Date</th>
		</tr>
	</thead>
	<tbody>	
		<s:iterator value="staged" status="stat">
			<tr>
				<td><s:property value="#stat.count" /></td>
				<td><s:property value="resultID" /></td>
				<td><s:property value="qualificationType" /></td>
				<td><s:property value="qualificationMethod" /></td>
				<td><s:property value="description" /></td>
				<td><s:property value="testID" /></td>
				<td><s:property value="firstName" /></td>
				<td><s:property value="lastName" /></td>
				<td><s:property value="employeeID" /></td>
				<td><s:property value="companyName" /></td>
				<td><s:property value="companyID" /></td>
				<td><s:date name="qualificationDate" format="MM/dd/yyyy" /></td>
			</tr>
		</s:iterator>
		<s:if test="staged.size() == 0">
			<tr>
				<td colspan="12">No results found</td>
			</tr>
		</s:if>
	</tbody>
</table>

</body>
</html>