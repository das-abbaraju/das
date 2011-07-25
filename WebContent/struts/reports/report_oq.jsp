<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="ReportOQ.title" /></title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
$(function() {
	$('a.excel').live('click', function(e) {
		e.preventDefault();
		download('ReportOQ');
	});
});
</script>
</head>
<body>
<h1><s:text name="ReportOQ.title" /></h1>
<s:include value="filters_employee.jsp" />
<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
<div class="right">
	<a class="excel" href="#" target="_blank" title="<s:text name="javascript.DownloadAllRows"><s:param value="%{data.size}" /></s:text>">
		<s:text name="global.Download" />
	</a>
</div>
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
<table class="report">
	<thead>
		<tr>
			<th><s:text name="global.Company" /></th>
			<th><s:text name="ReportOQ.label.Project" /></th>
			<th><s:text name="ReportOQ.label.QualifiedEmployees" /></th>
			<th><s:text name="ReportOQ.label.TotalEmployees" /></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data">
			<tr>
				<td>
					<s:if test="get('isContractor') == 1">
						<a href="ContractorView.action?id=<s:property value="get('accountID')" />"><s:property value="get('name')" /></a>
					</s:if>
					<s:else>
						<s:property value="get('name')" />
					</s:else>
				</td>
				<td><s:property value="get('jsName')" /></td>
				<td class="right"><a href="ReportOQEmployees.action?filter.accountName=<s:property value="get('accountID')" />&filter.projects=<s:property value="get('jsID')" />"><s:property value="get('employeeCount')" /></a></td>
				<td class="right"><s:property value="get('employeeTotals')" /></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:else>
</body>
</html>
