<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

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
		<th><a href="?orderBy=firstName,lastName">Employee</a></th>
		<th><a href="?orderBy=name">Account Name</a></th>
		<th>Account Type</th>
		<th>Title</th>
		<th>Location</th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="EmployeeDetail.action?employee.id=<s:property value="get('employeeID')" />">
				<s:property value="get('firstName')" /> <s:property value="get('lastName')" /></a></td>
			<td>
				<s:if test="get('type') == 'Contractor' && (permissions.admin || canViewContractor(get('id')))">
					<a href="ContractorView.action?id=<s:property value="get('id')" />">
						<s:property value="get('name')" /></a>
				</s:if>
				<s:elseif test="get('type') == 'Operator' && (permissions.corporate || permissions.admin || permissions.accountIdString == get('id'))">
					<a href="FacilitiesEdit.action?id=<s:property value="get('id')" />">
						<s:property value="get('name')" /></a>
				</s:elseif>
				<s:else>
					<s:property value="get('name')" />
				</s:else>
				<s:if test="get('dbaName').length() > 0"><br />DBA Name: <s:property value="get('dbaName')" /></s:if>
			</td>
			<td><s:property value="get('type')" /></td>
			<td><s:property value="get('title')" /></td>
			<td><s:property value="get('location')" /></td>
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
