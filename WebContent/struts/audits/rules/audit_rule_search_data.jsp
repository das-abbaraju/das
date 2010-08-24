<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria.
	Please try again.</div>
</s:if>
<s:else>
	<div><s:property value="report.pageLinksWithDynamicForm"
		escape="false" /></div>
	<table class="report">
		<thead>
			<tr>
				<td>Include</td>
				<td>Audit Type</td>
				<s:if test="filter.showCategory"><td>Category</td></s:if>
				<td>Account Type</td>
				<td>Operator</td>
				<s:if test="filter.showDependentAuditType"><td>Dependent Audit Type</td></s:if>
				<s:if test="filter.showDependentAuditStatus"><td>Dependent Audit Status</td></s:if>
				<td>Risk</td>
				<td>Tag</td>
				<td>Bid-Only</td>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data" status="stat">
				<tr class="clickable" onclick="window.location='<s:property value="actionUrl"/><s:property value="get('id')"/>'">
					<td><s:if test="get('include')==1">Yes</s:if><s:else>No</s:else></td>					
					<td><s:property value="get('audit_type')"/></td>					
					<s:if test="filter.showCategory"><td><s:property value="get('category')"/></td></s:if>				
					<td><s:property value="get('account_type')"/></td>					
					<td><s:property value="get('operator')"/></td>		
					<s:if test="filter.showDependentAuditType"><td><s:property value="get('dependentAuditType')"/></td></s:if>
					<s:if test="filter.showDependentAuditStatus"><td><s:property value="get('dependentAuditStatus')"/></td></s:if>			
					<td><s:property value="getRisk(get('risk'))"/></td>					
					<td><s:property value="get('tag')"/></td>					
					<td><s:property value="get('bid')"/></td>					
				</tr>
			</s:iterator>
		</tbody>
	</table>
	<div class="alphapaging"><s:property value="report.pageLinksWithDynamicForm"
		escape="false" /></div>
</s:else>
