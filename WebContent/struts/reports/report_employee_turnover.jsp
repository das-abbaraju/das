<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Employee Turnover</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Employee Turnover</h1>

<div class="beta"></div>

<s:form id="form1">
	<s:hidden name="filter.ajax" value="false"/>
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
	    <th></th>
	    <th><a href="?orderBy=t.name">Account</a></th>
	    <th><a href="?orderBy=current DESC,t.name">Active Employees</a></th>
	    <th><a href="?orderBy=experience DESC,t.name">Average Experience (Months)</a></th>
	    <th>New / Terminated<br />Past 3 months</th>
	    <th>New / Terminated<br />Past 6 months</th>
	    <th>New / Terminated<br />Past year</th>
	</tr>
	</thead>
	<s:iterator value="data" status="stat" begin="%{report.firstRowNumber - 1}" end="%{(report.firstRowNumber + 98) < data.size() ? (report.firstRowNumber + 98) : data.size() - 1}">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a></td> 
			<td class="center">
				<s:if test="permissions.admin">
					<a href="ManageEmployees.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('current')"/></a>
				</s:if>
				<s:else>
					<s:property value="[0].get('current')"/>
				</s:else>
			</td>
			<td class="center"><s:property value="[0].get('experience')"/></td>
			<td class="center"><s:property value="[0].get('new3')"/> / <s:property value="[0].get('old3')"/></td>
			<td class="center"><s:property value="[0].get('new6')"/> / <s:property value="[0].get('old6')"/></td>
			<td class="center"><s:property value="[0].get('new12')"/> / <s:property value="[0].get('old12')"/></td>
		</tr>
	</s:iterator>
</table>
<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>


</body>
</html>
