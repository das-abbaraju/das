<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Report Tester</s:param>
</s:include>

<style type="text/css">
td.waiting {
	background-color: gray;
}
td.success {
	background-color: green;
}
td.fail {
	background-color: red;
}
</style>

<h3>Models</h3>

<table id="models">
	<thead>
	<tr>
		<th>Model Type</th>
		<th>Result</th>
	</tr>
	</thead>
	<s:iterator value="@com.picsauditing.report.models.ModelType@values()" var="modelType">
		<tr id="models-row-<s:property value="#modelType" />">
			<td><s:property value="#modelType"/></td>
			<td class="results waiting"><s:property value="#modelType"/></td>
		</tr>
	</s:iterator>
</table>

<h3>Reports</h3>

<table id="reports">
	<thead>
	<tr>
		<th>Report</th>
		<th>Result</th>
	</tr>
	</thead>
	<s:iterator value="reports">
		<tr id="reports-row-<s:property value="id" />">
			<td><a href="Report.action?report=<s:property value="id" />"><s:property value="name" /></a></td>
			<td class="results waiting"><s:property value="id" /></td>
		</tr>
	</s:iterator>
</table>
