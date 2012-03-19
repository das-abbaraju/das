<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title><s:text name="OperatorFlagMatrix.title" /></title>
<s:include value="reportHeader.jsp"/>
</head>
<body>
<h1><s:property value="reportName"/></h1>
<s:text name="OperatorFlagMatrix.messageText" />
<br />
<a class="excel" href="OperatorFlagMatrixCSV.action?flagColor=<s:property value="flagColor"/>&category=<s:property value="category"/>" target="_BLANK"><s:text name="OperatorFlagMatrix.label.DownloadReport" /></a>
<table class="report">
	<thead>
		<tr>
			<th><s:text name="global.Contractor" /></th>
			<s:iterator value="tableDisplay.columns" id="column">
				<th><s:property value="#column"/></th>
			</s:iterator>
		</tr>
	</thead>
		<s:iterator value="tableDisplay.rows" id="row">
			<tr>
				<td><a href="ContractorView.action?id=<s:property value="tableDisplay.rowIds.get(#row)"/>"
						rel="ContractorQuick.action?id=<s:property value="tableDisplay.rowIds.get(#row)"/>"
						class="contractorQuick" title="<s:property value="#row"/>"><s:property value="#row"/></a></td>
				<s:iterator value="tableDisplay.columns" id="column">
					<td><s:property value="tableDisplay.getContentIcon(#row, #column)" escape="false"/></td>
				</s:iterator>
			</tr>
		</s:iterator>
</table>

</body>
</html>