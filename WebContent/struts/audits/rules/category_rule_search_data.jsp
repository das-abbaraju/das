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
				<td>Category</td>
				<td>Account Type</td>
				<td>Operator</td>
				<td>Risk</td>
				<td>Tag</td>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data" status="stat">
				<tr>
					<td><s:if test="get('include')==1">Yes</s:if><s:else>No</s:else></td>					
					<td><s:property value="get('audit_type')"/></td>					
					<td><s:property value="get('category')"/></td>					
					<td><s:property value="get('account_type')"/></td>					
					<td><s:property value="get('operator')"/></td>					
					<td><s:property value="getRisk(get('risk'))"/></td>					
					<td><s:property value="get('tag')"/></td>					
				</tr>
			</s:iterator>
		</tbody>
	</table>
	<div class="alphapaging"><s:property value="report.pageLinksWithDynamicForm"
		escape="false" /></div>
</s:else>
