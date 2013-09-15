<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<th></th>
		<th><a href="?orderBy=firstName,lastName"><s:text name="global.Employee" /></a></th>
		<s:if test="permissions.admin || permissions.operatorCorporate">
			<th><a href="?orderBy=name"><s:text name="global.CompanyName" /></a></th>
			<th><s:text name="global.Type" /></th>
		</s:if>
		<th><s:text name="Employee.title" /></th>
		<th><s:text name="Employee.location" /></th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="EmployeeDetail.action?employee=<s:property value="get('employeeID')" />">
				<s:property value="get('firstName')" /> <s:property value="get('lastName')" /></a></td>
			<s:if test="permissions.admin || permissions.operatorCorporate">
				<td>
					<s:if test="get('accountType') == 'Contractor' && (permissions.admin || isCanViewContractor(get('accountID')))">
						<a href="ContractorView.action?id=<s:property value="get('accountID')" />">
							<s:property value="get('name')" />
						</a>
					</s:if>
					<s:else>
						<s:property value="get('name')" />
					</s:else>
					<s:if test="get('dbaName').length() > 0"><br /><s:text name="ContractorAccount.dbaName.short" />: <s:property value="get('dbaName')" /></s:if>
				</td>
				<td><s:text name="global.%{get('accountType')}" /></td>
			</s:if>
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