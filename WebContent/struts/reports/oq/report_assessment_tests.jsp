<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title><s:property value="subHeading" /></title>
<s:include value="../reportHeader.jsp" />
</head>
<body>

<h1><s:property value="subHeading" /></h1>

<s:form id="form1">
	<s:hidden name="filter.ajax" value="false" />
	<s:hidden name="filter.destinationAction" value="ReportAssessmentTests" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="orderBy" />

<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<th></th>
		<th><a href="?orderBy=test,task,employee">Assessment Test</a></th>
		<th>Test Active</th>
		<th><a href="?orderBy=task,test,employee">Job Task</a></th>
		<th>Task Active</th>
		<th><a href="?orderBy=employee,q.qualified DESC,test,task">Employee</a></th>
		<th>Qualified</th>
		<th><a href="?orderBy=q.effectiveDate,test,task,employee">Qualification Date</a></th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td>
				<nobr>
					<s:property value="get('test')" />
					<a href="#" onclick="return false;" class="help cluetip"
						title="<s:property value="get('test')" />" 
						rel="#description_<s:property value="#stat.index + report.firstRowNumber" />"></a>
					<div id="description_<s:property value="#stat.index + report.firstRowNumber" />">
						<s:property value="get('description')" />
					</div>
				</nobr>
			</td>
			<td class="center"><s:property value="get('testActive')" /></td>
			<td><s:property value="get('task')" /></td>
			<td class="center"><s:property value="get('taskActive')" /></td>
			<td><a href="EmployeeDetail.action?employee.id=<s:property value="get('employeeID')" />">
				<s:property value="get('employee')" /></a></td>
			<td class="center"><s:property value="get('qualified')" /></td>
			<td class="center"><s:property value="get('qualEff')" /></td>
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
</s:form>

</body>
</html>
