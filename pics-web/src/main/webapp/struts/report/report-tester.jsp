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
	</tr>
	</thead>
	<s:iterator value="@com.picsauditing.report.models.ModelType@values()" var="modelType">
		<tr id="models-row-<s:property value="#modelType" />">
			<td><a href="ReportTester.action?modelType=<s:property value="#modelType"/>"><s:property value="#modelType"/></a></td>
		</tr>
	</s:iterator>
</table>
