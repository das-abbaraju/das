<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
</head>
<body>
<h1><s:text name="%{scope}.title" /></h1>

<s:include value="filters.jsp" />

<div id="report_data">
	<s:if test="report.allRows == 0">
		<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
	</s:if>
	<s:else>
		<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
		<table class="report">
			<thead>
				<tr>
					<th></th>
					<th><s:text name="global.ContractorName" /></th>
					<th>Parent Trade</th>
					<th>Child Trade</th>
					<th>View</th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="data" status="stat">
					<tr>
						<td class="right"><s:property value="#stat.count" /></td>
						<td><a href="ContractorView.action?id=<s:property value="get('id')" />"><s:property value="get('name')" /></a></td>
						<td><s:property value="get('parentName')" /></td>
						<td><s:property value="get('childName')" /></td>
						<td class="center"><a href="ContractorTrades.action?id=<s:property value="get('id')" />" class="preview"></a></td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
		<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
	</s:else>
</div>

</body>
</html>