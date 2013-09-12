<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
	<div class="right">
		<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="javascript.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
			href="#" title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"><s:text name="global.Download" /></a></div>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
	<table class="report">
		<thead>
		<tr>
			<th></th>
			<th><a href="javascript:changeOrderBy('a.name,e.lastName,e.firstName');"><s:text name="global.Company" /></a></th>
			<th><a href="javascript:changeOrderBy('e.lastName,e.firstName,a.name');"><s:text name="global.Employee" /></a></th>
			<th><a href="javascript:changeOrderBy('centerName,test');"><s:text name="global.AssessmentCenter" /></a></th>
			<th><a href="javascript:changeOrderBy('test,a.name,e.lastName,e.firstName');"><s:text name="AssessmentTest" /></a></th>
			<th><s:text name="%{scope}.label.InEffect" /></th>
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
				<td><a href="EmployeeDetail.action?employee=<s:property value="get('employeeID')" />"><s:property value="get('lastName')" />, <s:property value="get('firstName')" /></a></td>
				<td><s:property value="get('centerName')" /></td>
				<td><s:property value="get('test')" /></td>
				<td class="center"><s:property value="get('inEffect')" /></td>
			</tr>
		</s:iterator>
		</tbody>
	</table>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:else>