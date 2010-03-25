<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title>Operator Flag Matrix</title>
<s:include value="reportHeader.jsp"/>
</head>
<body>

<table class="report">
	<thead>
		<tr>
			<th>Contractor</th>
			<s:iterator value="tableDisplay.headers" id="header">
				<th title="<s:property value="tableDisplay.headerHover.get(#header)"/>"><s:property value="#header"/></th>
			</s:iterator>
		</tr>
	</thead>
		<s:iterator value="tableDisplay.columns" id="column">
			<tr>
				<td><a href="ContractorView.action?id=<s:property value="tableDisplay.columnIds.get(#column)"/>"
						rel="ContractorQuickAjax.action?id=<s:property value="tableDisplay.columnIds.get(#column)"/>"
						class="contractorQuick" title="<s:property value="#column"/>"><s:property value="#column"/></a></td>
				<s:iterator value="tableDisplay.headers" id="header">
					<td><s:property value="tableDisplay.getContent(#column, #header)" escape="false"/></td>
				</s:iterator>
			</tr>
		</s:iterator>
</table>

</body>
</html>