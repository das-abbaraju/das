<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>PQF Verification</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>PQF Verification</h1>
<s:include value="filters.jsp" />
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="report">
	<thead>
	<tr>
		<td></td>
		<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
		<td><a href="javascript: changeOrderBy('form1','csr_name');">CSR</a></td>
		<td><a href="javascript: changeOrderBy('form1','ca1.completedDate');">PQF Completed Date</a></td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="VerifyView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
			<td><s:property value="get('csr_name')"/></td>
			<td><s:date name="get('completedDate')" format="M/d/yy" /></td>
		</tr>
	</s:iterator>	
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>