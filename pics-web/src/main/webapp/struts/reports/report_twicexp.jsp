<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>TWIC Expiration</title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
</head>
<body>
<h1>TWIC Expiration Report</h1>
<s:include value="filters.jsp" /> 	
<div id="report_data">	
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>		
	<table class="report">
		<thead>
			<tr>
			<td></td>
			<td>Employee Name</td>
			<td>Title</td>
			<td>Contractor</td>
			<td>TWIC Expiration</td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property value="#stat.index + report.firstRowNumber" />.</td>
				<td><a href="EmployeeDetail.action?employee.id=<s:property value="get('id')"/>" ><s:property value="get('firstName')" /> <s:property value="get('lastName')" /></a></td>
				<td><s:property value="get('title')" /></td>
				<td><a href="ContractorView.action?id=<s:property value="get('aID')"/>" ><s:property value="get('name')" /></a></td>
				<s:if test="get('twicExpiration')!=null"><td><s:property value="get('twicExpiration')" /></td></s:if>
				<s:else><td>No TWIC listed</td></s:else>
			</tr>
		</s:iterator>
	</table>
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
</div>

</body>
</html>
