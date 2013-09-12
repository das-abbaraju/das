<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
function orderBy(orderBy) {
	$('#form1').find('input[name="orderBy"]').val(orderBy);
	$('#form1').submit();
}
</script>
</head>
<body>
<h1><s:text name="%{scope}.title" /></h1>
<s:include value="filters_employee.jsp"/>
<div class="right">
	<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ReportCompetencyByEmployee');"
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"><s:text name="global.Download" /></a>
</div>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<s:if test="data.size() > 0">
<table class="report" id="matrix">
	<thead>
		<tr>
			<th><a href="javascript: orderBy('name,lastName');"><s:text name="global.Company" /></a></th>
			<th><a href="javascript: orderBy('lastName,firstName');"><s:text name="global.Employee" /></a></th>
			<th><a href="javascript: orderBy('title');"><s:text name="Employee.title" /></a></th>
			<th><s:text name="%{scope}.label.JobRoles" /></th>
			<th><s:text name="%{scope}.label.Competency" /></th>
			<th><nobr><a href="javascript: orderBy('percent DESC');"><s:text name="%{scope}.label.Competency" /> %</a></nobr></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat" id="data">
			<tr>
				<td>
					<s:if test="#data.get('notWorksFor') == 0">
						<a href="ContractorView.action?id=<s:property value="#data.get('accountID')"/>"><s:property value="#data.get('name')" /></a>
					</s:if>
					<s:else>
						<s:property value="#data.get('name')" />
					</s:else>
				</td>
				<td><a href="EmployeeDetail.action?employee=<s:property value="#data.get('employeeID')"/>"><s:property value="#data.get('lastName')" />, <s:property value="#data.get('firstName')" /></a></td>
				<td><s:property value="get('title')"/></td>
				<td><s:property value="get('roles')"/></td>
				<td class="right"><s:property value="#data.get('skilled')" /> / <s:property value="#data.get('required')" /></td>
				<td class="right"><s:property value="#data.get('percent')" />%</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</s:if>

</body>
</html>
