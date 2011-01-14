<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>
	<div class="right">
		<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
			href="javascript: download('ReportAssessmentTests');" title="Download all <s:property value="report.allRows"/> results to a CSV file">Download</a></div>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
	<table class="report">
		<thead>
		<tr>
			<th></th>
			<th><a href="javascript: changeOrderBy('a.name,e.lastName,e.firstName');">Company</a></th>
			<th><a href="javascript: changeOrderBy('e.lastName,e.firstName,a.name');">Employee</a></th>
			<th><a href="javascript: changeOrderBy('centerName,test');">Assessment Center</a></th>
			<th><a href="javascript: changeOrderBy('test,a.name,e.lastName,e.firstName');">Assessment Test</a></th>
			<th>In Effect</th>
		</tr>
		</thead>
		<tbody>
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
				<td>
					<s:if test="get('accountType') == 'Contractor'">
						<a href="ContractorView.action?id=<s:property value="get('accountID')" />"><s:property value="get('name')" /></a>
					</s:if>
					<s:else>
						<s:property value="get('name')" />
					</s:else>
				</td>
				<td><a href="EmployeeDetail.action?employee.id=<s:property value="get('employeeID')" />"><s:property value="get('lastName')" />, <s:property value="get('firstName')" /></a></td>
				<td><s:property value="get('centerName')" /></td>
				<td><s:property value="get('test')" /></td>
				<td class="center"><s:property value="get('inEffect')" /></td>
			</tr>
		</s:iterator>
		</tbody>
	</table>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:else>