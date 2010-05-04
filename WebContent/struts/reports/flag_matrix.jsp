<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title>Operator Flag Matrix</title>
<s:include value="reportHeader.jsp"/>
</head>
<body>

<a class="excel" href="OperatorFlagMatrixCSV.action" target="_BLANK">Download Report</a>

<table class="report">
	<thead>
		<tr>
			<th>Contractor</th>
			<s:iterator value="tableDisplay.columns" id="column">
				<th><s:property value="#column"/></th>
			</s:iterator>
		</tr>
	</thead>
		<s:iterator value="tableDisplay.rows" id="row">
			<tr>
				<td><a href="ContractorView.action?id=<s:property value="tableDisplay.rowIds.get(#row)"/>"
						rel="ContractorQuickAjax.action?id=<s:property value="tableDisplay.rowIds.get(#row)"/>"
						class="contractorQuick" title="<s:property value="#row"/>"><s:property value="#row"/></a></td>
				<s:iterator value="tableDisplay.columns" id="column">
					<td><s:property value="tableDisplay.getContentIcon(#row, #column)" escape="false"/></td>
				</s:iterator>
			</tr>
		</s:iterator>
</table>

</body>
</html>