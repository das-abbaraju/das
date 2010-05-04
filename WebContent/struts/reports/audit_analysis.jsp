<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Audit Analysis</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Audit Analysis</h1>

<s:include value="auditanalysisfilters.jsp" />

<table class="report">
	<thead>
		<tr>
			<td>Month</td>
			<td>Created</td>
			<td>Submitted</td>
			<td>Closed</td>
		</tr>
	</thead>
	<s:iterator value="data">
		<tr>
			<td class="right">
				<s:property value="[0].get('label')" />
			</td>
			<td>
				<s:property value="[0].get('createdCount')" />
			</td>
			<td>
				<s:property value="[0].get('completeCount')" />			
			</td>
			<td>
				<s:property value="[0].get('closedCount')" />			
			</td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
