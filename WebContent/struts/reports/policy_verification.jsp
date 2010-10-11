<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Policy Verification</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Policy Verification</h1>
<s:include value="filters.jsp" />
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="report">
	<thead>
	<tr>
		<td></td>
		<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
		<s:if test="filter.primaryInformation">
			<td>Contact</td>
		</s:if>
		<td>Policy</td>
		<td><a href="javascript: changeOrderBy('form1','statusChangedDate DESC');">Changed On</a></td>
		<td>Operators</td>
		<td></td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><s:property value="get('name')"/></td>
			<s:if test="filter.primaryInformation">
				<td>
					<s:property value="get('contactname')"/> <br />
					<s:property value="get('contactphone')"/> <br />
					<a href="mailto:<s:property value="get('contactemail')"/>"><s:property value="get('contactemail')"/></a> <br />
				</td>
			</s:if>
			<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:property value="get('auditName')"/></a></td>
			<td><s:date name="get('statusChangedDate')" format="M/d/yy"/></td>
			<td><s:property value="get('operatorCount')"/></td>
			<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>&policy=true" target="VERIFY">Open</a></td>
		</tr>
	</s:iterator>	
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>