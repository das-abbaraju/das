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
				<s:if test="filter.showCategory"><td>Category</td>
					<td>RootCategory</td>
				</s:if>
				<td>Contractor Type</td>
				<td>Operator</td>
				<td>Risk</td>
				<td>Tag</td>
				<td>Bid-Only</td>
				<s:if test="filter.showDependentAuditType">
					<td colspan="2">Dependent Audit</td>
				</s:if>
				<td>Question</td>
				<td>View</td>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data" status="stat">
				<tr>
					<td><s:if test="get('include')==1">Yes</s:if><s:else>No</s:else></td>					
					<td><s:property value="get('audit_type')"/></td>					
					<s:if test="filter.showCategory"><td><s:property value="get('category')"/></td>
						<td><s:if test="get('rootCategory')==1">Yes</s:if><s:else>No</s:else></td>
					</s:if>				
					<td><s:property value="get('con_type')"/></td>					
					<td><s:property value="get('operator')"/></td>		
					<td><s:property value="getRisk(get('risk'))"/></td>					
					<td><s:property value="get('tag')"/></td>					
					<td><s:property value="get('bid')"/></td>
					<s:if test="filter.showDependentAuditType">
						<td><s:property value="get('dependentAuditType')"/></td>
						<td><s:property value="get('dependentAuditStatus')"/></td>
					</s:if>		
					<td><s:property value="get('question')"/></td>	
					<td><a href="<s:property value="actionUrl"/><s:property value="get('id')"/><s:if test="filter.checkDate!=null">&date=<s:property value="filter.checkDate"/></s:if>" class="preview"></a></td>					
				</tr>
			</s:iterator>
		</tbody>
	</table>
	<div class="alphapaging"><s:property value="report.pageLinksWithDynamicForm"
		escape="false" /></div>
</s:else>
