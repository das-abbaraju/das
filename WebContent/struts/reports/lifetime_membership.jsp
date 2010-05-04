<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Lifetime Members</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Lifetime Members</h1>

<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<div id="report_data">
	<table class="report">
	<thead>
	<tr>
		<td></td>
	    <td>Name</td>
	    <td class="center">Status</td>
	    <td class="center">Paying<br>Facilities</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
		<td class="center"><s:property value="get('status')"/></td>
		<td class="center"><s:property value="get('payingFacilities')"/></td>
	</tr>
	</s:iterator>
</table>
</div>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
